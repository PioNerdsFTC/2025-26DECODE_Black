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
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.ImuOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.pionerds.ftc.teamcode.Hardware.Gyro;
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
    private final Pose scorePose;
    private final Pose scanPose;
    private final Pose pickupPose1;
    private final Pose pickupPose2;
    private final Pose pickupPose3;
    private final Pose pickupEndPose1;
    private final Pose pickupEndPose2;
    private final Pose pickupEndPose3;
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

    public Auto(Pose startPose, Pose scorePose, Pose endPose, Boolean red, Telemetry telemetry, HardwareMap hardwareMap) {
        this.startPose = startPose;
        this.scorePose = scorePose;
        this.endPose = endPose;
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;

        double pickupX = 48.0;
        double pickupEndX = 32.0;
        double scanX = 56.0;
        double pickupAngle = 180;

        if(red){
            pickupX = 144.0 - 48.0;
            pickupEndX = 144.0 - 32.0;
            scanX = 144.0 - 56.0;
            pickupAngle = 0;
        }

        this.scanPose = new Pose(scanX, 80, Math.toRadians(90));
        this.pickupPose1 = new Pose(pickupX, 84, Math.toRadians(pickupAngle));
        this.pickupPose2 = new Pose(pickupX, 60, Math.toRadians(pickupAngle));
        this.pickupPose3 = new Pose(pickupX, 36, Math.toRadians(pickupAngle));
        this.pickupEndPose1 = new Pose(pickupEndX, 84, Math.toRadians(pickupAngle));
        this.pickupEndPose2 = new Pose(pickupEndX, 60, Math.toRadians(pickupAngle));
        this.pickupEndPose3 = new Pose(pickupEndX, 36, Math.toRadians(pickupAngle));
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
        follower.setPose(follower.getPose().setHeading(hardwareMap.gyroSensor.get("imu").getHeading()));

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
//                .addParametricCallback(0.9, () -> {hardware.storage.enableIntake(); intakeEnableCount++;})
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPoseList[i].getHeading())

                .addPath(new BezierLine(pickupPoseList[i], pickupEndPoseList[i]))
                // moved disableIntake() off this segment so it doesn't stop during the pickup->end line
                .setConstantHeadingInterpolation(Math.toRadians(0))

                .addPath(new BezierCurve(pickupEndPoseList[i], scorePose))
                // disable intake on the return curve (early in the return) so each pickup leg does enable->disable exactly once
//                .addParametricCallback(0.1, () -> {hardware.storage.disableIntake(); intakeDisableCount++;})
//                .addParametricCallback(0.9, () -> {
//                    try {
//                        launchBalls();
//                    } catch (InterruptedException e) {
//                        telemetry.addData("Error", "launchBalls was interrupted");
//                        telemetry.update();
//                        Thread.currentThread().interrupt();
//                    }
//                })
                .setLinearHeadingInterpolation(pickupEndPoseList[i].getHeading(), scorePose.getHeading());
            pickupAndScoreChains.add(singlePickupPathBuilder.build());
        }
    }

    public void launchBalls() throws InterruptedException {
        follower.pausePathFollowing();

        hardware.storage.disableFeeder();
        hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
        hardware.launcher.setLauncherVelocity(1000);
        hardware.sleep(3000);
        hardware.storage.enableFeeder();
        hardware.sleep(5000);
        hardware.storage.disableFeeder();

        hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
        hardware.sleep(3000);
        hardware.storage.enableFeeder();
        hardware.sleep(5000);
        hardware.storage.disableFeeder();

        hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
        hardware.sleep(3000);
        hardware.storage.enableFeeder();
        hardware.sleep(5000);
        hardware.storage.disableFeeder();
        hardware.launcher.stopLaunchers();

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

    public void pureInit(HardwareMap hardwaremap, Telemetry telemetry) {
        hardware.init(hardwaremap,telemetry);
        hardware.sleep(10000);
        DataStorage.storeAllAngles(hardware.gyro.getAngles());
    }
}