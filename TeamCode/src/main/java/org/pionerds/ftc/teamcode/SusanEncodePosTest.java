package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@TeleOp(name = "SusanEncodePosTest")
public class SusanEncodePosTest extends LinearOpMode {

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

        hardware.storage.setMotorBrakeBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        //hardware.storage.resetEncoderSusan();

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();

        // Main loop!
        while (opModeIsActive()) {


            //hardware.tick(gamepad1,gamepad2);
            //hardware.storage.testRotateSusan(1);

            //telemetry.addLine("susanPosition: "+hardware.storage.susanMotorEx.getCurrentPosition());

            //hardware.vision.printTagDistanceToTelemetry(AprilTagNames.BlueTarget);

            //driverControls1.tickControls(gamepad1,hardware);

            telemetry.addLine("\nCURRENT Vel: " + hardware.storage.currentSusanVelocity());
            telemetry.addLine("\nPosition: " + hardware.storage.getSusanCurrentTicks()+" ticks");

            hardware.storage.printSusanOffsets();

            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
