package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

public class Storage {
    private Hardware hardware;

    private Servo feederServo;
    private DcMotorEx susanMotorEx;
    private int susanTargetTicks = 0;
    private final int susanVelocityRequest = 300;
    private final int gearRatio = 3; // equals (90/30)
    private final int TPR = 288 * gearRatio; // ticks-per-revolution

    private boolean isInitialized = false;
    private Artifact[] inventory = new Artifact[] {Artifact.EMPTY, Artifact.EMPTY, Artifact.EMPTY};
    private LazySusanPositions currentSusanPositionEnum = LazySusanPositions.INTAKE1;
    Storage() {}

    public void init(Hardware hardware) {
        this.hardware = hardware;


        Servo feeder = this.hardware.mapping.getServoMotor("feeder");
        DcMotorEx susan = this.hardware.mapping.getMotor("susanMotor", 40.0, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);

        if (feeder != null && susan != null) {
            feederServo = feeder;
            susanMotorEx = susan;
            susanMotorEx.setTargetPositionTolerance(1);
            isInitialized = true;
        } else {
            hardware.telemetry.clearAll();
            hardware.telemetry.addLine("STORAGE IS NOT INITIALIZED!");
        }
    }


    public void feed() {
        if (!isInitialized) return;
        double pos = 1;

        feederServo.setPosition(pos);
    }

    public void contract() {
        if (!isInitialized) return;
        double pos = 0;

        feederServo.setPosition(pos);
    }

    public void moveSusanTo(LazySusanPositions susanPosition) {
        if (!isInitialized) return;
        if (susanPosition.equals(currentSusanPositionEnum)) return;

        int tickOffset = 0; // default: ((0 * 360)*(TPR/360)) = 0
        int currentPos = susanMotorEx.getCurrentPosition();
        int revolutions = currentPos / TPR; // ((int)/(int)) auto truncates YAY! lol

        switch (susanPosition) {
            case INTAKE2:
                tickOffset = 96; // ((1/3 * 360)*(TPR/360)) = 96
                break;
            case INTAKE3:
                tickOffset = 192; // ((2/3 * 360)*(TPR/360)) = 192
                break;
            case OUTPUT1:
                tickOffset = 48; // (((180 - (1/3) * 360))*(TPR/360)) = 48
                break;
            case OUTPUT2:
                tickOffset = 144; // (((180 - (0) * 360))*(TPR/360)) = 180
                break;
            case OUTPUT3:
                tickOffset = 240; // (((180 + (1/3) * 360))*(tpr/360)) = 240
                break;
        }
        currentSusanPositionEnum = susanPosition;
        tickOffset *= gearRatio;

        int currentRevolutionTick = revolutions * TPR + tickOffset;
        int lessRevolutionTick = currentRevolutionTick - TPR;
        int moreRevolutionTick = currentRevolutionTick + TPR;

        int distanceBetweenNowAndCurrent = Math.abs(
            currentRevolutionTick - currentPos
        );
        int distanceBetweenNowAndPrevious = Math.abs(
            lessRevolutionTick - currentPos
        );
        int distanceBetweenNowAndNext = Math.abs(
            moreRevolutionTick - currentPos
        );

        // find closest revolution with the tick
        if (
            distanceBetweenNowAndPrevious <= distanceBetweenNowAndCurrent &&
            distanceBetweenNowAndPrevious <= distanceBetweenNowAndNext
        ) {
            susanTargetTicks = lessRevolutionTick;
        } else if (
            distanceBetweenNowAndCurrent <= distanceBetweenNowAndPrevious &&
            distanceBetweenNowAndCurrent <= distanceBetweenNowAndNext
        ) {
            susanTargetTicks = currentRevolutionTick;
        } else {
            susanTargetTicks = moreRevolutionTick;
        }

        hardware.telemetry.addLine("\n\n");
        hardware.telemetry.addLine(
            "susanPosition: " + susanMotorEx.getCurrentPosition()
        );
        hardware.telemetry.addLine("susanTarget: " + susanTargetTicks);
        hardware.telemetry.addLine("susanRunMode: " + susanMotorEx.getMode());

        updateSusan();
    }

    public Artifact bestArtifact(int ballsOnRamp){
        Artifact[] pattern = hardware.vision.getArtifactPattern();

        return pattern[ballsOnRamp%3];
    }

    public void automatedSusan(int ballsOnRamp){

        moveSusanTo(bestBallPos(currentSusanPositionEnum, bestArtifact(ballsOnRamp).name()));

    }

    public void printAlgorithmData(int ballsOnRamp){
        hardware.telemetry.addLine("\n========== STORAGE ==========");
        hardware.telemetry.addLine("CurrentPos: "+currentSusanPositionEnum.name());
        hardware.telemetry.addLine("TargetPos: "+bestBallPos(currentSusanPositionEnum, bestArtifact(ballsOnRamp).name()).name());
        hardware.telemetry.addLine("BestArtifact: "+bestArtifact(ballsOnRamp).name());
        hardware.telemetry.addLine("\nInventory:");
        hardware.telemetry.addLine(inventory[0].name());
        hardware.telemetry.addLine(inventory[1].name());
        hardware.telemetry.addLine(inventory[2].name());
        hardware.telemetry.addLine("\n=============================");
    }

    public LazySusanPositions bestBallPos(LazySusanPositions currentPosEnum, String idealColor) {
        String currentPos = currentPosEnum.name();
        String finalPos = "OUTPUT1";
        String[] positions =
                {"OUTPUT1","OUTPUT2","OUTPUT3"};
        int pos = 0;
        for(int i = 0; i<3; i++){
            if(positions[i].substring(positions[i].length()-1).equals(currentPos.substring(currentPos.length()-1))){
                pos = i;
                break;
            }
        }
        if (inventory[pos].name().equals(idealColor)) {
            finalPos = positions[pos];
        }
        // check two spaces to the right (wraps to left) | AKA CHECKS TO THE LEFT!
        else if (inventory[((pos+2)%3)].name().equals(idealColor)) {
            finalPos = positions[((pos+2)%3)];
        }
        // check one space to the right (wraps to left)
        else if (inventory[((pos+1)%3)].name().equals(idealColor)) {
            finalPos = positions[((pos+1)%3)];
        }
        else {
            for(int i = 0; i<3; i++){
                if(!inventory[i].equals(Artifact.EMPTY)) finalPos = positions[i];
            }

        }
        for(LazySusanPositions susanPosReturn : LazySusanPositions.values()){
            if(susanPosReturn.name().equals(finalPos)){
                return susanPosReturn;
            }
        }
        return LazySusanPositions.OUTPUT1;
    }


    public void updateSusan() {
        if (!isInitialized) return;
        susanMotorEx.setTargetPosition(susanTargetTicks);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        susanMotorEx.setPower(1);
    }

    public void testRotateSusan(double power) {
        if (!isInitialized) return;
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        susanMotorEx.setPower(power);
    }

    public void stopSusan() {
        if (!isInitialized) return;
        susanMotorEx.setVelocity(0);
        susanMotorEx.setPower(0);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void resetEncoderSusan() {
        if (!isInitialized) return;
        susanMotorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public int getSusanCurrentTicks(){
        return susanMotorEx.getCurrentPosition();
    }

}
