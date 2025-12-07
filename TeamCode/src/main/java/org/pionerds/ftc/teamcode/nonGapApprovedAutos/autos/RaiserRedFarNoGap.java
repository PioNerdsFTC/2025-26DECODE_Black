package org.pionerds.ftc.teamcode.nonGapApprovedAutos.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;
import org.pionerds.ftc.teamcode.Utils.DataStorage;

@Autonomous(name = "RaiserRedFarNoGap",group = "Maple Grove Competition NoGap",preselectTeleOp = "RedOpPostCompetition")
public class RaiserRedFarNoGap extends OpMode {

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


        hardware.storage.disableFeeder();
        hardware.raiser.driveByInches(84,0.3);
        hardware.raiser.driveByDegrees(-60, 0.3);
        if(hardware.vision.getPioNerdAprilTag(target) != null) {
            hardware.launcher.setLauncherVelocity(hardware.aimbot.calculateMotorVelocity(target));
        } else {
            hardware.launcher.setLauncherVelocity(1800);
        }

        LazySusanPositions[][] selectedIntakeOutput = hardware.raiser.getIntakeOutputArrays();
        LazySusanPositions[] selectedIntake = selectedIntakeOutput[0];
        LazySusanPositions[] selectedOutput = selectedIntakeOutput[1];

        hardware.storage.moveSusanTo(selectedOutput[0]);
        hardware.sleep(1500);
        hardware.storage.enableFeederManual();
        hardware.sleep(1500);
        hardware.storage.disableFeeder();

        hardware.storage.moveSusanTo(selectedOutput[1]);
        hardware.sleep(1500);
        hardware.storage.enableFeederManual();
        hardware.sleep(1500);
        hardware.storage.disableFeeder();

        hardware.storage.moveSusanTo(selectedOutput[2]);
        hardware.sleep(1500);
        hardware.storage.enableFeederManual();
        hardware.sleep(1500);

        hardware.storage.disableFeeder();
        hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
        hardware.launcher.setLauncherVelocity(0);
        hardware.storage.disableIntake();

        hardware.raiser.driveByDegrees(60, 0.3);
        hardware.raiser.driveByInches(-30,0.3);

    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        DataStorage.storeAngle(0, hardware.gyro.getHeading());
        hardware.stop();
        super.stop();
    }
}
