package org.pionerds.ftc.teamcode.autonomousRaisers;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@Autonomous(name = "RaiserRedClose")
public class RaiserRedClose extends OpMode {

    final Hardware hardware = new Hardware();
    AprilTagNames target;

    @Override
    public void init() {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        target = AprilTagNames.RedTarget;
        hardware.storage.resetEncoderSusan();
    }

    @Override
    public void start() {
        hardware.vision.getArtifactPattern();
        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();

        hardware.raiser.driveByInches(-36,-0.3);

        if(hardware.vision.getPioNerdAprilTag(target) != null){
            hardware.launcher.setLauncherVelocity(hardware.aimbot.calculateMotorVelocity(target));
        } else {
            hardware.launcher.setLauncherVelocity(1800);
        }

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
