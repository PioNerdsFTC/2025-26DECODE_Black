package org.pionerds.ftc.teamcode.Hardware;

import android.util.Log;

public class SusanAutoAlgorithms {
    Hardware hardware;

    public void init(Hardware hardware){
        this.hardware = hardware;
    }

    public String aprilTagBallOrderFormatter() {
        Artifact[] pattern = hardware.vision.getArtifactPattern();
        String result ="";
        for (Artifact a : pattern) {
            if (a.name().equals("PURPLE")) {
                result+="P";
            }
            else if (a.name().equals("GREEN")) {
                result+="G";
            }
            else {result+="E";}
        }

        return result;
    }


    ///  Assumes initial susan position of:
    /// output1 = purple
    /// output2 = purple
    /// output3 = green
    ///
    /// Return String is just for error checking (or potentially telemetry)
    public String launchBallsAutoInitial(String obeliskOrder) {
        String result = "";
        AprilTagNames targetAprilTags = AprilTagNames.BlueTarget;
        boolean initial_purple = false;
        if (obeliskOrder.charAt(0)=='P') {
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);

            launchBall(targetAprilTags);
            initial_purple = true;
            result+="SUCCESS-PURPLE; ";
        }
        else if (obeliskOrder.charAt(0)=='G') {
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
            launchBall(targetAprilTags);
            result+="SUCCESS-GREEN";
        }
        else {
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
            result+="FAIL";
        }
        if (obeliskOrder.charAt(1)=='P'){
            if(!initial_purple) {
                hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
            }
            else {hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);}
            launchBall(targetAprilTags);
        }
        else if (obeliskOrder.charAt(1)=='G') {
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
            launchBall(targetAprilTags);
        }
        else {
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
            launchBall(targetAprilTags);
        }
        if (obeliskOrder.charAt(2)=='P'){
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
            launchBall(targetAprilTags);
        }
        else if (obeliskOrder.charAt(2)=='G') {
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
            launchBall(targetAprilTags);
        }
        else {
            hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
            launchBall(targetAprilTags);
        }
        return result;
    }

    public void launchBall(AprilTagNames target) {
        if(hardware.vision.getPioNerdAprilTag(target).getAprilTagDetection() != null){
            hardware.launcher.setLauncherVelocity(hardware.aimbot.calculateMotorVelocity(target));
        }
        else {
            hardware.launcher.setLauncherVelocity(1000);
        }
        hardware.storage.enableFeederManual();

        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            Log.e("error","Insomnia");
        }
        hardware.storage.disableFeeder();
        hardware.launcher.setLauncherVelocity(0);
    }


}
