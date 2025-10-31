package org.pionerds.ftc.teamcode.Hardware;

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

    public boolean isInRange(AprilTagNames tagName){
        return (hardware.vision.getPioNerdAprilTag(tagName).range() < maxLaunchDistanceCM);
    }

    private double calculateMotorVelocity(double range){
        return range*1.5;
    }

    private double calculateMotorPower(double range){
        return (range*1.5)/maxLaunchDistanceCM;
    }

    double tickDelay = 100.0;
    double stopDelay = 1000.0;
    double lastTick = 0.0;
    double lastStopTick = 0.0;
    boolean stopPending = false;

    public void tick(AprilTagNames tagName, AimbotMotorMovement movementType, boolean stopRequested){
        if (hardware.elapsedTime.milliseconds() - lastTick > tickDelay) {
            lastTick = hardware.elapsedTime.milliseconds();

            PioNerdAprilTag pioTag = hardware.vision.getPioNerdAprilTag(tagName);

            double range = pioTag.range();

            // detect if it is in the launching range
            if (range < maxLaunchDistanceCM) {
                telemetry.addLine("\nLauncher Motor:");
                if(!stopPending){
                    if(movementType.equals(AimbotMotorMovement.VELOCITY)){
                        hardware.launcher.setLauncherVelocity(calculateMotorVelocity(range));
                    } else {
                        hardware.launcher.setLauncherVelocity(calculateMotorPower(range));
                    }

                    if(stopRequested){
                        hardware.storage.feed();
                    }
                } else if (hardware.elapsedTime.milliseconds() - lastStopTick > stopDelay) {
                    // waited delay, so it's time to stop the motor!
                    hardware.launcher.stopLaunchers();
                    hardware.storage.contract();
                    stopPending = false;
                }
                if(stopRequested && !stopPending){
                    stopPending = true;
                    lastStopTick = lastTick;
                }
            } else {
                telemetry.addLine("Out of range!");
            }
        }

    }

}
