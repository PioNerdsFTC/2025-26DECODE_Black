package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.pionerds.ftc.teamcode.Robot.Robot;

@TeleOp(name = "TeleOp")
public class TeleOpMode extends LinearOpMode {
    final Robot robot = new Robot();

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        robot.init();

        telemetry.addLine("Robot initialized (TeleOp)");
        telemetry.update();

        while (opModeIsActive()) {}

        robot.stop();
    }
}
