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

/**
 * AutoOpMode - Autonomous operation mode for the robot.
 * This OpMode implements a state machine for autonomous navigation and scoring:
 * State 0: Start - Move from starting position to scanning position
 * State 1: Scan - Wait for robot to arrive, then scan artifact pattern with vision
 * State 2: Navigate - Move to scoring position with scanned pattern data
 * State 3: Complete - Wait for arrival, then stop (no further actions)
 * Uses Pedro Pathing library for advanced path following with Bezier curves.
 */
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

    public enum State {
        START_TO_SCORE,
        SCORE_TO_PICKUP,
        PICKUP_BALLS,
        PICKUP_TO_SCORE,
        PARKING,
        DONE
    }

    private State pathState;

    /**
     * Changes the current state of the autonomous state machine.
     * Also resets the path timer to measure duration of the new state.
     *
     * @param pState The new state number to transition to
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
        autonomousPathUpdate();
        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("pattern", artifactPattern);
        telemetry.update();
    }

    /**
     * Initialization method - called once when "INIT" is pressed.
     * Sets up timers, path follower, hardware, and pre-builds navigation paths.
     */
    @Override
    public void init() {
        // Initialize timing systems
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        // Set up path following system with robot's hardware configuration
        follower = Constants.createFollower(hardwareMap);
        pathBuilder = new PathBuilder(follower);
        follower.setStartingPose(startPose);

        hardware.init(hardwareMap, telemetry);

        startToScoreChain = pathBuilder
            .addPath(new BezierLine(startPose, scanPose))
            .setConstantHeadingInterpolation(Math.toRadians(90))
            .addParametricCallback(1.0, () -> {
                scanned = true;
            })
            .addPath(new BezierCurve(scanPose, scorePose))
            .setLinearHeadingInterpolation(scanPose.getHeading(), scorePose.getHeading())
            .build();

        pickupAndScoreChain = pathBuilder
            .addPath(new BezierCurve(scorePose, pickupPose1))
            .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPose1.getHeading())
            .addPath(new BezierLine(pickupPose1, pickupEndPose1))
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addParametricCallback(0.3333, intakeBall())
            .addParametricCallback(0.6666, intakeBall())
            .addParametricCallback(0.9999, intakeBall())
            .addPath(new BezierCurve(pickupEndPose1, scorePose))
            .setLinearHeadingInterpolation(pickupEndPose1.getHeading(), scorePose.getHeading())

            .addPath(new BezierCurve(scorePose, pickupPose2))
            .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPose2.getHeading())
            .addPath(new BezierLine(pickupPose2, pickupEndPose2))
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addParametricCallback(0.3333, intakeBall())
            .addParametricCallback(0.6666, intakeBall())
            .addParametricCallback(0.9999, intakeBall())
            .addPath(new BezierCurve(pickupPose2, scorePose))
            .setLinearHeadingInterpolation(pickupEndPose2.getHeading(), scorePose.getHeading())

            .addPath(new BezierCurve(scorePose, pickupPose3))
            .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPose3.getHeading())
            .addPath(new BezierLine(pickupPose3, pickupEndPose3))
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addParametricCallback(0.3333, intakeBall())
            .addParametricCallback(0.6666, intakeBall())
            .addParametricCallback(0.9999, intakeBall())
            .addPath(new BezierCurve(pickupPose3, scorePose))
            .setLinearHeadingInterpolation(pickupEndPose3.getHeading(), scorePose.getHeading())

            .build();
        //TODO make this less sus and add lazysusan code and launching code
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
     * Resets timers and starts the state machine at state 0.
     */
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(State.START_TO_SCORE);  // Begin with first state
    }

    /**
     * Called once when autonomous ends.
     * Everything stops automatically, so no manual cleanup needed.
     * r o b o t i c s
     **/
    @Override
    public void stop() {
    }

    private Runnable intakeBall() {
        return () -> {
            follower.pausePathFollowing();
            hardware.storage.enableIntake();
            Timer timer = new Timer();
            timer.resetTimer();

            while (actionTimer.getElapsedTime() < 1000) {
                //waiting
            }

            hardware.storage.disableIntake();
            follower.resumePathFollowing();
        };
    }

    public void autonomousPathUpdate() {
        switch (getPathState()) {

            case START_TO_SCORE:
                if (!pathStarted && !follower.isBusy()) {
                    follower.followPath(startToScoreChain, false);
                    pathStarted = true;
                }
                else if (pathStarted && !follower.isBusy()){
                    setPathState(State.SCORE_TO_PICKUP);
                    pathStarted = false;
                }
                break;

            case PICKUP_BALLS:
                if (!pathStarted && !follower.isBusy()) {
                    follower.followPath(pickupAndScoreChain);
                    pathStarted = true;
                }
                else if (pathStarted && !follower.isBusy()) {
                    setPathState(State.PARKING);
                    pathStarted = false;
                }
                break;

            case PARKING:
                if(!pathStarted && !follower.isBusy()) {
                    follower.followPath(new Path(new BezierLine(follower.getPose(), endPose)));
                    pathStarted = true;
                }
                else if (pathStarted && !follower.isBusy()) {
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
