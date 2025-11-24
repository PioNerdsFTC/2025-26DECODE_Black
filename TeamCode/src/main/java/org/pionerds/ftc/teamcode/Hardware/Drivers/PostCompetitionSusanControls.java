package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;

public class PostCompetitionSusanControls extends DriverControls {
    boolean printTelemetryData;
    boolean movingSusan;
    boolean adjustingSusan;
    LazySusanPositions[] lazySusanPositions;
    AprilTagNames targetName;
    double maxSusanVelocity;
    boolean isLauncherOn;
    boolean isTogglingLauncher;
    boolean isFeederOn;
    boolean isTogglingFeeder;
    boolean isIntakeOn;
    boolean isTogglingIntake;
    boolean isIntakeIn;

    public PostCompetitionSusanControls(
            String driverName,
            float maxSpeed,
            boolean isRed,
            boolean printTelemetryData
    ) {
        super(driverName, maxSpeed);
        this.printTelemetryData = printTelemetryData;
        targetName = (isRed ? AprilTagNames.RedTarget : AprilTagNames.BlueTarget);
        movingSusan = false;
        lazySusanPositions = LazySusanPositions.values();
        maxSusanVelocity = 300;
        isLauncherOn = false;
        isTogglingLauncher = false;
        isFeederOn = false;
        isTogglingFeeder = false;
        isIntakeOn = false;
        isTogglingIntake = false;
        isIntakeIn = false;
    }

