package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class Gyro {

    Hardware hardware = null;

    private IMU gyro = null;
    private IMU.Parameters params;

    public void init(Hardware hardware) {
        this.hardware = hardware;

        gyro = this.hardware.mapping.getIMU();
        params = new IMU.Parameters(
            new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
            )
        );
        gyro.initialize(params);
        gyro.resetYaw();
    }

    /**
     * Resets the yaw angle of the gyro to zero.
     */
    public void resetYaw() {
        if (gyro != null) {
            gyro.resetYaw();
        }
    }

    public YawPitchRollAngles getAngles() {
        return gyro.getRobotYawPitchRollAngles();
    }
}
