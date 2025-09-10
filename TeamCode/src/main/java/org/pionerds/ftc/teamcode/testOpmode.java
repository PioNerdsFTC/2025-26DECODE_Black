package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Test opmode")
public class testOpmode extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        telemetry.addLine("Hello, PionerDS!");
        telemetry.update();

        while (opModeIsActive()) {}
    }
}
