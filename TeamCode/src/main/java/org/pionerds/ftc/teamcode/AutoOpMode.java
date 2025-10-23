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
import org.pionerds.ftc.teamcode.Pathfinding.Constants;

@Autonomous(name = "AutoOpMode", group = "Examples")
public class AutoOpMode extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose = new Pose(56, 8, Math.toRadians(90)); // Start Pose of our robot.

    private int pathState;
    private Path scorePreload;

    public PathBuilder pathBuilder;

    public static PathChain path1;
    public static PathChain path2;
    public static PathChain path3;

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

        path1 = pathBuilder
            .addPath(
                new BezierLine(
                    new Pose(56.000, 8.000),
                    new Pose(53.150, 29.047)
                )
            )
            .setLinearHeadingInterpolation(
                Math.toRadians(90),
                Math.toRadians(180)
            )
            .build();

        path2 = pathBuilder
            .addPath(
                new BezierCurve(
                    new Pose(53.150, 29.047),
                    new Pose(84.258, 29.253),
                    new Pose(80.549, 56.034)
                )
            )
            .setLinearHeadingInterpolation(
                Math.toRadians(180),
                Math.toRadians(270)
            )
            .build();

        path3 = pathBuilder
            .addPath(
                new BezierCurve(
                    new Pose(80.549, 56.034),
                    new Pose(52.326, 65.099),
                    new Pose(55.828, 96.412)
                )
            )
            .setLinearHeadingInterpolation(
                Math.toRadians(270),
                Math.toRadians(0)
            )
            .build();
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void init_loop() {
        follower.followPath(path1);
    }

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
}