    @Override
    public void tickControls(Gamepad gamepad, Hardware hardware) {

        LazySusanPositions currentPos = hardware.storage.getCurrentSusanPositionEnum();

        // Control Lazy Susan - Adjust
        if((gamepad.left_trigger > 0.1 || gamepad.right_trigger > 0.1) && !movingSusan) {
            hardware.storage.sendVelocitySusan(maxSusanVelocity * (gamepad.right_trigger - gamepad.left_trigger));
            adjustingSusan = true;

        // Control Lazy Susan - Dpad-Up/Dpad-Down Y/A
        } else if(gamepad.dpad_up && !movingSusan && !adjustingSusan){
            if(hardware.storage.isIntakeState()){
                if(currentPos.equals(LazySusanPositions.INTAKE1)) hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
                else if(currentPos.equals(LazySusanPositions.INTAKE2)) hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
                else if(currentPos.equals(LazySusanPositions.INTAKE3)) hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
                movingSusan = true;
            } else {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
                movingSusan = true;
            }
        } else if(gamepad.dpad_down && !movingSusan && !adjustingSusan){
            if(hardware.storage.isIntakeState()){
                if(currentPos.equals(LazySusanPositions.INTAKE1)) hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
                else if(currentPos.equals(LazySusanPositions.INTAKE3)) hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
                else if(currentPos.equals(LazySusanPositions.INTAKE2)) hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
                movingSusan = true;
            } else {
                hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
                movingSusan = true;
            }
        } else if(gamepad.y && !movingSusan && !adjustingSusan){
            if(hardware.storage.isIntakeState()){
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
                movingSusan = true;
            } else {
                if(currentPos.equals(LazySusanPositions.OUTPUT1)) hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
                else if(currentPos.equals(LazySusanPositions.OUTPUT2)) hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
                else if(currentPos.equals(LazySusanPositions.OUTPUT3)) hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
                movingSusan = true;
            }
        } else if(gamepad.a && !movingSusan && !adjustingSusan){
            if(hardware.storage.isIntakeState()){
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
                movingSusan = true;
            } else {
                if(currentPos.equals(LazySusanPositions.OUTPUT3)) hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
                else if(currentPos.equals(LazySusanPositions.OUTPUT2)) hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
                else if(currentPos.equals(LazySusanPositions.OUTPUT1)) hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
                movingSusan = true;
            }
        } else if(!(gamepad.dpad_up || gamepad.dpad_down || gamepad.y || gamepad.a || hardware.storage.isSusanBusy())){
            movingSusan = false;
            if(gamepad.left_trigger <= 0.1 && gamepad.right_trigger <= 0.1) adjustingSusan = false;
        }
        if(!adjustingSusan && !movingSusan) {
            hardware.storage.stopSusan();
        }


        // Enable/Disable Launcher with Left Bumper Toggle Press
        if(gamepad.left_bumper && !isTogglingLauncher){
            if(isLauncherOn) hardware.launcher.setLauncherVelocity(0);
            isLauncherOn = !isLauncherOn;
            isTogglingLauncher = true;
        } else if(!gamepad.left_bumper) isTogglingLauncher = false;

        if(isLauncherOn) hardware.launcher.setLauncherVelocity(hardware.aimbot.calculateMotorVelocity(targetName));



        // Enable/Disable Feeder with Left Bumper Toggle Press
        if(gamepad.right_bumper && !isTogglingFeeder){
            if(!isFeederOn) hardware.storage.enableFeederManual();
            else hardware.storage.disableFeeder();
            isFeederOn = !isFeederOn;
            isTogglingFeeder = true;
        } else if(!gamepad.right_bumper) isTogglingFeeder = false;


        if(!isTogglingIntake){
            if(gamepad.dpad_left){
                if(!isIntakeOn){
                    hardware.storage.enableIntakeManual(1.00);
                    isIntakeIn = true;
                    isIntakeOn = true;
                } else if(isIntakeIn) {
                    hardware.storage.disableIntake();
                    isIntakeIn = false;
                    isIntakeOn = false;
                } else if(!isIntakeIn){
                    hardware.storage.enableIntakeManual(1.00);
                    isIntakeIn = true;
                    isIntakeOn = true;
                }
                isTogglingIntake = true;
            } else if(gamepad.dpad_right){
                if(!isIntakeOn){
                    hardware.storage.enableIntakeManual(-1.00);
                    isIntakeIn = false;
                    isIntakeOn = true;
                } else if(isIntakeIn) {
                    hardware.storage.enableIntakeManual(-1.00);
                    isIntakeIn = false;
                    isIntakeOn = true;
                } else if(!isIntakeIn){
                    hardware.storage.disableIntake();
                    isIntakeIn = false;
                    isIntakeOn = false;
                }
                isTogglingIntake = true;
            }
        }
        if(!(gamepad.dpad_left || gamepad.dpad_right)) isTogglingIntake = false;


        if(printTelemetryData){
            hardware.telemetry.addLine("========== OPERATOR CONTROLS ==========");
            hardware.telemetry.addLine("==========     Lazy Susan    ==========");
            hardware.telemetry.addLine("Susan Pos == "+currentPos.name());
            hardware.telemetry.addLine("Moving Susan == "+movingSusan);
            hardware.telemetry.addLine("Adjusting Susan == "+adjustingSusan);
            hardware.telemetry.addLine("==========   Aimbot System   ==========");
            hardware.telemetry.addLine("launcherOn == "+isLauncherOn);
            hardware.telemetry.addLine("togglingLauncher == "+isTogglingLauncher);
            hardware.telemetry.addLine("Request.v == "+hardware.aimbot.calculateMotorVelocity(targetName));
            hardware.telemetry.addLine("Launcher 0.v == "+hardware.launcher.launcher0.getVelocity());
            hardware.telemetry.addLine("Launcher 1.v == "+hardware.launcher.launcher1.getVelocity());
            hardware.telemetry.addLine("Launcher 0.c == "+hardware.launcher.launcher0.getCurrent(CurrentUnit.AMPS)+" AMPS");
            hardware.telemetry.addLine("Launcher 1.c == "+hardware.launcher.launcher1.getCurrent(CurrentUnit.AMPS)+" AMPS");
            hardware.telemetry.addLine("==========   Feeder System   ==========");
            hardware.telemetry.addLine("feederOn == "+isFeederOn);
            hardware.telemetry.addLine("togglingFeeder == "+isTogglingLauncher);
            hardware.telemetry.addLine("==========   Intake System   ==========");
            hardware.telemetry.addLine("intakeOn == "+isIntakeOn);
            hardware.telemetry.addLine("intakeIn == "+isIntakeIn);
            hardware.telemetry.addLine("togglingIntake == "+isTogglingIntake);
            hardware.telemetry.addLine("=======================================");
        }

    }

}
