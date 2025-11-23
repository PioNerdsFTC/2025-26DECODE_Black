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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class AutoOpMode extends OpMode {

    protected Auto auto;

@Disabled
@Autonomous(name = "AutoOpMode", group = "Examples")
public class AutoOpMode extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose = new Pose(56, Constants.localizerConstants.robot_Width / 2, Math.toRadians(90)); // Start Pose of our robot.
    private final Pose scanPose = new Pose(56, 80, Math.toRadians(90));
    private final Pose scorePose = new Pose(48, 110, Math.toRadians(144.046));
    private final Pose pickupPose1 = new Pose(48, 84, Math.toRadians(0));
    private final Pose pickupPose2 = new Pose(48, 60, Math.toRadians(0));
    private final Pose pickupPose3 = new Pose(48, 36, Math.toRadians(0));
    private final Pose pickupEndPose1 = new Pose(32, 84, Math.toRadians(0));
    private final Pose pickupEndPose2 = new Pose(32, 60, Math.toRadians(0));
    private final Pose pickupEndPose3 = new Pose(32, 36, Math.toRadians(0));
    private final Pose endPose = new Pose(38.75, 33.25, Math.toRadians(0));
    private final double pileYCoordOffset = 24;
  
    private boolean scanned = false;
    private boolean pathStarted = false;
    private String artifactPattern = "No scan attempt yet";

    // Count intake enable/disable calls for debugging/verification
    private int intakeEnableCount = 0;
    private int intakeDisableCount = 0;
    private int getIntakeTotalToggles() { return intakeEnableCount + intakeDisableCount; }

    final Hardware hardware = new Hardware();

    private PathBuilder pathBuilder;
    private PathBuilder pathBuilder2;
    private PathChain startToScoreChain;
    private PathChain pickupAndScoreChain;


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


    @Override
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

    /**
     * Initialization method - called once when "INIT" is pressed.
     */
    @Override
    public void init() {
        // Auto will be initialized in subclasses
    }

    @Override
    public void start() {
        if (auto != null) {
            auto.start();
        }
    }

    @Override
    public void loop() {
        if (auto != null) {
            auto.loop();
        }
    }
}
