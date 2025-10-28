package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;

public class Aimbot {

    private Hardware hardware;
    private Telemetry telemetry;
    private DriverControls controls;
    private double maxLaunchDistanceCM;
    private boolean aimbotEnabled = false;



    public void init(Hardware hardware, Telemetry telemetry, DriverControls controls, double maxLaunchDistanceCM, AprilTagNames tagName, AimbotMotorMovement motorMoveType) {
        this.hardware = hardware;
        this.telemetry = telemetry;
        this.controls = controls;
        this.maxLaunchDistanceCM = maxLaunchDistanceCM;
        aimbotEnabled = true;
    }



    public void revLauncherPower(AprilTagNames tagName){
        double range = hardware.vision.getPioNerdAprilTag(tagName).range();

        hardware.launcher.setLauncherPower(Math.min(range,1));


    }

    public boolean isOutOfRange(AprilTagNames tagName){
        return (hardware.vision.getPioNerdAprilTag(tagName).range() < maxLaunchDistanceCM);
    }

    private double calculateMotorVelocity(double range){
        return range*1.5;
    }

    private double calculateMotorPower(double range){
        return (range*1.5)/maxLaunchDistanceCM;
    }

    double tickDelay = 100.0;
    double lastTick = 0.0;
    public void tick(AprilTagNames tagName, AimbotMotorMovement movementType){
        if (lastTick + tickDelay < hardware.elapsedTime.milliseconds()) {


            lastTick = hardware.elapsedTime.milliseconds();

            PioNerdAprilTag pioTag = hardware.vision.getPioNerdAprilTag(tagName);
            AprilTagDetection unwrappedDetection = pioTag.getAprilTagDetection();


            double range = pioTag.range();

            // detect if it is in the launching range
            if (range < maxLaunchDistanceCM) {
                telemetry.addLine("\nLauncher Motor:");
                if (movementType == AimbotMotorMovement.VELOCITY) {
                    hardware.launcher.setLauncherVelocity(calculateMotorVelocity(range));
                } else { // USE POWER FOR THIS ONE!
                    hardware.launcher.setLauncherPower(calculateMotorPower(range));
                }
            } else {
                telemetry.clearAll();
                telemetry.addLine("Out of range!");
            }
        }

    }

}
