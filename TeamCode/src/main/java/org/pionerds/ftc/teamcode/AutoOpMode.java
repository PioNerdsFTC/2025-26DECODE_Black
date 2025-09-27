package org.pionerds.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@Config
@Autonomous(name = "Autonomous", group = "Autonomous")
public class AutoOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);

        telemetry.addLine("Robot initialized (Auto)");
        telemetry.update();

        waitForStart();

        while (opModeIsActive() && hardware.continueRunning) {
            sleep(1);
        }

        hardware.stop();
    }
}
