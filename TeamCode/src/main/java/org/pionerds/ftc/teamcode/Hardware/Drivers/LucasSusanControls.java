package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.AimbotMotorMovement;
import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;
import org.pionerds.ftc.teamcode.Hardware.PioNerdAprilTag;

/**
 * LucasSusanControls implements driver controls for the lazy susan storage system.
 * This driver station operator manages:
 * - Automated lazy susan positioning
 * - Aimbot control for launching artifacts
 * - Ball counter for tracking scoring progress
 * - Manual intake control with artifact type selection
 * 
 * Button mappings:
 * - D-pad Up: Auto-position susan and run aimbot (without firing)
 * - D-pad Down: Fire artifact and increment ball counter
 * - D-pad Right/Left: Manually adjust ball counter
 * - Y/X/A: Stop intake and mark collected artifact (EMPTY/PURPLE/GREEN)
 * - B: Enable intake
 */
public class LucasSusanControls extends DriverControls {

    public LucasSusanControls(
        String driverName,
        boolean isDriver,
        float maxSpeed
    ) {
        super(driverName, maxSpeed);
    }

    // Button state tracking for debouncing (prevents multiple triggers from single press)
    boolean reset_Gyro_Pressed = false;    // Reserved for gyro reset functionality
    boolean movingSusan = false;           // Tracks if d-pad up is held (susan auto-positioning)
    boolean stoppingAimbot = false;        // Tracks if d-pad down is held (firing sequence)
    boolean ballCountPressed = false;      // Tracks if any d-pad button is pressed (prevents double-counting)
    int ballsOnRamp = 0;                   // Counter for artifacts scored (used to determine next target)

    boolean changingIntakeState = false;   // Tracks if any intake button is pressed (prevents multiple state changes)
    /**
     * Main control loop - called every tick to handle gamepad input and update robot state.
     * Implements debounced button handling to prevent multiple actions from a single button press.
     * 
     * @param gamepad The gamepad input device
     * @param hardware The main hardware object for robot control
     */
    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {

        // === AIMBOT AND LAZY SUSAN CONTROL ===
        // D-pad controls for launching sequence
        
        // D-pad UP: Position susan and prepare to launch (but don't fire yet)
        if (gamepad.dpad_up && !gamepad.dpad_down) {
            hardware.telemetry.addLine("Controller: Starting Aimbot");
            // Only trigger susan movement once per button press
            if(!movingSusan){
                hardware.storage.automatedSusan(ballsOnRamp);  // Move susan to optimal position
            }
            // Keep aimbot active to track target (stopRequested=false means don't fire)
            hardware.aimbot.tick(AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY, false);
            movingSusan = true;  // Mark button as held
            
        // D-pad DOWN: Fire the artifact and increment ball counter
        } else if (!gamepad.dpad_up && gamepad.dpad_down) {
            hardware.telemetry.addLine("Controller: Stopping Aimbot");
            // Trigger aimbot with stopRequested=true to initiate firing sequence
            hardware.aimbot.tick(AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY, true);
            
            // Increment ball counter once per button press (debounced)
            if(!ballCountPressed){
                ballsOnRamp++;
                ballCountPressed = true;
            }
            stoppingAimbot = true;  // Mark button as held
            
        // Neither button pressed: Reset all state flags
        } else {
            movingSusan = false;
            stoppingAimbot = false;
            ballCountPressed = false;
            hardware.aimbot.allowStopping();
        }

        // === MANUAL BALL COUNTER ADJUSTMENT ===
        // D-pad RIGHT/LEFT: Manually increment/decrement ball counter (wraps at 0-9 range)
        
        // D-pad RIGHT: Increment counter
        if(gamepad.dpad_right && !ballCountPressed) {
            hardware.telemetry.addLine("Controller: Increasing BallCount");
            if(ballsOnRamp==9){
                ballsOnRamp = 0;  // Wrap to 0 after 9
            } else {
                ballsOnRamp+=1;
            }
            ballCountPressed = true;  // Prevent repeated triggering
            
        // D-pad LEFT: Decrement counter
        } else if(gamepad.dpad_left && !ballCountPressed){
            hardware.telemetry.addLine("Controller: Decrease BallCount");
            if(ballsOnRamp==0) {
                ballsOnRamp = 9;  // Wrap to 9 from 0
            } else {
                ballsOnRamp-=1;
            }
            ballCountPressed = true;  // Prevent repeated triggering
            
        // No d-pad buttons pressed: Reset debounce flag
        } else if(!(gamepad.dpad_right || gamepad.dpad_left || gamepad.dpad_down)) {
            ballCountPressed = false;
        }

        // === INTAKE CONTROL ===
        // Face buttons control intake and artifact type selection
        // Each button stops intake and records what type of artifact was collected
        
        if(!changingIntakeState){
            if(gamepad.y) hardware.storage.disableIntake(Artifact.EMPTY);   // Y: Collected nothing
            if(gamepad.x) hardware.storage.disableIntake(Artifact.PURPLE);  // X: Collected purple artifact
            if(gamepad.a) hardware.storage.disableIntake(Artifact.GREEN);   // A: Collected green artifact
            if(gamepad.b) hardware.storage.enableIntake();                  // B: Start intake motor

            changingIntakeState = true;  // Mark that an intake button is pressed
            
        // No intake buttons pressed: Reset debounce flag
        } else if(!(gamepad.y || gamepad.x || gamepad.a || gamepad.b)) {
            changingIntakeState = false;
        }

        // === TELEMETRY OUTPUT ===
        // Display current algorithm state for debugging
        hardware.storage.printAlgorithmData(ballsOnRamp);
        hardware.vision.printTagDistanceToTelemetry(AprilTagNames.BlueTarget);

    }
}