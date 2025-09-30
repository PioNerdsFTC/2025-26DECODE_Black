package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

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
        while (opModeIsActive()) {

          
            // Add AprilTagPoseFtc data to Telemetry
            /*PioNerdAprilTag obeliskTagGPP;
            obeliskTagGPP = hardware.vision.getPioNerdAprilTag(AprilTagNames.Obelisk_GPP);
            if(obeliskTagGPP != null){
                telemetry.addLine("Blue Target Range: "+obeliskTagGPP.range(2)+"cm");
                if (obeliskTagGPP.range()<50){
                    hardware.storage.feed();
                } else {
                    hardware.storage.contract();
                }
            }
            */

            telemetry.update();
            sleep(1);
        }


        hardware.stop();
    }
}
