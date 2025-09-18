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

        motors[0] = this.hardware.mapping.getMotor("motor0", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motors[1] = this.hardware.mapping.getMotor("motor1", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motors[2] = this.hardware.mapping.getMotor("motor2", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motors[3] = this.hardware.mapping.getMotor("motor3", 40.0, Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
    }

    static private final double maxPow = 0.8;

    public void setDriveMotorsPow(double pow0, double pow1, double pow2, double pow3){
        motors[0].setPower(pow0);
        motors[1].setPower(pow1);
        motors[2].setPower(pow2);
        motors[3].setPower(pow3);
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
                motors[1].setPower(-maxPow);
                motors[2].setPower(maxPow);
                motors[3].setPower(-maxPow);
            } catch (Exception e) {
                Log.e("Error","Cannot power motors dpad_down");
            }
        }
        else if (driverGamepad.dpad_up){
            try {
                motors[0].setPower(-maxPow);
                motors[1].setPower(maxPow);
                motors[2].setPower(-maxPow);
                motors[3].setPower(maxPow);

            } catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_up");
            }
        }
        else if (driverGamepad.dpad_left){
            try {
                motors[0].setPower(maxPow);
                motors[1].setPower(maxPow);
                motors[2].setPower(-maxPow);
                motors[3].setPower(-maxPow);

            } catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_left");
            }
        }
        else if (driverGamepad.dpad_right){
            try {
                motors[0].setPower(-maxPow);
                motors[1].setPower(-maxPow);
                motors[2].setPower(maxPow);
                motors[3].setPower(maxPow);

            }
            catch (Exception e) {
                Log.e("Error", "Cannot power motors dpad_right");
            }
        }
        else {
            try {
                motors[0].setPower(0);
                motors[1].setPower(0);
                motors[2].setPower(0);
                motors[3].setPower(0);


            }
            catch (Exception e){
                Log.e("Error","Unable to power down motors");
            }
        }
    }

    public void bumperTurn(Gamepad driverGamepad){
            if (driverGamepad.right_bumper) {
                try {
                    motors[0].setPower(-maxPow);
                    motors[1].setPower(-maxPow);
                    motors[2].setPower(-maxPow);
                    motors[3].setPower(-maxPow);
                } catch (Exception e) {
                    Log.e("Error", "Cannot power motors right_bumper");
                }
            }
            if (driverGamepad.left_bumper){

            try {
                motors[0].setPower(maxPow);
                motors[1].setPower(maxPow);
                motors[2].setPower(maxPow);
                motors[3].setPower(maxPow);
            } catch (Exception e) {
                Log.e("Error","Cannot power motors left_bumper");
            }
        }
    }

    public void stopMotors(){
        for (DcMotor motor : motors){
            motor.setPower(0);
        }
    }

    public void stickDrive(Gamepad gamepad1){
        double x = gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;
        if (Math.abs(x)<0.2 && Math.abs(y)<0.2){
            stopMotors();
        }
        else {
            motors[0].setPower((-x-y)/2);
            motors[1].setPower((-x+y)/2);
            motors[2].setPower((x+y)/2);
            motors[3].setPower((x-y)/2);
        }
    }
}