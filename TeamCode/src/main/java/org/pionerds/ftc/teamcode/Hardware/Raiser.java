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
    private double[] driveMotorDesiredVelocities = {0.00,0.00,0.00,0.00};
    private int[] driveMotorPositions = {0,0,0,0};
    private double intendedHeadingDegree = 0.00;

    public void init(Hardware hardware){
        this.hardware = hardware;
        this.driveMotors = hardware.drivetrain.getMotors();
    }

    public void tune(){
        resetEncoders();
        setMotorPositions(3500,false,false);
        setMotorVelocities(300,false,false);
        updateMotorsPower();
    }

    public void tuneSide(){
        resetEncoders();
        setMotorPositions(3500,false,true);
        setMotorVelocities(300,false,true);
        updateMotorsPower();
    }

    public void tuneRotation(){
        resetEncoders();
        setMotorPositions(1000,true,false);
        setMotorVelocities(300,true,false);
        updateMotorsPower();
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

        setMotorPositions(position,false,false);
        setMotorVelocities(velocity,false,false);
        scaleMotorPowers();

        driveMotorVelocities = new double[]{-0.3,0.3,0.3,-0.3};

        updateMotorsPower();

        while (motorsBusy()){
            forwardCorrectionTick((0.05)*(getAngleDifference()));
            scaleMotorPowers();

            driveMotorVelocities = new double[]{-0.3,0.3,0.3,-0.3};

            updateMotorsPower();

            hardware.telemetry.addLine("Robot Gyro: "+hardware.gyro.getAngles()[0]);
            hardware.telemetry.addLine("Heading Gyro: "+intendedHeadingDegree);
            hardware.telemetry.addLine("Difference Angle: "+getAngleDifference());

            hardware.telemetry.addLine("\n");

            hardware.telemetry.addLine("Intended Driving Velocities:");
            hardware.telemetry.addLine("Motor0: "+driveMotorDesiredVelocities[0]);
            hardware.telemetry.addLine("Motor1: "+driveMotorDesiredVelocities[1]);
            hardware.telemetry.addLine("Motor2: "+driveMotorDesiredVelocities[2]);
            hardware.telemetry.addLine("Motor3: "+driveMotorDesiredVelocities[3]);

            hardware.telemetry.addLine("\n");

            hardware.telemetry.addLine("Set Driving Velocities:");
            hardware.telemetry.addLine("Motor0: "+driveMotorVelocities[0]);
            hardware.telemetry.addLine("Motor1: "+driveMotorVelocities[1]);
            hardware.telemetry.addLine("Motor2: "+driveMotorVelocities[2]);
            hardware.telemetry.addLine("Motor3: "+driveMotorVelocities[3]);

            hardware.telemetry.addLine("\n");

            hardware.telemetry.addLine("Set Driving Positions:");
            hardware.telemetry.addLine("Motor0: "+driveMotorPositions[0]);
            hardware.telemetry.addLine("Motor1: "+driveMotorPositions[1]);
            hardware.telemetry.addLine("Motor2: "+driveMotorPositions[2]);
            hardware.telemetry.addLine("Motor3: "+driveMotorPositions[3]);

            hardware.telemetry.addLine("\nwaiting on motors for linear movement...");
            hardware.telemetry.update();
        } // halts thread until it gets to position

    }

    public void driveByInches(double inches){
        driveByInches(inches, 500.00);
    }

    public void driveByInchesRight(double inches, double velocity){
        resetEncoders();
        int position = (int) (inches * ticksPerInches);

        setMotorPositions(position,false,true);
        setMotorVelocities(velocity,false,true);
        scaleMotorPowers();

        updateMotorsPower();

        while (motorsBusy() && hardware.continueRunning){
            hardware.telemetry.addLine("waiting on motors for linear movement and correcting...");
            hardware.telemetry.update();
        } // halts thread until it gets to position

    }

    public void driveByInchesRight(double inches){
        driveByInchesRight(inches, 500.00);
    }

    private double getAngleDifference(){
        return hardware.gyro.getAngles()[0]-intendedHeadingDegree;
    }

    public void driveByDegrees(double degrees, double velocity){
        resetEncoders();
        int position = (int) (degrees * ticksPerDegree);

        setMotorPositions(position,true,false);
        setMotorVelocities(velocity,true,false);
        scaleMotorPowers();

        intendedHeadingDegree += degrees;

        updateMotorsPower();

        while (motorsBusy() && hardware.continueRunning){
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
            motor.setTargetPositionTolerance(5);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(driveMotorVelocities[i]);
        }
    }

    private void updatePositions(){
        for(int i=0; i<driveMotors.length; i++){
            DcMotorEx motor = driveMotors[i];
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setTargetPosition(driveMotorPositions[i]);
            motor.setTargetPositionTolerance(5);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    private void updateMotorsPower(){
        for(int i=0; i<driveMotors.length; i++){
            DcMotorEx motor = driveMotors[i];
            motor.setPower(driveMotorVelocities[i]);
        }
    }

    private void setMotorVelocities(double velocity, boolean rotate, boolean right){
        int rotateFactor = (rotate ? 1 : -1);
        int rightFactor = (right ? 1 : -1);
        if(!rotate) {
            driveMotorDesiredVelocities[0] = rightFactor * rotateFactor * velocity;
            driveMotorDesiredVelocities[1] = velocity;
            driveMotorDesiredVelocities[2] = rightFactor * velocity;
            driveMotorDesiredVelocities[3] = rotateFactor * velocity;
        } else {
            driveMotorVelocities[0] = rightFactor * rotateFactor * velocity;
            driveMotorVelocities[1] = velocity;
            driveMotorVelocities[2] = rightFactor * velocity;
            driveMotorVelocities[3] = rotateFactor * velocity;
        }
    }

    private void forwardCorrectionTick(double endBringer){
        driveMotorVelocities[0] = driveMotorDesiredVelocities[0] - endBringer;
        driveMotorVelocities[1] = driveMotorDesiredVelocities[0] + endBringer;
        driveMotorVelocities[2] = driveMotorDesiredVelocities[0] - endBringer;
        driveMotorVelocities[3] = driveMotorDesiredVelocities[0] + endBringer;
    }

    private void setMotorPositions(int position, boolean rotate, boolean right){
        int rotateFactor = (rotate ? 1 : -1);
        int rightFactor = (right ? -1 : 1);
        driveMotorPositions[0] = rightFactor*rotateFactor*position;
        driveMotorPositions[1] = position;
        driveMotorPositions[2] = rightFactor*position;
        driveMotorPositions[3] = rotateFactor*position;
        updatePositions();
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


    private void scaleMotorPowers(){
        double scaleToNumber = 0.00;
        boolean scale = false;

        for(double num: driveMotorVelocities){
            if(num > 1) {
                scale = true;
                if (num > scaleToNumber) {
                    scaleToNumber = num;
                }
            }
        }

        if(scale){
            for(int i = 0; i<driveMotorVelocities.length; i++){
                driveMotorVelocities[i] = driveMotorVelocities[i] / scaleToNumber * 1;
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
