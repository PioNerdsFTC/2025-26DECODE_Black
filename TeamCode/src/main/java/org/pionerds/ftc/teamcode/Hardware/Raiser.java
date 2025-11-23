package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Raiser {
    private final double ticksPerInches = (3500/93.75);
    private final double ticksPerDegree = (1000/-127); //find
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
        setMotorPositions(3500,false);
        setMotorVelocities(500,false);
        updateMotors();
    }

    public void tuneRotation(){
        resetEncoders();
        setMotorPositions(1000,true);
        setMotorVelocities(500,true);
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

    public void driveByInches(double inches, double velocity){
        resetEncoders();
        int position = (int) (inches * ticksPerInches);

        setMotorPositions(position,false);
        setMotorVelocities(velocity,false);
        scaleMotorVelocities();

        updateMotors();

        while (motorsBusy()){
            hardware.telemetry.addLine("waiting on motors for linear movement...");
            hardware.telemetry.update();
        } // halts thread until it gets to position

    }

    public void driveByInches(double inches){
        driveByInches(inches, 1000.00);
    }

    public void driveByDegrees(double degrees, double velocity){
        resetEncoders();
        int position = (int) (degrees * ticksPerDegree);

        setMotorPositions(position,true);
        setMotorVelocities(velocity,true);
        scaleMotorVelocities();

        updateMotors();

        while (motorsBusy()){
            hardware.telemetry.addLine("waiting on motors for rotation...");
            hardware.telemetry.update();
        } // halts thread until it gets to position

    }

    public void driveByDegrees(double degrees){
        driveByDegrees(degrees,1000.00);
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
            motor.setTargetPositionTolerance(1);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(driveMotorVelocities[i]);
        }
    }

    private void setMotorVelocities(double velocity, boolean rotate){
        int rotateFactor = (rotate ? 1 : -1);
        driveMotorVelocities[0] = rotateFactor*velocity;
        driveMotorVelocities[1] = velocity;
        driveMotorVelocities[2] = velocity;
        driveMotorVelocities[3] = rotateFactor*velocity;
    }

    private void setMotorPositions(int position, boolean rotate){
        int rotateFactor = (rotate ? 1 : -1);

        driveMotorPositions[0] = rotateFactor*position;
        driveMotorPositions[1] = position;
        driveMotorPositions[2] = position;
        driveMotorPositions[3] = rotateFactor*position;
    }

    private void scaleMotorVelocities(){

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

    public void rotateToTarget(AprilTagNames tagName){
        PioNerdAprilTag piotag = hardware.vision.getPioNerdAprilTag(tagName);
        if(piotag != null){
            double angle = piotag.bearing(2);
            hardware.telemetry.addLine("TAG BEARING:\n"+angle);
            hardware.telemetry.addLine("TAG RANGE:\n"+piotag.range(2));

            driveByDegrees(-piotag.bearing(1));

        }
    }

}
