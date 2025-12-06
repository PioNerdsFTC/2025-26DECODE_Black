package org.pionerds.ftc.teamcode.autonomousRaisers;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@Autonomous(name = "RaiserBlueClose")
public class RaiserBlueClose extends OpMode {

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

        hardware.raiser.driveByInches(-24,-0.3);
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
