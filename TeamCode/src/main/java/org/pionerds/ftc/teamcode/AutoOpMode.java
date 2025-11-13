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

    // Path following system from Pedro Pathing library
    private Follower follower;

    // Timers for tracking durations and timeouts
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose = new Pose(56, Constants.localizerConstants.robot_Width / 2, Math.toRadians(90)); // Start Pose of our robot.
    private final Pose scanPose = new Pose(56, 80, Math.toRadians(90));
    private final Pose scorePose = new Pose(48, 110, Math.toRadians(144.046));
    private final Pose pickupPose = new Pose(48, 84, Math.toRadians(180));
    private final Pose endPose = new Pose(38.75, 33.25, Math.toRadians(180));

    private boolean scanned = false;
    private int pickupCycle = 0;

    private String artifactPattern = "No scan attempt yet";

    final Hardware hardware = new Hardware();

    private PathBuilder pathBuilder;
    private PathChain startToScoreChain;
    private Path pickupToScore;

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
        telemetry.addData("pickup cycle", pickupCycle);
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

        pickupToScore = new Path(new BezierCurve(pickupPose, scorePose));
        pickupToScore.setLinearHeadingInterpolation(pickupPose.getHeading(), scorePose.getHeading());
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

    private PathChain updatePickupPose(int cycle) {

        final double pileYCoordOffset = 24;

        return(pathBuilder
            .addPath(new BezierCurve(scorePose, pickupPose.withY(pickupPose.getY() - (pileYCoordOffset * cycle))))
            .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPose.getHeading())
            .build()
        );
    }

    private PathChain nextBallPath(int ball){

        final double ballXCoordOffset = 5;
        final Pose currentPose = follower.getPose();

        return(pathBuilder
            .addPath(new BezierLine(currentPose, currentPose.withX(currentPose.getX() - (ballXCoordOffset * ball))))
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build()
        );
    }

    public void autonomousPathUpdate() {
        switch (getPathState()) {

            case START_TO_SCORE:
                if (!follower.isBusy()) {
                    follower.followPath(startToScoreChain, false);
                    setPathState(State.SCORE_TO_PICKUP);
                }
                break;

            case SCORE_TO_PICKUP:

                break;

            case PICKUP_BALLS:

                break;

            case PICKUP_TO_SCORE:
                if(!follower.isBusy()) {
                    follower.followPath(new Path(new BezierLine(follower.getPose(), scorePose)));
                    setPathState(State.PARKING);
                }
                break;

            case PARKING:
                if(!follower.isBusy()) {
                    follower.followPath(new Path(new BezierLine(follower.getPose(), endPose)));
                    setPathState(State.DONE);
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
