public class Autonomous {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private final Pose startPose = new Pose(
        56,
        Constants.localizerConstants.robot_Width / 2,
        Math.toRadians(90)
    ); // Start Pose of our robot.
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

    private int getIntakeTotalToggles() {
        return intakeEnableCount + intakeDisableCount;
    }

    final Hardware hardware = new Hardware();

    private PathBuilder pathBuilder;
    private PathBuilder pathBuilder2;
    private PathChain startToScoreChain;
    private PathChain pickupAndScoreChain;

    public enum State {
        START_TO_SCORE,
        PICKUP_BALLS,
        PARKING,
        DONE,
    }

    private State pathState;

    /**
     * Changes the current state of the autonomous state machine.
     * Also resets the path timer to measure duration of the new state.
     *
     * @param pState The new State to transition to
     */
    private void setPathState(State pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void start() {
        opmodeTimer.resetTimer();
        setPathState(State.START_TO_SCORE); // Begin with first state
    }
}
