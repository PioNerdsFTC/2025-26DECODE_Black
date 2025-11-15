package org.pionerds.ftc.teamcode.Hardware;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.ScheduleTask;

/**
 * Aimbot class manages the automated launcher system for scoring artifacts.
 * It uses AprilTag vision to determine distance to target and automatically
 * adjusts launcher power/velocity. Includes timing mechanisms for feeding
 * artifacts and stopping the launcher after firing.
 */
public class Aimbot {

    private final double maxLaunchDistanceCM = 10000; // Default max distance in centimeters for effective launching
    // Timing variables for controlled launching sequence
    double tickDelay = 100.0;       // Minimum milliseconds between aimbot tick executions
    double stopDelay = 1000.0;      // Milliseconds to wait after feeding before stopping launcher
    double lastTick = 0.0;          // Timestamp of last tick execution
    double lastStopTick = 0.0;      // Timestamp when stop sequence began
    boolean stopPending = false;    // Tracks if we're in the delayed stop sequence
    boolean isStopped = false;      // Tracks if aimbot is stopped
    private Hardware hardware;
    private Telemetry telemetry;
    private DriverControls controls;

    // Package-private method to set hardware reference from Hardware class

    /**
     * Package-private setter to inject hardware reference from the main Hardware class.
     *
     * @param hardware Main hardware object containing all robot components
     */
    void setHardware(Hardware hardware) {
        this.hardware = hardware;
        this.telemetry = hardware.telemetry;
    }

    /**
     * Initializes the aimbot system with all necessary dependencies.
     *
     * @param hardware      Main hardware object
     * @param telemetry     Telemetry for debugging output
     * @param tagName       AprilTag to track (not currently used, kept for future expansion)
     * @param motorMoveType Movement type (not currently used, kept for future expansion)
     */
    public void init(Hardware hardware, Telemetry telemetry, AprilTagNames tagName, AimbotMotorMovement motorMoveType) {
        this.hardware = hardware;
        this.telemetry = telemetry;
    }

    /**
     * Sets launcher power based on range to target.
     * Power is calculated as a proportion of the distance, capped at maximum power.
     *
     * @param tagName The AprilTag to use for range calculation
     */
    public void revLauncherPower(AprilTagNames tagName) {
        double range = hardware.vision.getPioNerdAprilTag(tagName).range();
        // Set power to minimum of range or 1.0 (full power)
        hardware.launcher.setLauncherPower(Math.min(range, 1));
    }

    /**
     * Checks if the robot is within effective launching range of the target.
     *
     * @param tagName The AprilTag to check distance to
     * @return true if within range, false otherwise
     */
    public boolean isInRange(AprilTagNames tagName) {
        return (hardware.vision.getPioNerdAprilTag(tagName).range() < maxLaunchDistanceCM);
    }

    /**
     * Calculates motor velocity based on distance to target.
     * Uses a linear relationship: velocity = range * 1.5
     *
     * @param range Distance to target in centimeters
     * @return Calculated motor velocity
     */
    private double calculateMotorVelocity(double range) {
        return range * 1.5;
    }

    /**
     * Calculates motor power as a proportion of maximum distance.
     * Returns a value between 0 and 1 representing the power level.
     *
     * @param range Distance to target in centimeters
     * @return Calculated motor power (0.0 to 1.0)
     */
    private double calculateMotorPower(double range) {
        return (range * 1.5) / maxLaunchDistanceCM;
    }

    /**
     * Main aimbot control loop - call this repeatedly to maintain automated targeting and launching.
     * <p>
     * This method implements a state machine with three states:
     * 1. Normal operation: Adjust launcher speed based on distance to target
     * 2. Stop requested: Begin feeding artifact and start stop timer
     * 3. Stop pending: Wait for delay, then stop launcher and clear inventory
     * <p>
     * The method rate-limits itself to execute at most once per tickDelay milliseconds.
     *
     * @param tagName       The AprilTag to track for distance measurement
     * @param movementType  Whether to use VELOCITY or POWER control mode
     * @param stopRequested True when user wants to fire an artifact
     */
    public void tick(AprilTagNames tagName, AimbotMotorMovement movementType, boolean stopRequested) {
        // Save tons of processing power
        if (isStopped && stopRequested) return;
        if (stopPending && !stopRequested) return;

        // Safety checks - ensure all required objects are initialized
        if (hardware == null || hardware.elapsedTime == null || telemetry == null) return;

        // Rate limiting: only execute once per tickDelay milliseconds
        if (hardware.elapsedTime.milliseconds() - lastTick > tickDelay) {
            telemetry.addLine("\n\nTICKING AIMBOT\n");
            lastTick = hardware.elapsedTime.milliseconds();

            // Get the target AprilTag data from vision system
            PioNerdAprilTag pioTag = hardware.vision.getPioNerdAprilTag(tagName);
            if (pioTag == null) {
                telemetry.addLine("TAG == NULL");
                return;
            }
            double range = pioTag.range();

            // Only operate if robot is within effective launch range
            if (range < maxLaunchDistanceCM) {
                telemetry.addLine("\nLauncher Motor:");

                // STATE 1: Normal operation (not in stop sequence)
                if (!stopPending) {
                    // Set launcher motor speed based on control mode
                    if (movementType.equals(AimbotMotorMovement.VELOCITY)) {
                        hardware.launcher.setLauncherVelocity(calculateMotorVelocity(range));
                    } else {
                        hardware.launcher.setLauncherPower(calculateMotorPower(range));
                    }

                    // STATE 2: Stop requested - begin firing sequence
                    if (stopRequested && !stopPending && !isStopped) {
                        hardware.storage.enableFeeder();  // Start feeding artifact into launcher
                        stopPending = true;               // Enter stop pending state
                        lastStopTick = lastTick;         // Record when we started the stop sequence
                        ScheduleTask.add(() -> {
                                this.tick(AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY, true);
                            },
                            hardware.elapsedTime.milliseconds() + 5500);
                    }

                    // STATE 3: Stop pending - waiting for artifact to feed before stopping
                } else if (!isStopped && (hardware.elapsedTime.milliseconds() - lastStopTick > stopDelay)) {
                    // Delay has elapsed, artifact should be launched - now stop everything
                    hardware.launcher.stopLaunchers();
                    hardware.storage.disableFeeder();
                    hardware.storage.disableIntake(Artifact.EMPTY); // Clear artifact from inventory slot
                    stopPending = false;  // Reset state for next firing sequence
                    isStopped = true; // Tells aimbot it is stopped and stops it from being stopped repeatedly

                    // OPTIONAL: Cancel stop sequence if stop is no longer requested during delay
                } else if (!stopRequested) {
                    stopPending = false;  // Allow cancellation of launch sequence
                }
            } else {
                // Robot is out of effective launching range
                telemetry.addLine("Out of range!");
            }
        }

    }

    public void allowStopping() {
        if (!stopPending) isStopped = false;
    }

}
