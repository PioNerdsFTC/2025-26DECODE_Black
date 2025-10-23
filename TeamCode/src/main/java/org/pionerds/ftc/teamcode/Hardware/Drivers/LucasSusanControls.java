package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;
import org.pionerds.ftc.teamcode.Hardware.PioNerdAprilTag;

public class LucasSusanControls extends DriverControls {

    public LucasSusanControls(
        String driverName,
        boolean isDriver,
        float maxSpeed
    ) {
        super(driverName, isDriver, maxSpeed);
    }

    /**
     * Ticked every loop in the TeleOp.
     * @param gamepad
     * @param hardware
     *
     * @Controls:
     * Left_Bumper - 0.5 Speed Modifier <br>
     * A_Button - Feed Intake <br>
     * !(A_Button) - Contract Intake <br>
     * X_Button - Send PioNerdTag distance to Aimbot <br>
     * Right_Trigger - Set Launcher Velocity * 400 (@Overrides X_Button) <br>
     * Left_Stick - Sends Positional Request to Drivetrain <br>
     * Right_Stick.x - Sends Rotational Request to Drivetrain
     *
     **/

    boolean reset_Gyro_Pressed = false;
    boolean movingSusan = false;

    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {
        // A-Button
        if (gamepad.dpad_down) {
            hardware.storage.feed();
        } else {
            hardware.storage.contract();
        }

        if (!movingSusan) {
            if (gamepad.dpad_down) {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
            } else if (gamepad.dpad_left) {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
            } else if (gamepad.dpad_right) {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
            } else if (gamepad.y) {
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
            } else if (gamepad.x) {
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
            } else if (gamepad.b) {
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
            }
            movingSusan = true;
        } else if (
            !(gamepad.dpad_down ||
                gamepad.dpad_left ||
                gamepad.dpad_right ||
                gamepad.y ||
                gamepad.x ||
                gamepad.b)
        ) {
            movingSusan = false;
        }
    }
}
