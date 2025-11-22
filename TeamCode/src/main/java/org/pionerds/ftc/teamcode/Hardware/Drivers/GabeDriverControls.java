package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.PioNerdAprilTag;

public class GabeDriverControls extends DriverControls {

    /**
     * Ticked every loop in the TeleOp.
     *
     * @param gamepad
     * @param hardware
     * @Controls: Left_Bumper - 0.5 Speed Modifier <br>
     * A_Button - Feed Intake <br>
     * !(A_Button) - Contract Intake <br>
     * X_Button - Send PioNerdTag distance to Aimbot <br>
     * Right_Trigger - Set Launcher Velocity * 400 (@Overrides X_Button) <br>
     * Left_Stick - Sends Positional Request to Drivetrain <br>
     * Right_Stick.x - Sends Rotational Request to Drivetrain
     *
     **/

    boolean reset_Gyro_Pressed = false;

    public GabeDriverControls(
        String driverName,
        boolean isDriver,
        float maxSpeed
    ) {
        super(driverName, maxSpeed);
        this.setIsDriver(isDriver);
    }

    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {
        // Resets
        setSpeedMultiplier(1.0f);

        // Left Bumper
        if (gamepad.left_bumper) {
            //  setSpeedMultiplier(0.5f);
            setRotationSpeed(0.5f);
        } else if (gamepad.right_bumper) {
            setRotationSpeed(-0.5f);
        } else if (!gamepad.right_bumper && !gamepad.left_bumper) {
            setRotationSpeed(0);
        }

        // A-Button
        if (gamepad.a) {
            hardware.storage.enableFeeder();
        } else {
            hardware.storage.disableFeeder();
        }

        PioNerdAprilTag blueTarget = hardware.vision.getPioNerdAprilTag(
            AprilTagNames.BlueTarget
        );
        if (gamepad.x && !(blueTarget == null) && gamepad.right_trigger > 0) {
            // Send the distance to the aimbot class
            hardware.launcher.setLauncherVelocity(blueTarget.range(2));
        }
        if (gamepad.right_trigger > 0) {
            hardware.launcher.setLauncherVelocity(gamepad.right_trigger * 400);
        }

        // Set Speeds to the value or the capped value for the driver
        if (gamepad.left_stick_x < 0) {
            setSpeedX(
                Math.max(gamepad.left_stick_x, -getMaxSpeed()) * speedMultiplier
            );
        } else {
            setSpeedX(
                Math.min(gamepad.left_stick_x, getMaxSpeed()) * speedMultiplier
            );
        }

        if (gamepad.left_stick_y < 0) {
            setSpeedY(
                Math.max(gamepad.left_stick_y, -getMaxSpeed()) * speedMultiplier
            );
        } else {
            setSpeedY(
                Math.min(gamepad.left_stick_y, getMaxSpeed()) * speedMultiplier
            );
        }

        if (!reset_Gyro_Pressed && gamepad.dpad_up && gamepad.dpad_right) {
            reset_Gyro_Pressed = true;
            hardware.gyro.resetYaw();
        } else {
            reset_Gyro_Pressed = false;
        }

        if (getIsDriver()) {
            hardware.drivetrain.driveWithControls(this, true, true);
        }
    }
}
