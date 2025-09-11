package org.pionerds.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Drivetrain {

    public DcMotor[] motor = { null, null, null, null }; //front right

    static private double maxPow = 0.8;

    public void setDriveMotorsPow(double[] powVals){
        motor[0].setPower(powVals[0]);
        motor[1].setPower(powVals[1]);
        motor[2].setPower(powVals[2]);
        motor[3].setPower(powVals[3]);
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
              
            } catch (Exception e) {
                Log.e("Error","Cannot power motors dpad_down");
            }
        }
        if (driverGamepad.dpad_up){
            try {

            }
            } catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_up");
            }
        }
        if (driverGamepad.dpad_left){
            try {

            } catch (Exception e) {
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

                    motor[0].setPower(maxPow);
                    motor[1].setPower(maxPow);
                    motor[2].setPower(maxPow);
                    motor[3].setPower(maxPow);
                } catch (Exception e) {
                    Log.e("Error", "Cannot power motors right_bumper");
                }
            }
            if (driverGamepad.left_bumper){

            try {
                motor[0].setPower(-maxPow);
                motor[1].setPower(-maxPow);
                motor[2].setPower(-maxPow);
                motor[3].setPower(-maxPow);
            } catch (Exception e) {
                Log.e("Error","Cannot power motors left_bumper");
            }
        }
    }
}