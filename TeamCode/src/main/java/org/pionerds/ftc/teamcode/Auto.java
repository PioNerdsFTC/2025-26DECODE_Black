package org.pionerds.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;
import org.pionerds.ftc.teamcode.Pathfinding.Constants;
import org.pionerds.ftc.teamcode.Utils.DataStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Auto {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose;
    private final Pose endPose;
    private final Pose scanPose = new Pose(56, 80, Math.toRadians(90));
    private final Pose scorePose = new Pose(48, 110, Math.toRadians(144.046));
    private final Pose pickupPose1 = new Pose(48, 84, Math.toRadians(0));
    private final Pose pickupPose2 = new Pose(48, 60, Math.toRadians(0));
    private final Pose pickupPose3 = new Pose(48, 36, Math.toRadians(0));
    private final Pose pickupEndPose1 = new Pose(32, 84, Math.toRadians(0));
    private final Pose pickupEndPose2 = new Pose(32, 60, Math.toRadians(0));
    private final Pose pickupEndPose3 = new Pose(32, 36, Math.toRadians(0));
    private final double pileYCoordOffset = 24;
    private final Telemetry telemetry;
    private final HardwareMap hardwareMap;
  
    private boolean scanned = false;
    private boolean pathStarted = false;
    private String artifactPattern = "No scan attempt yet";

    // Count intake enable/disable calls for debugging/verification
    private int intakeEnableCount = 0;
    private int intakeDisableCount = 0;
    private int getIntakeTotalToggles() { return intakeEnableCount + intakeDisableCount; }

    final Hardware hardware = new Hardware();

    private PathBuilder pathBuilder;
    private PathChain startToScoreChain;
    private List<PathChain> pickupAndScoreChains;
    private int pickupCycle = 0;


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

    public Auto(Pose startPose, Pose endPose, Telemetry telemetry, HardwareMap hardwareMap) {
        this.startPose = startPose;
        this.endPose = endPose;
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
    }

    /**
     * Main loop - runs repeatedly during autonomous period.
     * Updates path following and executes state machine logic.
     */
    public void loop() {
        if (!scanned) {
            artifactPattern = Arrays.toString(hardware.vision.getArtifactPattern());
        }

        // Process state machine and path transitions
        autonomousPathUpdate();


        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", this.getPathState().toString());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("pattern", artifactPattern);
        telemetry.addData("intake enables (spins)", intakeEnableCount);
        telemetry.addData("intake disables", intakeDisableCount);
        telemetry.addData("intake total toggles", getIntakeTotalToggles());
        telemetry.update();
    }


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
        pickupAndScoreChains = new ArrayList<>();
        follower.setStartingPose(startPose);

        hardware.init(hardwareMap, telemetry);

        startToScoreChain = pathBuilder
            .addPath(new BezierLine(startPose, scanPose))
            .setConstantHeadingInterpolation(Math.toRadians(90))
            .addParametricCallback(0.9, () -> {
                scanned = true;
            })
            .addPath(new BezierCurve(scanPose, scorePose))
            .setLinearHeadingInterpolation(scanPose.getHeading(), scorePose.getHeading())
            .build();

        // Build pickup/score chain
        for (int i = 0; i < pickupPoseList.length; i++) {
            PathBuilder singlePickupPathBuilder = new PathBuilder(follower);
            singlePickupPathBuilder
                .addPath(new BezierCurve(scorePose, pickupPoseList[i]))
                .addParametricCallback(0.9, () -> {hardware.storage.enableIntake(); intakeEnableCount++;})
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPoseList[i].getHeading())

                .addPath(new BezierLine(pickupPoseList[i], pickupEndPoseList[i]))
                // moved disableIntake() off this segment so it doesn't stop during the pickup->end line
                .setConstantHeadingInterpolation(Math.toRadians(0))

                .addPath(new BezierCurve(pickupEndPoseList[i], scorePose))
                // disable intake on the return curve (early in the return) so each pickup leg does enable->disable exactly once
                .addParametricCallback(0.1, () -> {hardware.storage.disableIntake(); intakeDisableCount++;})
                .addParametricCallback(0.9, () -> {
                    try {
                        launchBalls();
                    } catch (InterruptedException e) {
                        telemetry.addData("Error", "launchBalls was interrupted");
                        telemetry.update();
                        Thread.currentThread().interrupt();
                    }
                })
                .setLinearHeadingInterpolation(pickupEndPoseList[i].getHeading(), scorePose.getHeading());
            pickupAndScoreChains.add(singlePickupPathBuilder.build());
        }
    }

    public void launchBalls() throws InterruptedException {
        follower.pausePathFollowing();

        hardware.storage.disableFeeder();
        hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
        hardware.launcher.setLauncherPower(0.5);
        Thread.sleep(1000);
        hardware.storage.enableFeeder();
        Thread.sleep(2000);
        hardware.storage.disableFeeder();

        hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
        hardware.launcher.setLauncherPower(0.5);
        Thread.sleep(1000);
        hardware.storage.enableFeeder();
        Thread.sleep(2000);
        hardware.storage.disableFeeder();

        hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
        hardware.launcher.setLauncherPower(0.5);
        Thread.sleep(1000);
        hardware.storage.enableFeeder();
        Thread.sleep(2000);
        hardware.storage.disableFeeder();

        follower.resumePathFollowing();
    }
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(State.START_TO_SCORE);  // Begin with first state
    }

    public void autonomousPathUpdate() {
        switch (getPathState()) {

            case START_TO_SCORE:
                if (!pathStarted && !follower.isBusy()) {
                    follower.followPath(startToScoreChain, false);
                    pathStarted = true;
                }
                else if (pathStarted && !follower.isBusy()) {
                    setPathState(State.PICKUP_BALLS);
                    pathStarted = false;
                }
                break;

            case PICKUP_BALLS:
                if (!pathStarted && !follower.isBusy()) {
                    if (pickupCycle < pickupAndScoreChains.size()) {
                        follower.followPath(pickupAndScoreChains.get(pickupCycle));
                        pathStarted = true;
                    } else {
                        // All pickup cycles are done, move to parking
                        setPathState(State.PARKING);
                    }
                }
                else if (pathStarted && !follower.isBusy()) {
                    // Current pickup cycle finished, prepare for the next one
                    pickupCycle++;
                    pathStarted = false;
                }
                break;

            case PARKING:
                //TODO: flush enemy gate before parking
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
