package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.pionerds.ftc.teamcode.Utils.DataStorage;

public class Gyro {

    Hardware hardware = null;

    private IMU gyro = null;
    private IMU.Parameters params;

    public void init(Hardware hardware) {
        this.hardware = hardware;

        gyro = this.hardware.mapping.getIMU();
        params = new IMU.Parameters(
            new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
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

    /**
     * Gets the accumulated angles (yaw, pitch, roll) by adding stored angles to current gyro readings.
     * The returned array contains [yaw, pitch, roll] in the gyro's default angle unit (usually degrees).
     * @return double array containing [yaw, pitch, roll]
     */
    public double[] getAngles() {

        double[] result = {0.0,0.0,0.0};
        YawPitchRollAngles newGyroData = gyro.getRobotYawPitchRollAngles();
        double[] oldGyroData = DataStorage.getAllStoredAngles();

        result[0] = oldGyroData[0] + newGyroData.getYaw();
        result[1] = oldGyroData[1] + newGyroData.getPitch();
        result[2] = oldGyroData[2] + newGyroData.getRoll();

        return result;
    }
}
