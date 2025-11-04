package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.AimbotMotorMovement;
import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
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

    boolean reset_Gyro_Pressed = false;
    boolean movingSusan = false;
    boolean stoppingAimbot = false;
    boolean ballCountPressed = false;
    int ballsOnRamp = 0;

    boolean changingIntakeState = false;


    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {

        if (gamepad.dpad_up && !gamepad.dpad_down) {
            if(!movingSusan){
                hardware.storage.automatedSusan(ballsOnRamp);
            }
            hardware.aimbot.tick(AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY, false);
            movingSusan = true;
        } else if (!gamepad.dpad_up && gamepad.dpad_down) {
            hardware.aimbot.tick(AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY, true);
            if(!ballCountPressed){
                ballsOnRamp++;
                ballCountPressed = true;
            }
            stoppingAimbot = true;
        } else {
            movingSusan = false;
            stoppingAimbot = false;
            ballCountPressed = false;
        }

        // Handle manual ball count adjustment with debouncing
        if(gamepad.dpad_right && !ballCountPressed) {
            if(ballsOnRamp==9){
                ballsOnRamp = 0;
            } else {
                ballsOnRamp+=1;
            }
            ballCountPressed = true;
        } else if(gamepad.dpad_left && !ballCountPressed){
            if(ballsOnRamp==0) {
                ballsOnRamp = 9;
            } else {
                ballsOnRamp-=1;
            }
            ballCountPressed = true;
        } else if(!(gamepad.dpad_right || gamepad.dpad_left || gamepad.dpad_down)) {
            // Reset the pressed flag when no directional buttons are pressed
            ballCountPressed = false;
        }

        if(!changingIntakeState){
            if(gamepad.y) hardware.storage.disableIntake(Artifact.EMPTY);
            if(gamepad.x) hardware.storage.disableIntake(Artifact.PURPLE);
            if(gamepad.a) hardware.storage.disableIntake(Artifact.GREEN);
            if(gamepad.b) hardware.storage.enableIntake();

            changingIntakeState = true;
        } else if(!(gamepad.y || gamepad.x || gamepad.a || gamepad.b)) {
            changingIntakeState = false;
        }

        hardware.storage.printAlgorithmData(ballsOnRamp);

    }
}