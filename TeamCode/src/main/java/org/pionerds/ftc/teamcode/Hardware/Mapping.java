package org.pionerds.ftc.teamcode.Hardware;

import android.util.Log;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.pionerds.ftc.teamcode.Utils.Environment;

/**
 * Hardware mapping class for the robot.
 */
public class Mapping {

    /**
     * The hardware object.
     */
    private Hardware hardware;
    private HardwareMap map = null;

    /**
     * Constructor for the hardware mapping class.
     *
     * @param hardware The Hardware object.
     */

    public void init(Hardware hardware, HardwareMap map) {
        this.map = map;
        this.hardware = hardware;
    }

    /**
     * @param motorName              The name of the motor.
     * @param gearRatio              The gear ratio of the motor.
     * @param motorDirection         The direction of the motor.
     * @param motorZeroPowerBehavior The zero power behavior of the motor.
     * @return The motor object.
     */
    DcMotorImplEx getMotor(
        String motorName,
        Double gearRatio,
        DcMotorSimple.Direction motorDirection,
        DcMotor.ZeroPowerBehavior motorZeroPowerBehavior
    ) {
        DcMotorImplEx motor = null;

        try {
            motor = this.map.get(DcMotorImplEx.class, motorName);
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
    CRServo getContinuousServo(
        String servoName,
        CRServoImplEx.Direction servoDirection
    ) {
        CRServoImplEx continuousServo = null;
        try {
            continuousServo = this.map.get(CRServoImplEx.class, servoName);
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
    IMU getIMU() {
        IMU imu = null;

        try {
            imu = this.map.get(IMU.class, "imu");
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
    Servo getServoMotor(String servoMotorName) {
        Servo servoMotor = null;

        try {
            servoMotor = this.map.get(Servo.class, servoMotorName);
        } catch (Exception e) {
            Log.e(
                "Error",
                "Cannot map servo motor with name " + servoMotorName
            );

            if (!Environment.competing) hardware.continueRunning = false;
        }

        return servoMotor;
    }

    /**
     * Map a servo motor from the hardware map
     *
     * @param webcamName The name of the webcam
     * @return The webcam
     */
    CameraName getWebcam(String webcamName) {
        CameraName webcam = null;

        try {
            webcam = this.map.get(WebcamName.class, webcamName);
        } catch (Exception e) {
            Log.e("Error", "Cannot map servo motor with name " + webcam);

            if (!Environment.competing) hardware.continueRunning = false;
        }

        return webcam;
    }
}
