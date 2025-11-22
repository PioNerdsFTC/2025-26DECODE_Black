package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Raiser {
    private final double ticksPerInches = (3500/93.75);
    private final double maxVelocity = 2500.00;
    private Hardware hardware;
    private DcMotorEx[] driveMotors;
    private double[] driveMotorVelocities = {0.00,0.00,0.00,0.00};
    private int[] driveMotorPositions = {0,0,0,0};

    public void init(Hardware hardware){
        this.hardware = hardware;
        this.driveMotors = hardware.drivetrain.getMotors();
    }

    public void tune(){
        resetEncoders();
        setMotorPositions(3500);
        setMotorVelocities(500);
        updateMotors();
    }

    public void tunePrint(){
        for(int i=0; i<4; i++){
            hardware.telemetry.addLine("======= Motor"+i+" =======");
            hardware.telemetry.addLine("= Velocity: "+driveMotors[i].getVelocity());
            hardware.telemetry.addLine("= Target Pos: "+driveMotors[i].getTargetPosition());
            hardware.telemetry.addLine("= Current Pos: "+driveMotors[i].getCurrentPosition());
            hardware.telemetry.addLine("= Current: "+driveMotors[i].getCurrent(CurrentUnit.AMPS)+" Amps\n\n");
        }
    }

    public void resetEncoders(){
        for(DcMotorEx motor : driveMotors){
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    public void driveByInches(double inches){
        int position = (int) (inches * ticksPerInches);
        double velocity = 1000.00;

        setMotorPositions(position);
        setMotorVelocities(1000);
        scaleMotorVelocities();

        updateMotors();

        while (motorsBusy()){} // halts thread until it gets to position

    }

    private boolean motorsBusy(){
        for(DcMotorEx motor : driveMotors){
            if(motor.isBusy()) return true;
        }
        return false;
    }

    private void updateMotors(){
        for(int i=0; i<driveMotors.length; i++){
            DcMotorEx motor = driveMotors[i];
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setTargetPosition(driveMotorPositions[i]);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(driveMotorVelocities[i]);
        }
    }

    public void setMotorVelocities(double velocity){
        driveMotorVelocities[0] = -velocity;
        driveMotorVelocities[1] = velocity;
        driveMotorVelocities[2] = velocity;
        driveMotorVelocities[3] = -velocity;
    }

    public void setMotorPositions(int position){
        driveMotorPositions[0] = -position;
        driveMotorPositions[1] = position;
        driveMotorPositions[2] = position;
        driveMotorPositions[3] = -position;
    }

    public void scaleMotorVelocities(){

        double scaleToNumber = 0.00;
        boolean scale = false;

        for(double num: driveMotorVelocities){
            if(num > maxVelocity) {
                scale = true;
                if (num > scaleToNumber) {
                    scaleToNumber = num;
                }
            }
        }

        if(scale){
            for(int i = 0; i<driveMotorVelocities.length; i++){
                driveMotorVelocities[i] = driveMotorVelocities[i] / scaleToNumber * maxVelocity;
            }
        }
    }

}
