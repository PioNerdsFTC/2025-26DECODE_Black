package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasSusanControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

/**
 * TeleOpMode - Main teleoperated (driver-controlled) operation mode for the robot.
 * <p>
 * This OpMode sets up:
 * - Two driver control stations (one for driving, one for storage/launching)
 * - All robot hardware systems
 * - Vision processing for target tracking
 * - Continuous control loop for real-time operation
 */
@TeleOp(name = "TeleOp")
public class TeleOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();

    // Driver 1: Primary robot driver (handles movement and navigation)
    final DriverControls driverControls1 = new LucasDriverControls(
        "Lucas Schwietz",
        true,        // Is primary driver
            1.0f
    );

    // Driver 2: Storage system operator (handles lazy susan and launching)
    final DriverControls driverControls2 = new LucasSusanControls(
        "Lucas S",
        false,       // Is secondary driver
        0.7f         // 70% speed control
    );

    /**
     * Main OpMode execution method - runs once when "INIT" is pressed.
     * Handles initialization, waiting for start, and the main control loop.
     */
    @Override
    public void runOpMode() throws InterruptedException {
        // === INITIALIZATION PHASE ===
        // Initialize all hardware systems (motors, servos, sensors, vision)
        hardware.init(hardwareMap, telemetry, driverControls1, driverControls2);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        // Wait for the driver to press "START" button
        waitForStart();

        // === START PHASE ===
        // Create and register elapsed time tracker for timing-dependent operations
        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        // Reset lazy susan encoder to establish known starting position
        hardware.storage.resetEncoderSusan();

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();

        // === MAIN CONTROL LOOP ===
        // Continuously process driver inputs and update robot state until OpMode stops
        while (opModeIsActive()) {

            // Process gamepad inputs and update all hardware systems
            hardware.tick(gamepad1, gamepad2);

            // Commented out test code (kept for debugging purposes):
            //hardware.storage.testRotateSusan(0.5);
            //hardware.storage.enableFeeder();

            // Display driver information on Driver Station
            telemetry.addLine("\nDriver: " + driverControls1.getDriverName());
            telemetry.addLine("Speed X: " + driverControls1.getSpeedX());
            telemetry.addLine("Speed Y: " + driverControls1.getSpeedY());

            // Commented out susan position telemetry (uncomment for debugging):
            //telemetry.addLine("susanPosition: "+hardware.storage.susanMotorEx.getCurrentPosition());

            // Display distance to target AprilTag for aiming assistance
            hardware.vision.printTagDistanceToTelemetry(AprilTagNames.BlueTarget);

            ScheduleTask.runTasks();

            // Update telemetry display on Driver Station
            telemetry.update();

            // Small delay to prevent overwhelming the system (1ms between loops)
            sleep(1);
        }

        // === CLEANUP PHASE ===
        // Stop all hardware systems when OpMode ends
        hardware.stop();
    }
}
