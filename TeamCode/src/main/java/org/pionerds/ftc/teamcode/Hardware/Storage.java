package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

public class Storage {
    private Hardware hardware;

    private CRServo feederServo;
    private DcMotorEx susanMotorEx;
    private DcMotorEx intakeMotorEx;
    private Servo bumpUpServo;
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


        CRServo feeder = this.hardware.mapping.getContinuousServo("feeder", DcMotorSimple.Direction.FORWARD);
        Servo bumpUpFeeder = this.hardware.mapping.getServoMotor("bumpUp");
        DcMotorEx intake = this.hardware.mapping.getMotor("intake",40.0,DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT);
        DcMotorEx susan = this.hardware.mapping.getMotor("susanMotor", 40.0, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);

        if (feeder != null && bumpUpFeeder != null && susan != null && intake != null) {
            feederServo = feeder;
            bumpUpServo = bumpUpFeeder;
            susanMotorEx = susan;
            susanMotorEx.setTargetPositionTolerance(1);
            intakeMotorEx = intake;
            isInitialized = true;
        } else {
            hardware.telemetry.clearAll();
            hardware.telemetry.addLine("STORAGE IS NOT INITIALIZED!");
        }
    }


    public void enableFeeder() {
        if (!isInitialized) return;
        feederServo.setPower(1);
        if(isSusanInPosition(1)) {
            bumpUpServo.setPosition(0.05);
        } else {
            hardware.telemetry.addLine("Susan Not In Position.");
        }
    }

    public void disableFeeder() {
        if (!isInitialized) return;
        feederServo.setPower(0);
        bumpUpServo.setPosition(0);
    }

    public Artifact[] getInventory(){
        return inventory;
    }

    public void setArtifact(Artifact artifact, int position){
        inventory[position] = artifact;
    }

    public void enableIntake(){
        if (!isInitialized) return;
        intakeMotorEx.setPower(0.7);
    }

    public void disableIntake(){
        if (!isInitialized) return;
        intakeMotorEx.setPower(0);
    }

    public void disableIntake(Artifact artifact){
        if (!isInitialized) return;
        intakeMotorEx.setPower(0);
        // Extract position index from enum name (e.g., "INTAKE1" or "OUTPUT1" -> index 0)
        try {
            char lastChar = currentSusanPositionEnum.name().charAt(currentSusanPositionEnum.name().length() - 1);
            if (Character.isDigit(lastChar)) {
                int index = Character.getNumericValue(lastChar) - 1; // Convert 1-based to 0-based
                if (index >= 0 && index < inventory.length) {
                    inventory[index] = artifact;
                }
            }
        } catch (Exception e) {
            // Log error but don't crash
            hardware.telemetry.addLine("Error updating inventory: " + e.getMessage());
        }
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
        Artifact bestArtifact = bestArtifact(ballsOnRamp);
        LazySusanPositions targetPos = bestBallPos(currentSusanPositionEnum, bestArtifact.name());
        hardware.telemetry.addLine("\n========== STORAGE ==========");
        hardware.telemetry.addLine("CurrentPos: "+currentSusanPositionEnum.name());
        hardware.telemetry.addLine("TargetPos: "+targetPos.name());
        hardware.telemetry.addLine("BestArtifact: "+bestArtifact.name());
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
        // Extract the numeric index from the position name (e.g., "OUTPUT1" -> 0, "INTAKE2" -> 1)
        // This works because both INTAKE and OUTPUT positions use 1-based numbering
        int pos = 0;
        try {
            char lastChar = currentPos.charAt(currentPos.length() - 1);
            if (Character.isDigit(lastChar)) {
                pos = Character.getNumericValue(lastChar) - 1; // Convert 1-based to 0-based index
                // Ensure pos is within valid range [0, 2]
                if (pos < 0 || pos > 2) {
                    pos = 0;
                }
            }
        } catch (Exception e) {
            pos = 0; // Default to first position on error
        }
        if (inventory[pos].name().equals(idealColor)) {
            finalPos = positions[pos];
        }
        // Check one position counter-clockwise (two positions clockwise in circular array)
        else if (inventory[((pos+2)%3)].name().equals(idealColor)) {
            finalPos = positions[((pos+2)%3)];
        }
        // Check one position clockwise
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
        if (!isInitialized) return 0;
        return susanMotorEx.getCurrentPosition();
    }

    public boolean isSusanInPosition(int ticks){
        if (!isInitialized) return false;
        return (Math.abs(susanTargetTicks-getSusanCurrentTicks())<ticks);
    }

}
