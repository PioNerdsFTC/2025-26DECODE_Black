package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.PioNerdAprilTag;

@TeleOp(name = "TeleOp")
public class TeleOpMode extends LinearOpMode {
    final Hardware hardware = new Hardware();
    final DriverControls driverControls1 = new LucasDriverControls("Lucas Schwietz",1.0f);
    final DriverControls driverControls2 = new LucasDriverControls("Liam St. Ores",0.7f);

    @Override
    public void runOpMode() throws InterruptedException {

        hardware.init(hardwareMap, telemetry, driverControls1, driverControls2);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart(); // Wait for start!

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();
      

        // Main loop!
        while (opModeIsActive() && hardware.continueRunning) {

            telemetry.addLine("Driver: "+driverControls1.getDriverName());
            telemetry.addLine("Speed X: "+driverControls1.getSpeedX());
            telemetry.addLine("Speed Y: "+driverControls1.getSpeedY());

            hardware.tick(gamepad1,gamepad2);

            telemetry.update();
            sleep(1);
        }


        hardware.stop();
    }
}
