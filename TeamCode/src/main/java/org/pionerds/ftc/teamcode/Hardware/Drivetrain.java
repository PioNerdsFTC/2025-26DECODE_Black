package org.pionerds.ftc.teamcode.Hardware;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.*;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Drivetrain {

    Hardware hardware = null;

    public DcMotor[] motors = { null, null, null, null }; //front right, front left, back left, back right

    public void init(Hardware hardware) {
        this.hardware = hardware;

        motors[0] = this.hardware.mapping.mapMotor("motor0", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT);
        motors[1] = this.hardware.mapping.mapMotor("motor1", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT);
        motors[2] = this.hardware.mapping.mapMotor("motor2", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT);
        motors[3] = this.hardware.mapping.mapMotor("motor3", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT);
    }

    static private final double maxPow = 0.8;

    public void setDriveMotorsPow(double motor0, double motor1, double motor2, double motor3){
        motors[0].setPower(motor0);
        motors[1].setPower(motor1);
        motors[2].setPower(motor2);
        motors[3].setPower(motor3);
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