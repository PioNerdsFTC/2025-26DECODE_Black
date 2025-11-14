package org.pionerds.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Pathfinding.Constants;
import org.pionerds.ftc.teamcode.Utils.DataStorage;

import java.util.Arrays;

@Autonomous(name = "AutoOpMode", group = "Examples")
public class AutoOpMode extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose = new Pose(56, Constants.localizerConstants.robot_Width / 2, Math.toRadians(90)); // Start Pose of our robot.
    private final Pose scanPose = new Pose(56, 80, Math.toRadians(90));
    private final Pose scorePose = new Pose(48, 110, Math.toRadians(144.046));
    private final Pose pickupPose1 = new Pose(48, 84, Math.toRadians(180));
    private final Pose pickupPose2 = new Pose(48, 60, Math.toRadians(180));
    private final Pose pickupPose3 = new Pose(48, 36, Math.toRadians(180));
    private final Pose pickupEndPose1 = new Pose(32, 84, Math.toRadians(180));
    private final Pose pickupEndPose2 = new Pose(32, 60, Math.toRadians(180));
    private final Pose pickupEndPose3 = new Pose(32, 36, Math.toRadians(180));
    private final Pose endPose = new Pose(38.75, 33.25, Math.toRadians(180));

    private boolean scanned = false;
    private boolean pathStarted = false;
    private String artifactPattern = "No scan attempt yet";

    final Hardware hardware = new Hardware();

    private PathBuilder pathBuilder;
    private PathChain startToScoreChain;
    private PathChain pickupAndScoreChain;

    // NON-BLOCKING intake state (Approach 1)
    private volatile boolean waitingForIntake = false;
    private volatile long intakeStartMs = 0;
    private static final long INTAKE_MS = 1000L; // intake duration in ms

    public enum State {
        START_TO_SCORE,
        PICKUP_BALLS,
        PARKING,
        DONE
    }

    private State pathState;

    /**
     * Changes the current state of the autonomous state machine.
     * Also resets the path timer to measure duration of the new state.
     *
     * @param pState The new State to transition to
     */
    public void setPathState(State pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /**
     * Main loop - runs repeatedly during autonomous period.
     * Updates path following and executes state machine logic.
     */
    @Override
    public void loop() {
        if (!scanned) {
            artifactPattern = Arrays.toString(hardware.vision.getArtifactPattern());
        }

        // Process state machine and path transitions
        autonomousPathUpdate();

        // Poll non-blocking intake waiting state and finish when elapsed
        handleIntakeWaiting();

        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", this.getPathState().toString());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("pattern", artifactPattern);
        telemetry.addData("waitingForIntake", waitingForIntake);
        telemetry.update();
    }

    /**
     * Initialization method - called once when "INIT" is pressed.
     */
    @Override
    public void init() {
        // Initialize timing systems
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        Pose[] pickupPoseList = {pickupPose1, pickupPose2, pickupPose3};
        Pose[] pickupEndPoseList = {pickupEndPose1, pickupEndPose2, pickupEndPose3};

        // Set up path following system with robot's hardware configuration
        follower = Constants.createFollower(hardwareMap);
        pathBuilder = new PathBuilder(follower);
        follower.setStartingPose(startPose);

        hardware.init(hardwareMap, telemetry);

        startToScoreChain = pathBuilder
            .addPath(new BezierLine(startPose, scanPose))
            .setConstantHeadingInterpolation(Math.toRadians(90))
            .addParametricCallback(100, () -> {
                scanned = true;
            })
            .addPath(new BezierCurve(scanPose, scorePose))
            .setLinearHeadingInterpolation(scanPose.getHeading(), scorePose.getHeading())
            .build();

        // Build pickup/score chain. Reuse builder but ensure correct ordering of callbacks
        for (int i = 0; i < pickupPoseList.length; i++) {
            pathBuilder
                .addPath(new BezierCurve(scorePose, pickupPoseList[i]))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPoseList[i].getHeading())

                .addPath(new BezierLine(pickupPoseList[i], pickupEndPoseList[i]))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                // NON-BLOCKING: add parametric callbacks that set up a pause + intake start
                .addParametricCallback(1.0/3.0, intakeBall())
                .addParametricCallback(2.0/3.0, intakeBall())
                .addParametricCallback(1.0, intakeBall())

                .addPath(new BezierCurve(pickupEndPoseList[i], scorePose))
                .setLinearHeadingInterpolation(pickupEndPoseList[i].getHeading(), scorePose.getHeading());
        }
        pickupAndScoreChain = pathBuilder.build();
        //TODO add lazy-susan code and launching code
    }

    /**
     * Called continuously after init while waiting for start.
     * Currently unused but required by OpMode structure.
     */
    @Override
    public void init_loop() {
    }

    /**
     * Called once when "START" is pressed to begin autonomous.
     */
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(State.START_TO_SCORE);  // Begin with first state
    }

    /**
     * Called once when autonomous ends.
     * Everything stops automatically, so no manual cleanup needed.
     **/
    @Override
    public void stop() {
    }

    /**
     * NON-BLOCKING intake callback factory.
     * This turns on intake, pauses following, records start time, and returns immediately.
     * The actual wait and resume happens on the main loop via handleIntakeWaiting().
     */
    private Runnable intakeBall() {
        return () -> {
            // If already waiting, do nothing (protect against multiple triggers)
            if (waitingForIntake) return;

            // Mark that we are intentionally pausing to intake
            waitingForIntake = true;

            // Pause follower (this sets isBusy = false inside follower)
            follower.pausePathFollowing();

            // Turn on intake immediately (safe on OpMode thread)
            hardware.storage.enableIntake();

            // Record start time and return: do NOT block here
            intakeStartMs = System.currentTimeMillis();
        };
    }

    /**
     * Polling helper called every loop to finish the intake pause when the timeout elapses.
     */
    private void handleIntakeWaiting() {
        if (!waitingForIntake) return;

        long now = System.currentTimeMillis();
        if (now - intakeStartMs >= INTAKE_MS) {
            // Stop intake and resume following
            hardware.storage.disableIntake();

            // Clear waiting flag before resuming to prevent state machine races
            waitingForIntake = false;

            // Resume follower (will restore isBusy etc. inside follower)
            follower.resumePathFollowing();
        }
    }

    public void autonomousPathUpdate() {
        switch (getPathState()) {

            case START_TO_SCORE:
                if (!pathStarted && !follower.isBusy()) {
                    follower.followPath(startToScoreChain, false);
                    pathStarted = true;
                }
                // ignore deliberate pauses for intake
                else if (pathStarted && !follower.isBusy() && !waitingForIntake) {
                    setPathState(State.PICKUP_BALLS);
                    pathStarted = false;
                }
                break;

            case PICKUP_BALLS:
                if (!pathStarted && !follower.isBusy()) {
                    follower.followPath(pickupAndScoreChain);
                    pathStarted = true;
                }
                else if (pathStarted && !follower.isBusy() && !waitingForIntake) {
                    setPathState(State.PARKING);
                    pathStarted = false;
                }
                break;

            case PARKING:
                if(!pathStarted && !follower.isBusy()) {
                    follower.followPath(new Path(new BezierLine(follower.getPose(), endPose)));
                    pathStarted = true;
                }
                else if (pathStarted && !follower.isBusy() && !waitingForIntake) {
                    setPathState(State.DONE);
                    pathStarted = false;
                }
                break;

            case DONE:
                if(!follower.isBusy()) {
                    DataStorage.storeAngle(1, follower.getPose().getHeading());
                }
                break;
        }
    }

    private State getPathState() {
        return pathState;
    }
}