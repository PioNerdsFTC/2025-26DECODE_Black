package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;

@Autonomous(name = "RaiserForwardToTheEndTimes")
public class RaiserForwardToTheEndTimes extends OpMode {

    final Hardware hardware = new Hardware();
    AprilTagNames target;

    @Override
    public void init() {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        target = AprilTagNames.BlueTarget;
        hardware.storage.resetEncoderSusan();

    }

    @Override
    public void start() {
        hardware.vision.getArtifactPattern();
        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();

        /*hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
        hardware.sleep(5000);
        hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
        hardware.sleep(5000);
        hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
        hardware.sleep(5000);*/


        // START AI CODE


        hardware.raiser.driveByInches(96,0.3);


    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        hardware.stop();
        super.stop();
    }
}
