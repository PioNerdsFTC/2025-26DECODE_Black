package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "LaunchTest")
public class LaunchTestOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();
    final DriverControls driverControls1 = new LucasDriverControls(
            "Lucas Schwietz",
            true,
            1.0f
    );

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart(); // Wait for start!
        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        //hardware.storage.resetEncoderSusan();

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();
        boolean changingVelocity = false;
        double currentVelocity = 0.00;

        // Main loop!
        while (opModeIsActive()) {


            //hardware.tick(gamepad1,gamepad2);
            //hardware.storage.testRotateSusan(1);

            //telemetry.addLine("susanPosition: "+hardware.storage.susanMotorEx.getCurrentPosition());

            hardware.vision.printTagDistanceToTelemetry(AprilTagNames.BlueTarget);

            //driverControls1.tickControls(gamepad1,hardware);

            if(!changingVelocity){
                if(gamepad1.dpad_up){
                    currentVelocity += 5.00;
                    changingVelocity = true;
                } else if (gamepad1.dpad_down) {
                    currentVelocity -= 5.00;
                    changingVelocity = true;
                }
            } else if (!(gamepad1.dpad_up || gamepad1.dpad_down)) {
                changingVelocity = false;
            }

            telemetry.addLine("\nVelocity: "+currentVelocity);
            telemetry.addLine("encoderReading0: "+hardware.launcher.launcher0.getVelocity());
            telemetry.addLine("encoderReading1: "+hardware.launcher.launcher1.getVelocity());
            hardware.launcher.launcher0.setVelocity(currentVelocity);
            hardware.launcher.launcher1.setVelocity(currentVelocity);
            //hardware.launcher.setLauncherVelocity(currentVelocity);


            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
