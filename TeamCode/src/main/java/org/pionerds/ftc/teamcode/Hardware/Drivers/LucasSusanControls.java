package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.AimbotMotorMovement;
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
        super(driverName, maxSpeed);
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
    boolean stoppingAimbot = false;
    int ballsOnRamp = 0;


    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {
        hardware.telemetry.addLine("\nBalls On Ramp: "+ballsOnRamp);
        if (gamepad.a && !gamepad.b) {
            if(!movingSusan){
                hardware.storage.automatedSusan(ballsOnRamp);
            }
            hardware.aimbot.tick(AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY, false);
            movingSusan = true;
        } else if (gamepad.a && gamepad.b) {
            hardware.aimbot.tick(AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY, true);
            ballsOnRamp++;
            stoppingAimbot = true;
        } else if (!(gamepad.a)) {
            movingSusan = false;
            stoppingAimbot = false;
        }

        hardware.storage.printAlgorithmData(ballsOnRamp);

    }
}