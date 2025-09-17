package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.VisionCommands;

@TeleOp(name = "TeleOp")
public class TeleOpMode extends LinearOpMode {
    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart(); // Wait for start!

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();

        // Main Loop!
        while (opModeIsActive() && hardware.continueRunning) {
            hardware.tick(gamepad1);

            for(String detectionName: hardware.vision.currentDetectionsNames()){
                telemetry.addLine(detectionName);
            }

            telemetry.update();
            sleep(1);
        }

        hardware.stop();
    }
}
