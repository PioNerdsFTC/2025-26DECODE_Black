package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.PioNerdAprilTag;
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
        while (opModeIsActive() //&& hardware.continueRunning
         ) {
            hardware.tick(gamepad1);

            // Add AprilTagPoseFtc data to Telemetry
            PioNerdAprilTag blueTargetAprilTag;
            blueTargetAprilTag = hardware.vision.getPioNerdAprilTag(AprilTagNames.BlueTarget);
            if(blueTargetAprilTag != null){
                telemetry.addLine("BlueTarget Distances");
                telemetry.addLine("x: "+blueTargetAprilTag.x());
                telemetry.addLine("y: "+blueTargetAprilTag.y());
                telemetry.addLine("z: "+blueTargetAprilTag.z());
                telemetry.addLine("Range: "+blueTargetAprilTag.range());
                telemetry.addLine("Pythag A,B: "+(Math.sqrt(Math.pow((blueTargetAprilTag.x()),2)) + Math.pow((blueTargetAprilTag.x()),2)));
                hardware.launcher.launcher0.setVelocity(blueTargetAprilTag.range());
                hardware.launcher.launcher1.setVelocity(blueTargetAprilTag.range());
            }


            telemetry.update();
            sleep(1);
        }

        hardware.stop();
    }
}
