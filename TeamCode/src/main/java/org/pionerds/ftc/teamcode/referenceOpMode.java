package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Reference OpMode implementation")
@Disabled
public class referenceOpMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        telemetry.addLine("Hello, PionerDS!");
        telemetry.update();

        while (opModeIsActive()) {}
    }
}
