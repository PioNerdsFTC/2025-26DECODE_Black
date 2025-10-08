package org.pionerds.ftc.teamcode.Hardware;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.*;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;

public class Drivetrain {

    Hardware hardware = null;

    private Telemetry telemetry = null;

    private DcMotor[] motors = { null, null, null, null }; //front right, front left, back left, back right
    private double[] motorSpeed = { 0.0, 0.0, 0.0, 0.0 };

    public void init(Hardware hardware, Telemetry telemetry) {
        this.hardware = hardware;
        this.telemetry = telemetry;

        motors[0] = this.hardware.mapping.getMotor("motor0", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motors[1] = this.hardware.mapping.getMotor("motor1", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motors[2] = this.hardware.mapping.getMotor("motor2", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motors[3] = this.hardware.mapping.getMotor("motor3", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
    }

    static private final double maxPow = 0.8;

    public void setDriveMotorsPow(){
        motors[0].setPower(motorSpeed[0]);
        motors[1].setPower(motorSpeed[1]);
        motors[2].setPower(motorSpeed[2]);
        motors[3].setPower(motorSpeed[3]);
        telemetry.addLine("Motor 0 Pow: "+motorSpeed[0]);
        telemetry.addLine("Motor 1 Pow: "+motorSpeed[1]);
        telemetry.addLine("Motor 2 Pow: "+motorSpeed[2]);
        telemetry.addLine("Motor 3 Pow: "+motorSpeed[3]);
    }

    public void driveDPad(Gamepad driverGamepad){
        if (driverGamepad.dpad_down){
            try {
                motorSpeed[0] = maxPow;
                motorSpeed[1] = -maxPow;
                motorSpeed[2] = maxPow;
                motorSpeed[3] = -maxPow;
            } catch (Exception e) {
                Log.e("Error","Cannot power motors dpad_down");
            }
        }
        else if (driverGamepad.dpad_up){ try {
                motorSpeed[0] = -maxPow;
                motorSpeed[1] = maxPow;
                motorSpeed[2] = -maxPow;
                motorSpeed[3] = maxPow;

            } catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_up");
            }
        } else if (driverGamepad.dpad_left){
            try {
                motorSpeed[0] = maxPow;
                motorSpeed[1] = maxPow;
                motorSpeed[2] = -maxPow;
                motorSpeed[3] = -maxPow;

            } catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_left");
            }
        } else if (driverGamepad.dpad_right){
            try {
                motorSpeed[0] = -maxPow;
                motorSpeed[1] = -maxPow;
                motorSpeed[2] = maxPow;
                motorSpeed[3] = maxPow;
            }
            catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_right");
            }
        } else {
            try {
                motorSpeed[0] = -maxPow;
                motorSpeed[1] = -maxPow;
                motorSpeed[2] = maxPow;
                motorSpeed[3] = maxPow;
            }
            catch (Exception e){
                Log.e("Error","Unable to power down motors");
            }
        }

    }

    public void bumperTurn(Gamepad driverGamepad){
       if (driverGamepad.right_bumper) {
           try {
               motorSpeed[0] = -maxPow;
               motorSpeed[1] = -maxPow;
               motorSpeed[2] = -maxPow;
               motorSpeed[3] = -maxPow;
           } catch (Exception e) {
               Log.e("Error", "Cannot power motors right_bumper");
           }
       }
       if (driverGamepad.left_bumper){
            try {
                motorSpeed[0] = maxPow;
                motorSpeed[1] = maxPow;
                motorSpeed[2] = maxPow;
                motorSpeed[3] = maxPow;
            } catch (Exception e) {
                Log.e("Error","Cannot power motors left_bumper");
            }
        }

    }

    public void scaleMotorsToFit(){
        boolean flag = false;
        for (double speed : motorSpeed) {
            if (Math.abs(speed)>1) {
                flag = true;
            }
        }
        if(flag) {
            double maxMotorPow = Math.max(Math.max(motorSpeed[0],motorSpeed[1]),Math.max(motorSpeed[2],motorSpeed[3]));
            double minMotorPow = Math.min(Math.min(motorSpeed[0],motorSpeed[1]),Math.min(motorSpeed[2],motorSpeed[3]));

            double finalMotorDivisor = Math.max(maxMotorPow,Math.abs(minMotorPow));

            for(int i = 0; i<4; i++){
                motorSpeed[i] /= finalMotorDivisor;
            }
            telemetry.addLine("MotorSpeedDivisor: "+finalMotorDivisor);
        }
    }

    public void stopMotors(){
        motorSpeed[0] = 0.00;
        motorSpeed[1] = 0.00;
        motorSpeed[2] = 0.00;
        motorSpeed[3] = 0.00;
    }

    public void driveWithControls(DriverControls driverControls){
        // for laying flat, use Roll. for vertical, use YAW (test robot rn)
        stickDrive(driverControls, hardware.gyro.getAngles().getYaw());
        stickTurn(driverControls);
        scaleMotorsToFit();
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
        } else {
            double mag = convertedAngle[2];
            double matchMagnitude = mag;
            motorSpeed[0] = ((-x - y)) * matchMagnitude;
            motorSpeed[1] = ((-x + y)) * matchMagnitude;
            motorSpeed[2] = ((x + y)) * matchMagnitude;
            motorSpeed[3] = ((x - y)) * matchMagnitude;
        }

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
        double mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        orientation = Math.toRadians(orientation);
        double stickAngle = Math.atan2(y, x);
        double finalAngle = stickAngle + orientation;

        double return_x = Math.cos(finalAngle);
        double return_y = Math.sin(finalAngle);

        return new double[]{-return_x, -return_y, mag};

        //return new double[] {0,0};
    }
}