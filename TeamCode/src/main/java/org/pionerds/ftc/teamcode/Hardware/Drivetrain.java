package org.pionerds.ftc.teamcode.Hardware;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.*;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;

import java.sql.Driver;

public class Drivetrain {

    Hardware hardware = null;

    private Telemetry telemetry = null;

    private DcMotor[] motors = { null, null, null, null }; //front right, front left, back left, back right
    private double[] motorSpeed = { 0.0, 0.0, 0.0, 0.0 };
    private String[] motorNames = {"motor0","motor1","motor2","motor3"};

    public void init(Hardware hardware, Telemetry telemetry) {
        this.hardware = hardware;
        this.telemetry = telemetry;

        for (int i = 0; i < 4; i++) {
            motors[i] = this.hardware.mapping.getMotor(motorNames[i], 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    public void setDriveMotorsPow(){
        for (int i = 0; i < 4; i++) {
            motors[i].setPower(motorSpeed[i]);
            telemetry.addLine(
                    "Motor " +
                    Integer.toString(i) +
                    " Pow: " +
                    (Math.round(motorSpeed[i] * 100) / 100.0));
        }
    }



    public void scaleMotorsToFit(boolean bumperTurning,DriverControls driverControls){
        if (bumperTurning) {
            for (int i = 0; i < 4; i++){
                motorSpeed[i]+= driverControls.getRotationSpeed();
            }
        }

        boolean flag = false;

        for (double speed : motorSpeed) {
            if (Math.abs(speed) > 1) {
                flag = true;
                break;
            }
        }

        if (!flag) return;

        double maxMotorPow = Math.max(Math.max(motorSpeed[0],motorSpeed[1]),Math.max(motorSpeed[2],motorSpeed[3]));
        double minMotorPow = Math.min(Math.min(motorSpeed[0],motorSpeed[1]),Math.min(motorSpeed[2],motorSpeed[3]));

        double finalMotorDivisor = Math.max(maxMotorPow,Math.abs(minMotorPow));

        for (int i = 0; i < 4; i++){
            motorSpeed[i] /= finalMotorDivisor;
        }

        telemetry.addLine("MotorSpeedDivisor: "+finalMotorDivisor);
    }

    public void stopMotors(){
        motorSpeed[0] = 0.00;
        motorSpeed[1] = 0.00;
        motorSpeed[2] = 0.00;
        motorSpeed[3] = 0.00;
    }

    public void robotCentricDrive(DriverControls driverControls) {
        double x = driverControls.getSpeedX();
        double y = driverControls.getSpeedY();

        if (Math.abs(x) < 0.2 && Math.abs(y) < 0.2) {
            stopMotors();
            return;
        }
        motorSpeed[0] = -x - y;
        motorSpeed[1] = -x + y;
        motorSpeed[2] = x + y;
        motorSpeed[3] = x - y;
    }


    public void driveWithControls(DriverControls driverControls, boolean hasDumbDrivePreference, boolean bumperTurnPreferred){
        // for laying flat, use Roll. for vertical, use YAW (test robot rn)
        if (hasDumbDrivePreference) {
            robotCentricDrive(driverControls);
        } else {
            stickDrive(driverControls, hardware.gyro.getAngles().getYaw());
            stickTurn(driverControls);
        }

        scaleMotorsToFit(bumperTurnPreferred, driverControls);
        setDriveMotorsPow();
    }

    public void stickDrive(DriverControls driverControls, double orientation) {
        double x = driverControls.getSpeedX();
        double y = driverControls.getSpeedY();

        double[] convertedAngle = convertOrientation(x, y, orientation);

        x = convertedAngle[0];
        y = convertedAngle[1];

        telemetry.addLine("\nDrivetrain:");
        telemetry.addLine("X: " + x + "\nY: " + y);

        if (Math.abs(x) < 0.2 && Math.abs(y) < 0.2) {
            stopMotors();
            return;
        }
        double mag = convertedAngle[2];

        motorSpeed[0] = ((-x - y)) * mag;
        motorSpeed[1] = ((-x + y)) * mag;
        motorSpeed[2] = ((x + y)) * mag;
        motorSpeed[3] = ((x - y)) * mag;
    }

    public void stickTurn(DriverControls driverControls) {
        if (Math.abs(driverControls.getRotationSpeed()) > 0.2) {
            telemetry.addLine("Rotation Speed: "+driverControls.getRotationSpeed());

            double x = -driverControls.getRotationSpeed();

            for (int i = 0; i < 4; i++) {
                motorSpeed[i] += x;
            }
        }
    }

    public double[] convertOrientation(double x, double y, double orientation){
        x = -x;
        y = -y;

        orientation = Math.toRadians(orientation);

        double mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        double stickAngle = Math.atan2(y, x);
        double finalAngle = stickAngle + orientation;

        double return_x = Math.cos(finalAngle);
        double return_y = Math.sin(finalAngle);

        return new double[] { -return_x, -return_y, mag };
    }
}