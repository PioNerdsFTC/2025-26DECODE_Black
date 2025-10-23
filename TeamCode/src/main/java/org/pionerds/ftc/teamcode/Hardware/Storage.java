package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

public class Storage {
    Hardware hardware = null;

    private Servo servo0;
    private Servo servo1;
    public DcMotorEx susanMotorEx;
    private int susanTargetTicks = 0;
    private final int susanVelocityRequest = 300;
    private final int gearRatio = 6; // equals (90/15)
    private final int TPR = 288 * gearRatio; // ticks-per-revolution

    boolean isInitialized = false;

    Storage() { }

    public void init(Hardware hardware) {
        this.hardware = hardware;

        if(this.hardware.mapping.getServoMotor("servo0") != null && this.hardware.mapping.getServoMotor("servo1") != null && this.hardware.mapping.getMotor("susanMotor",40.0, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE) != null){
            servo0 = this.hardware.mapping.getServoMotor("servo0");
            servo1 = this.hardware.mapping.getServoMotor("servo1");
            susanMotorEx = this.hardware.mapping.getMotor("susanMotor",40.0, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
            susanMotorEx.setTargetPositionTolerance(1);
            isInitialized = true;
        } else {
            hardware.telemetry.clearAll();
            hardware.telemetry.addLine("STORAGE IS NOT INITIALIZED!");
        }
    }


    public void feed() {
        if(!isInitialized) return;
        double pos = 1;

        servo0.setPosition(pos);
        servo1.setPosition(pos);
    }

    public void contract() {
        if(!isInitialized) return;
        double pos = 0;

        servo0.setPosition(pos);
        servo1.setPosition(pos);
    }

    public void moveSusanTo(LazySusanPositions susanPosition){
        if(!isInitialized) return;

        int addTick = 0;  // default: ((0 * 360)*(TPR/360)) = 0
        int currentPos = susanMotorEx.getCurrentPosition();
        int revolutions = currentPos/TPR ; // ((int)/(int)) auto truncates YAY! lol

        switch (susanPosition) {
            case INTAKE2:
                addTick = 96; // ((1/3 * 360)*(TPR/360)) = 96
                break;
            case INTAKE3:
                addTick = 192; // ((2/3 * 360)*(TPR/360)) = 192
                break;
            case OUTPUT1:
                addTick = 48; // (((180 - (1/3) * 360))*(TPR/360)) = 48
                break;
            case OUTPUT2:
                addTick = 144; // (((180 - (0) * 360))*(TPR/360)) = 180
                break;
            case OUTPUT3:
                addTick = 240; // (((180 + (1/3) * 360))*(tpr/360)) = 240
                break;
        }

        addTick *= gearRatio;

        int currentRevolutionTick = revolutions * TPR + addTick;
        int lessRevolutionTick = currentRevolutionTick - TPR;
        int moreRevolutionTick = currentRevolutionTick + TPR;

        int distanceBetweenNowAndCurrent = Math.abs(currentRevolutionTick - currentPos);
        int distanceBetweenNowAndPrevious = Math.abs(lessRevolutionTick - currentPos);
        int distanceBetweenNowAndNext = Math.abs(moreRevolutionTick - currentPos);

        // find closest revolution with the tick
        if(distanceBetweenNowAndPrevious <= distanceBetweenNowAndCurrent && distanceBetweenNowAndPrevious <= distanceBetweenNowAndNext){
            susanTargetTicks = lessRevolutionTick;
        } else if(distanceBetweenNowAndCurrent <= distanceBetweenNowAndPrevious && distanceBetweenNowAndCurrent <= distanceBetweenNowAndNext){
            susanTargetTicks = currentRevolutionTick;
        } else {
            susanTargetTicks = moreRevolutionTick;
        }

        hardware.telemetry.addLine("\n\n");
        hardware.telemetry.addLine("susanPosition: "+susanMotorEx.getCurrentPosition());
        hardware.telemetry.addLine("susanTarget: "+susanTargetTicks);
        hardware.telemetry.addLine("susanRunMode: "+susanMotorEx.getMode());

        updateSusan();
    }


    public void updateSusan(){
        if(!isInitialized) return;
        susanMotorEx.setTargetPosition(susanTargetTicks);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        susanMotorEx.setPower(1);
    }


    public void testRotateSusan(double power){
        if(!isInitialized) return;
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        susanMotorEx.setPower(power);
    }

    public void stopSusan(){
        if(!isInitialized) return;
        susanMotorEx.setVelocity(0);
        susanMotorEx.setPower(0);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void resetEncoderSusan(){
        if(!isInitialized) return;
        susanMotorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
