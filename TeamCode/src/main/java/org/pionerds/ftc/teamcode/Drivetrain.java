package org.pionerds.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Drivetrain {

    public DcMotor motor0 = null; //front right
    public DcMotor motor1 = null; //front left
    public DcMotor motor2 = null; //back left
    public DcMotor motor3 = null; //back right

    static private double maxPow = 0.8;


    public void setDriveMotorsPow(double[] powVals){
        motor0.setPower(powVals[0]);
        motor1.setPower(powVals[1]);
        motor2.setPower(powVals[2]);
        motor3.setPower(powVals[3]);
    }

    public double[] expandRotVals(double powVal){
        return new double[]{powVal,powVal,powVal,powVal};
    }

    public void drivetrainMain(Gamepad driverGamepad){

        driveDPad(driverGamepad);

    }

    public void driveDPad(Gamepad driverGamepad){

        if (driverGamepad.dpad_down){
            try {

            }
            catch (Exception e) {
                Log.e("Error","Cannot power motors dpad_down");
            }
        }
        if (driverGamepad.dpad_up){
            try {

            }
            catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_up");
            }
        }
        if (driverGamepad.dpad_left){
            try {

            }
            catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_left");
            }
        }
        if (driverGamepad.dpad_right){
            try {

            }
            catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_right");
            }
        }
    }

    public void bumperTurn(Gamepad driverGamepad){
            if (driverGamepad.right_bumper) {
                try {
                    motor0.setPower(maxPow);
                    motor1.setPower(maxPow);
                    motor2.setPower(maxPow);
                    motor3.setPower(maxPow);
                } catch (Exception e) {
                    Log.e("Error", "Cannot power motors right_bumper");
                }
            }
            if (driverGamepad.left_bumper){
                 try {
                    motor0.setPower(-maxPow);
                    motor1.setPower(-maxPow);
                    motor2.setPower(-maxPow);
                    motor3.setPower(-maxPow);
            }
                 catch (Exception e) {
                    Log.e("Error","Cannot power motors left_bumper");
                 }
        }
    }

}