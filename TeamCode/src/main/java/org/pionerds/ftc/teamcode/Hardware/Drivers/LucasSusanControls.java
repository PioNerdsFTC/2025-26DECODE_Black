package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;
import org.pionerds.ftc.teamcode.Hardware.PioNerdAprilTag;

public class LucasSusanControls extends DriverControls {
    public LucasSusanControls(String driverName, boolean isDriver, float maxSpeed){
        super(driverName, isDriver ,maxSpeed);
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
    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {

        // A-Button
        if(gamepad.dpad_down){
            hardware.storage.feed();
        } else {
            hardware.storage.contract();
        }


        if(gamepad.dpadUpWasPressed()){
            hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
        }
        if(gamepad.dpadLeftWasPressed()){
            hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
        }
        if(gamepad.dpadRightWasPressed()){
            hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
        }
        if(gamepad.yWasPressed()){
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
        }
        if(gamepad.xWasPressed()){
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
        }
        if(gamepad.bWasPressed()){
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
        }

    }
}