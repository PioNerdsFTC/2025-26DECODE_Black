package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "TuneRaiserOpMode")
public class TuneRaiserOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart(); // Wait for start!
        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();


        hardware.raiser.resetEncoders();
        hardware.raiser.tune();
        hardware.sleep(1000);
        hardware.raiser.tuneSide();
        hardware.sleep(1000);
        hardware.raiser.tuneRotation();


        // Main loop!
        while (opModeIsActive()) {

            hardware.raiser.tunePrint();
            hardware.raiser.rotateToTarget(AprilTagNames.BlueTarget);
            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
