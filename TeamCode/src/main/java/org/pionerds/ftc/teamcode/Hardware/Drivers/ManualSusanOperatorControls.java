package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;

public class ManualSusanOperatorControls extends DriverControls {

    // Button state tracking for debouncing (prevents multiple triggers from single press)
    boolean reset_Gyro_Pressed = false;    // Reserved for gyro reset functionality
    boolean movingSusan = false;           // Tracks if d-pad up is held (susan auto-positioning)
    boolean stoppingAimbot = false;        // Tracks if d-pad down is held (firing sequence)
    boolean ballCountPressed = false;      // Tracks if any d-pad button is pressed (prevents double-counting)
    int ballsOnRamp = 0;                   // Counter for artifacts scored (used to determine next target)
    boolean stoppedAlready = true;
    boolean changingIntakeState = false;   // Tracks if any intake button is pressed (prevents multiple state changes)
    boolean susanAdjusting = false;
    boolean changingPos = false;
    boolean changingLauncherSpeed = false;
    boolean changingIntakeSpeed = false;
    boolean launcherOn = false;
    boolean intakeOn = false;
    boolean changingFeeder = false;
    boolean feederOn = false;

    public ManualSusanOperatorControls(
            String driverName,
            boolean isDriver,
            float maxSpeed
    ) {
        super(driverName, maxSpeed);
    }

    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {

        /*LazySusanPositions currentPos = hardware.storage.getCurrentSusanPositionEnum();

        if (!changingPos) {
            if (gamepad.dpad_left) {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
            } else if (gamepad.dpad_down) {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
            } else if (gamepad.dpad_right) {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
            } else if (gamepad.x) {
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
            } else if (gamepad.a) {
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
            } else if (gamepad.b) {
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
            } else if (gamepad.left_bumper) {
                for (int i = 0; i < LazySusanPositions.values().length; i++) {
                    if (currentPos.equals(LazySusanPositions.values()[i])) {
                        hardware.storage.moveSusanTo(LazySusanPositions.values()[(i - 1 + 6) % 6]);
                    }
                }
            } else if (gamepad.right_bumper) {
                for (int i = 0; i < LazySusanPositions.values().length; i++) {
                    if (currentPos.equals(LazySusanPositions.values()[i])) {
                        hardware.storage.moveSusanTo(LazySusanPositions.values()[(i + 1) % 6]);
                    }
                }


            }

        } else if (changingPos && !(gamepad.dpad_left || gamepad.dpad_down || gamepad.dpad_right || gamepad.x || gamepad.a || gamepad.b || gamepad.left_bumper || gamepad.right_bumper)) {
            changingPos = false;
        }
        */

        if(!changingLauncherSpeed && gamepad.left_stick_button) {
            launcherOn = !launcherOn;
            changingLauncherSpeed = true;
        } else if(!gamepad.left_stick_button) {
            changingLauncherSpeed = false;
        }

        if(launcherOn) {
            hardware.launcher.setLauncherVelocity(hardware.aimbot.calculateMotorVelocity(AprilTagNames.BlueTarget));
        }
        if(!launcherOn) {
            hardware.launcher.setLauncherVelocity(0);
        }

        if(!changingFeeder && gamepad.dpad_up){
            if(!feederOn) hardware.storage.enableFeederManual();
            if(feederOn) hardware.storage.disableFeeder();
            feederOn = !feederOn;
            changingFeeder = true;
        } else if(!gamepad.dpad_up){
            changingFeeder = false;
        }

        if(!changingIntakeSpeed && gamepad.right_stick_button){
            if(!intakeOn) hardware.storage.enableIntake();
            if(intakeOn) hardware.storage.disableIntake();
            intakeOn = !intakeOn;
            changingIntakeSpeed = true;
        } else if(!gamepad.right_stick_button){
            changingIntakeSpeed = false;
        }

        if (Math.abs(gamepad.left_stick_x)>0.1) {
            hardware.storage.adjustLazySusan(gamepad.left_stick_x);
            susanAdjusting = true;
        } else if(gamepad.dpad_left){
            hardware.storage.adjustLazySusan(1);
            susanAdjusting = true;
        } else if(gamepad.dpad_right){
            hardware.storage.adjustLazySusan(-1);
            susanAdjusting = true;
        }
        else {
            susanAdjusting = false;
        }

        if (!susanAdjusting) {
            hardware.storage.stopSusan();
        }
        if(Math.abs(gamepad.right_stick_y)>0.1){
            hardware.storage.enableIntakeManual(gamepad.right_stick_y);
            intakeOn = true;
        }

        hardware.telemetry.addLine("Launcher: " + launcherOn);
        hardware.telemetry.addLine("Intake: " + intakeOn);
    }
}
