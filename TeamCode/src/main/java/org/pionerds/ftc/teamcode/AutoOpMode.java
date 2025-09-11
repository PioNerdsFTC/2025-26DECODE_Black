package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.pionerds.ftc.teamcode.Hardware.Hardware;

@Autonomous(name = "Autonomous")
public class AutoOpMode extends LinearOpMode {
    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        hardware.init(hardwareMap);

        telemetry.addLine("Robot initialized (Auto)");
        telemetry.update();

        while (opModeIsActive() && hardware.continueRunning) {}

        hardware.stop();
    }
}
