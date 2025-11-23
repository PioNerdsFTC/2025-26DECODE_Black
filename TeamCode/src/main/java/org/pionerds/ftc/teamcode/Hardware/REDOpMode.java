package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControlsRed;
import org.pionerds.ftc.teamcode.Hardware.Drivers.ManualSusanOperatorControls;

@TeleOp(name = "RedOpMode")
public class REDOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();
    final DriverControls driverControls1 = new LucasDriverControlsRed(
        "Lucas Schwietz",
        true,
        1.0f
    );

    final ManualSusanOperatorControls driverControls2 = new ManualSusanOperatorControls(
            "Lukie Pookie",
            true,
            1.0f
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
            if(gamepad2.right_bumper) hardware.raiser.rotateToTarget(AprilTagNames.BlueTarget);

            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
