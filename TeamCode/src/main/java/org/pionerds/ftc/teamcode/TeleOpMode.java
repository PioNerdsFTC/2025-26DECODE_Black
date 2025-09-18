package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
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

            // add obelisk stuff to telemetry
            telemetry.addLine("obeliskIdentified: "+hardware.vision.isObeliskIdentified());
            for(AprilTagMetadata metadata: hardware.vision.currentDetectionsMetadata()){
                telemetry.addLine("AprilTag: \""+metadata.name+"\"");
                telemetry.addLine("" + metadata.id);
            }



            for(int i=0;i<3;i++){
                Artifact a = hardware.vision.getArtifactPattern()[i];
                telemetry.addLine("Artifact: " + a.name());
            }

            telemetry.update();
            sleep(1);
        }

        hardware.stop();
    }
}
