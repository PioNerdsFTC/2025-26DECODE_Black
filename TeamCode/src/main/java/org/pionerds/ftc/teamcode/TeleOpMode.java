package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.VisionCommands;

@TeleOp(name = "TeleOp")
public class TeleOpMode extends LinearOpMode {
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
            hardware.tick(gamepad1);

            // Add AprilTagPoseFtc data to Telemetry
            AprilTagPoseFtc distanceToBlueTarget;
            distanceToBlueTarget = hardware.vision.getTagPosition(AprilTagNames.BlueTarget);
            if(distanceToBlueTarget != null){
                telemetry.addLine("BlueTarget Distances");
                telemetry.addLine("x: "+Math.round(distanceToBlueTarget.x*100)/100);
                telemetry.addLine("y: "+Math.round(distanceToBlueTarget.y*100)/100);
                telemetry.addLine("z: "+Math.round(distanceToBlueTarget.z*100)/100);
                telemetry.addLine("Range: "+(Math.round(distanceToBlueTarget.range * ((double)61/356) * 100)) / 100);
                telemetry.addLine("Pythag A,B: "+(Math.sqrt(Math.pow((distanceToBlueTarget.x),2)) + Math.pow((distanceToBlueTarget.y),2)));

            }

            telemetry.update();
            sleep(1);
        }

        hardware.stop();
    }
}
