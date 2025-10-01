package org.pionerds.ftc.teamcode.Hardware;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.*;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;

public class Drivetrain {

    Hardware hardware = null;
    double[] motorSpeed = { 0.0, 0.0, 0.0, 0.0 };

    public DcMotor[] motors = { null, null, null, null }; //front right, front left, back left, back right

    public void init(Hardware hardware) {
        this.hardware = hardware;

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
    }

    public double[] driveDPad(Gamepad driverGamepad){
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

        return motorSpeed;
    }



    public double[] bumperTurn(Gamepad driverGamepad){
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

       return motorSpeed;
    }

    public void stopMotors(){
        motorSpeed[0] = 0.00;
        motorSpeed[1] = 0.00;
        motorSpeed[2] = 0.00;
        motorSpeed[3] = 0.00;
    }

    public void stickDrive(DriverControls driverControls) {
        double x = driverControls.getSpeedX();
        double y = driverControls.getSpeedY();

        if (Math.abs(x) < 0.2 && Math.abs(y) < 0.2) {
            stopMotors();
        } else {
            motorSpeed[0] = (-x - y);
            motorSpeed[1] = (-x + y);
            motorSpeed[2] = (x + y);
            motorSpeed[3] = (x - y);
        }
    }
}
