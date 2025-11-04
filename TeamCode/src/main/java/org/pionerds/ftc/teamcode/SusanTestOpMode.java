package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "SusanTest")
public class SusanTestOpMode extends LinearOpMode {

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
        boolean changingPower = false;
        double currentPower = 0.00;

        // Main loop!
        while (opModeIsActive()) {


            //hardware.tick(gamepad1,gamepad2);
            //hardware.storage.testRotateSusan(1);

            //telemetry.addLine("susanPosition: "+hardware.storage.susanMotorEx.getCurrentPosition());

            //hardware.vision.printTagDistanceToTelemetry(AprilTagNames.BlueTarget);

            //driverControls1.tickControls(gamepad1,hardware);

            if(!changingPower){
                if(gamepad1.dpad_up){
                    currentPower += 0.1;
                    changingPower = true;
                } else if (gamepad1.dpad_down) {
                    currentPower -= 0.1;
                    changingPower = true;
                }
            } else if (!(gamepad1.dpad_up || gamepad1.dpad_down)) {
                changingPower = false;
            }
            if(gamepad1.b){
                currentPower = 0;
            }

            telemetry.addLine("\nPower: "+currentPower);
            hardware.storage.testRotateSusan(currentPower);

            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
