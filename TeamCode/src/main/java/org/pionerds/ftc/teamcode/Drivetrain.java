package org.pionerds.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Drivetrain {

    public DcMotor[] motors = { null, null, null, null }; //front right, front left, back left, back right

    static private double maxPow = 0.8;

    public void setDriveMotorsPow(double[] powVals){
        motors[0].setPower(powVals[0]);
        motors[1].setPower(powVals[1]);
        motors[2].setPower(powVals[2]);
        motors[3].setPower(powVals[3]);
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
                motors[0].setPower(maxPow);
                motors[1].setPower(maxPow);
                motors[2].setPower(maxPow);
                motors[3].setPower(maxPow);
            } catch (Exception e) {
                Log.e("Error","Cannot power motors dpad_down");
            }
        }
        if (driverGamepad.dpad_up){
            try {
                motors[0].setPower(maxPow);
                motors[1].setPower(maxPow);
                motors[2].setPower(maxPow);
                motors[3].setPower(maxPow);

            } catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_up");
            }
        }
        if (driverGamepad.dpad_left){
            try {
                motors[0].setPower(maxPow);
                motors[1].setPower(maxPow);
                motors[2].setPower(maxPow);
                motors[3].setPower(maxPow);

            } catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_left");
            }
        }
        if (driverGamepad.dpad_right){
            try {
                motors[0].setPower(maxPow);
                motors[1].setPower(maxPow);
                motors[2].setPower(maxPow);
                motors[3].setPower(maxPow);

            }
            catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_right");
            }
        }
    }

    public void bumperTurn(Gamepad driverGamepad){
            if (driverGamepad.right_bumper) {
                try {
                    motors[0].setPower(maxPow);
                    motors[1].setPower(maxPow);
                    motors[2].setPower(maxPow);
                    motors[3].setPower(maxPow);
                } catch (Exception e) {
                    Log.e("Error", "Cannot power motors right_bumper");
                }
            }
            if (driverGamepad.left_bumper){

            try {
                motors[0].setPower(-maxPow);
                motors[1].setPower(-maxPow);
                motors[2].setPower(-maxPow);
                motors[3].setPower(-maxPow);
            } catch (Exception e) {
                Log.e("Error","Cannot power motors left_bumper");
            }
        }
    }
}