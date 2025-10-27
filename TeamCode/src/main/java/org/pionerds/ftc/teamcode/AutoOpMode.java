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

@Autonomous(name = "AutoOpMode", group = "Examples")
public class AutoOpMode extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose = new Pose(56, 8, Math.toRadians(90)); // Start Pose of our robot.
    private final Pose scanPose = new Pose(56, 80, Math.toRadians(90));
    private final Pose scorePose = new Pose(48, 110, Math.toRadians(144.046));
    final Hardware hardware = new Hardware();

    private int pathState;

    public PathBuilder pathBuilder;
    public static PathChain pathChain;
    public static PathChain pathChain2;

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
        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
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

        pathChain = pathBuilder
            .addPath(
                new BezierLine(startPose, scanPose)
            )
            .setConstantHeadingInterpolation(Math.toRadians(90))
            .build();

        pathChain2 = pathBuilder
            .addPath(
                new BezierCurve(scanPose, scorePose)
            )
            .setLinearHeadingInterpolation(
                Math.toRadians(90),
                Math.toRadians(144.046)
            )
            .build();

        follower.followPath(pathChain);
        follower.followPath(pathChain2);
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
                if(!follower.isBusy()) {
                    follower.followPath(pathChain,true);
                    hardware.vision.getArtifactPattern();
                    setPathState(1);
                }

            case 1:
                if(!follower.isBusy() && ) {
                    follower.followPath(pathChain2, true);
                    hardware.aimbot.tick();
                    setPathState(2);
                }
            case 2:
                if(!follower.isBusy()) {
                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                    setPathState(-1);
                }
                break;
        }
    }
}
