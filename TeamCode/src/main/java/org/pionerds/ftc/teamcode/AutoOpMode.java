package org.pionerds.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Pathfinding.Constants;

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
    private Pose pickupPose = new Pose(48, 84, Math.toRadians(180));
    private final Pose endPose = new Pose(38.75, 33.25, Math.toRadians(180));

    private boolean scanned = false;
    private int pickupCycle = 0;

    private String artifactPattern = "No scan attempt yet";

    final Hardware hardware = new Hardware();

    // State machine variable - tracks which autonomous phase we're in
    private int pathState;

    private PathBuilder pathBuilder;
    private PathChain startToScoreChain;
    private PathChain scoreToPickupChain;
    private PathChain pickupToScoreChain;


    /**
     * Changes the current state of the autonomous state machine.
     * Also resets the path timer to measure duration of the new state.
     *
     * @param pState The new state number to transition to
     */
    public void setPathState(int pState) {
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
        telemetry.addData("heading", follower.getPose().getHeading());
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
            .addPath(new BezierCurve(scanPose, scorePose))
            .addParametricCallback(80, () -> {
                scanned = true;
            })
            .setLinearHeadingInterpolation(scanPose.getHeading(), scorePose.getHeading())
            .build();

        pickupToScoreChain = pathBuilder
            .addPath(new BezierCurve(pickupPose, scorePose))
            .setLinearHeadingInterpolation(pickupPose.getHeading(), scorePose.getHeading())
            .build();
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
        setPathState(0);  // Begin with first state
    }

    /**
     * Called once when autonomous ends.
     * Everything stops automatically, so no manual cleanup needed.
     * r o b o t i c s
     **/
    @Override
    public void stop() {
    }

    private void updatePickupPose(int cycle) {
        pickupPose = pickupPose.withY(pickupPose.getY() - (24 * cycle));

        scoreToPickupChain = pathBuilder
            .addPath(new BezierCurve(scorePose, pickupPose))
            .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPose.getHeading())
            .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {

            case 0:
                follower.followPath(startToScoreChain, false);
                if (!follower.isBusy()) {
                    setPathState(1);
                }
                break;

            case 1:
                if (!follower.isBusy()) {
                    updatePickupPose(pickupCycle);
                    follower.followPath(scoreToPickupChain, false);
                    setPathState(2);
                }
                break;

            case 2:
                if (pickupCycle == 2 && !follower.isBusy()) {
                    setPathState(67);
                } else if (!follower.isBusy()) {
                    follower.followPath(pickupToScoreChain, false);
                    pickupCycle++;
                    setPathState(1);
                }
                break;

            case 67:
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
                break;
        }
    }
}
