package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "ReadPatternOpMode")
public class ReadPatternOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        AprilTagNames target = AprilTagNames.BlueTarget;

        waitForStart(); // Wait for start!


        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        // Main loop!
        while (opModeIsActive()) {

            Artifact[] pattern = hardware.vision.getArtifactPattern();
            telemetry.addLine("scanned: "+hardware.vision.getObeliskIdentified());

            for(Artifact art : pattern){
                telemetry.addLine("object: "+art.name());
            }
            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
