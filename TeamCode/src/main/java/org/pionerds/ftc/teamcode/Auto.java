package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.pionerds.ftc.teamcode.Robot.Robot;

@Autonomous(name = "Autonymous")
public class Auto extends LinearOpMode {
    final Robot robot = new Robot();

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        robot.init();

        telemetry.addLine("Robot initialized (Auto)");
        telemetry.update();

        while (opModeIsActive()) {}

        robot.stop();
    }
}
