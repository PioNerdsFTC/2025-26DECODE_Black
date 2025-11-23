package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Utils.DataStorage;

public class LucasDriverControlsRed extends DriverControls {

    boolean resetGyroPressed = false;

    public LucasDriverControlsRed(String driverName, boolean isDriver, float maxSpeed) {
        super(driverName, maxSpeed);
        this.setIsDriver(isDriver);
    }

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

    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {
        // Resets
        setSpeedMultiplier(1.0f);

        // Left Bumper
        if (gamepad.left_bumper) {
            setSpeedMultiplier(0.5f);
            setRotationMultiplier(0.5f);
        }

        // Set Rotation Speed for Drivetrain
        setRotationSpeed(
                (float)(Math.signum(gamepad.right_stick_x)*Math.pow(gamepad.right_stick_x,2))
        );

        // Set Speeds to the value or the capped value for the driver
        if (gamepad.left_stick_x < 0) {
            setSpeedX(
                Math.max(-1*gamepad.left_stick_x, -getMaxSpeed()) * speedMultiplier
            );
        } else {
            setSpeedX(
                Math.min(-1*gamepad.left_stick_x, getMaxSpeed()) * speedMultiplier
            );
        }

        if (gamepad.left_stick_y < 0) {
            setSpeedY(
                Math.max(-1*gamepad.left_stick_y, -getMaxSpeed()) * speedMultiplier
            );
        } else {
            setSpeedY(
                Math.min(-1*gamepad.left_stick_y, getMaxSpeed()) * speedMultiplier
            );
        }

        if (!resetGyroPressed && gamepad.dpad_up && gamepad.dpad_right) {
            resetGyroPressed = true;
            hardware.gyro.resetYaw();
            DataStorage.storeAllAngles(new double[] {0.0,0.0,0.0});

        } else {
            resetGyroPressed = false;
        }

        if (getIsDriver()) {
            hardware.drivetrain.driveWithControls(this, false, false);
        }
    }
}
