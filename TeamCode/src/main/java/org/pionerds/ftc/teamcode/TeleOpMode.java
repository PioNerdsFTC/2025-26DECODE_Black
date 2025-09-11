package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "TeleOp")
public class TeleOpMode extends LinearOpMode {
    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        hardware.init(hardwareMap);

        telemetry.addLine("Robot initialized (TeleOp)");
        telemetry.update();

        while (opModeIsActive() && hardware.continueRunning) {
            hardware.tick();
            sleep(1);
        }

        hardware.stop();
    }
}
