package org.pionerds.ftc.teamcode.Hardware;

import android.util.Log;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.pionerds.ftc.teamcode.Environment;

/**
 * Hardware mapping class for the robot.
 */
public class Mapping {

    /**
     * The hardware object.
     */
    private Hardware hardware;

    /**
     * Constructor for the hardware mapping class.
     *
     * @param hardware The Hardware object.
     */
    Mapping(Hardware hardware) { }

    /**
     * @param motorName              The name of the motor.
     * @param gearRatio              The gear ratio of the motor.
     * @param motorDirection         The direction of the motor.
     * @param motorZeroPowerBehavior The zero power behavior of the motor.
     * @return The motor object.
     */
    DcMotorImplEx mapMotor(
            String motorName,
            Double gearRatio,
            DcMotorSimple.Direction motorDirection,
            DcMotor.ZeroPowerBehavior motorZeroPowerBehavior
    ) {
        DcMotorImplEx motor = null;

        try {
            motor = hardware.map.get(DcMotorImplEx.class, motorName);
            motor.setDirection(motorDirection);
            motor.setZeroPowerBehavior(motorZeroPowerBehavior);
            motor.getMotorType().setGearing(gearRatio);
        } catch (Exception e) {
            Log.e("Error", "Cannot map motor: " + motorName);

            if (!Environment.competing) hardware.continueRunning = false;
        }

        return motor;
    }

    /**
     * Map a continuous servo from the hardware map
     *
     * @param servoName      Name of continuous rotation servo
     * @param servoDirection Direction of crServo
     * @return The servo
     */
    CRServo mapContinuousServo(
            String servoName,
            CRServoImplEx.Direction servoDirection
    ) {
        CRServoImplEx continuousServo = null;
        try {
           continuousServo = hardware.map.get(CRServoImplEx.class, servoName);
           continuousServo.setDirection(servoDirection);
        } catch (Exception e) {
            Log.e("Error", "Cannot map continuous servo: " + servoName);

            if (!Environment.competing) hardware.continueRunning = false;
        }
        return continuousServo;
    }

    /**
     * Get the IMU(gyro) from the hardware map
     *
     * @return the IMU
     */
    IMU mapIMU() {

        IMU imu = null;

        try {
            imu = hardware.map.get(IMU.class, "imu");
        } catch (Exception e) {
            Log.e("Error", "Cannot map IMU, is it named 'imu'?");

            if (!Environment.competing) hardware.continueRunning = false;
        }

        return imu;
    }

    /**
     * Map a servo motor from the hardware map
     *
     * @param servoMotorName The name of the servo motor
     * @return The servo motor
     */
    ServoImplEx mapServoMotor(String servoMotorName) {
        ServoImplEx servoMotor = null;

        try {
            servoMotor = hardware.map.get(ServoImplEx.class, servoMotorName);
        } catch (Exception e) {
            Log.e("Error", "Cannot map servo motor with name " + servoMotorName);

            if (!Environment.competing) hardware.continueRunning = false;
        }

        return servoMotor;
    }
}