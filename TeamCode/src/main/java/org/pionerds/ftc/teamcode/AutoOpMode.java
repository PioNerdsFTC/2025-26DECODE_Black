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

@Autonomous(name = "AutoOpMode", group = "Examples")
public class AutoOpMode extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose = new Pose(56, Constants.localizerConstants.robot_Width/2, Math.toRadians(90)); // Start Pose of our robot.
    private final Pose scanPose = new Pose(56, 80, Math.toRadians(90));
    private final Pose scorePose = new Pose(48, 110, Math.toRadians(144.046));
    private Pose pickupPose = new Pose(48, 84, Math.toRadians(180));
    private final Pose endPose = new Pose(38.75, 33.25, Math.toRadians(180));

    private boolean scanned = false;
    private int pickupCycle = 0;

    private String artifactPattern = new String("No scan attempt yet");

    final Hardware hardware = new Hardware();

    private int pathState;

    private PathBuilder pathBuilder;
    private PathChain startToScoreChain;
    private PathChain scoreToPickupChain;
    private PathChain pickupToScoreChain;


    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/
    @Override
    public void loop() {
        if(!scanned){
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
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        follower = Constants.createFollower(hardwareMap);
        pathBuilder = new PathBuilder(follower);
        follower.setStartingPose(startPose);

        hardware.init(hardwareMap, telemetry, hardware.elapsedTime);

        startToScoreChain = pathBuilder
            .addPath(new BezierLine(startPose, scanPose))
            .setConstantHeadingInterpolation(Math.toRadians(90))
            .addPath(new BezierCurve(scanPose, scorePose))
            .addParametricCallback(80, () -> {scanned=true;})
            .setLinearHeadingInterpolation(scanPose.getHeading(), scorePose.getHeading())
            .build();

        scoreToPickupChain = pathBuilder
            .addPath(new BezierCurve(scorePose, pickupPose))
            .setLinearHeadingInterpolation(scorePose.getHeading(), pickupPose.getHeading())
            .build();

        pickupToScoreChain = pathBuilder
            .addPath(new BezierCurve(pickupPose, scorePose))
            .setLinearHeadingInterpolation(pickupPose.getHeading(), scorePose.getHeading())
            .build();
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void init_loop() {}

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override
    public void stop() {}

    public void autonomousPathUpdate() {
        switch (pathState) {

            case 0:
                follower.followPath(startToScoreChain,false);
                setPathState(1);
                break;

            case 1:
                if(!follower.isBusy()) {
                    pickupPose = pickupPose.withY(pickupPose.getY()-(24*pickupCycle));
                    follower.followPath(scoreToPickupChain,false);
                    setPathState(2);
                }
                break;

            case 2:
                if(pickupCycle==2 && !follower.isBusy()){
                    setPathState(67);
                }
                else if(!follower.isBusy()){
                    follower.followPath(pickupToScoreChain, false);
                }
                break;

            case 67:
                if(!follower.isBusy()) {
                    setPathState(-1);
                }
                break;
        }
    }
}
