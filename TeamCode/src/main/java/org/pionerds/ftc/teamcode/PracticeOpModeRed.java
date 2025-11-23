package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.ManualSusanOperatorControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "PracticeOpRed")
public class PracticeOpModeRed extends LinearOpMode {

    final Hardware hardware = new Hardware();
    final DriverControls driverControls1 = new LucasDriverControls(
        "Lucas Schwietz",
        true,
        1.0f,
            true
    );

    final ManualSusanOperatorControls driverControls2 = new ManualSusanOperatorControls(
            "Lukie Pookie",
            true,
            1.0f,
            true
    );

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry,driverControls1,driverControls2);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart(); // Wait for start!
        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        hardware.storage.resetEncoderSusan();

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();


        // Main loop!
        while (opModeIsActive()) {

            hardware.tick(gamepad1,gamepad2);

            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
