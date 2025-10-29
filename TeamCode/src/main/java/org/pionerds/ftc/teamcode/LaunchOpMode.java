package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "dont use")
public class LaunchOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart(); // Wait for start!

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();

        // Main loop!
        while (opModeIsActive() && hardware.continueRunning) {
            //hardware.tick(gamepad1);

            // Add AprilTagPoseFtc data to Telemetry
            AprilTagPoseFtc distanceToBlueTarget;
            distanceToBlueTarget = hardware.vision.getTagPosition(
                AprilTagNames.BlueTarget
            );
            double rangeToBlueTarget = 0.00;

            if (distanceToBlueTarget != null) {
                telemetry.addLine("BlueTarget Distance");
                telemetry.addLine(
                    "Range: " +
                        (Math.round(distanceToBlueTarget.range * 100)) / 100
                );
                rangeToBlueTarget = (double) ((Math.round(
                            distanceToBlueTarget.range * 100
                        )) /
                    100);
            }

            double conversionFactor = 7.00;
            rangeToBlueTarget *= conversionFactor;
            hardware.launcher.setLauncherVelocity(rangeToBlueTarget);

            telemetry.update();
            sleep(1);
        }

        hardware.stop();
    }
}
