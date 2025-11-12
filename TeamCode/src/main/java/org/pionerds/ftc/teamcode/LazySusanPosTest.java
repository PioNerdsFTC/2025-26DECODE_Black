package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;

@TeleOp(name = "SusanEnumPosTest")
public class LazySusanPosTest extends LinearOpMode {

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

        hardware.storage.resetEncoderSusan();

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();
        boolean changingPos = false;

        // Main loop!
        while (opModeIsActive()) {


            //hardware.tick(gamepad1,gamepad2);
            //hardware.storage.testRotateSusan(1);

            //telemetry.addLine("susanPosition: "+hardware.storage.susanMotorEx.getCurrentPosition());

            //hardware.vision.printTagDistanceToTelemetry(AprilTagNames.BlueTarget);

            //driverControls1.tickControls(gamepad1,hardware);

            LazySusanPositions currentPos = hardware.storage.getCurrentSusanPositionEnum();

            if (!changingPos) {
                if (gamepad1.dpad_left) {
                    hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
                } else if (gamepad1.dpad_down) {
                    hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
                } else if (gamepad1.dpad_right) {
                    hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
                } else if (gamepad1.x) {
                    hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
                } else if (gamepad1.a) {
                    hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
                } else if (gamepad1.b) {
                    hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
                } else if (gamepad1.left_bumper) {
                    for (int i = 0; i < LazySusanPositions.values().length; i++) {
                        if (currentPos.equals(LazySusanPositions.values()[i])) {
                            hardware.storage.moveSusanTo(LazySusanPositions.values()[(i - 1 + 6) % 6]);
                        }
                    }
                } else if (gamepad1.right_bumper) {
                    for (int i = 0; i < LazySusanPositions.values().length; i++) {
                        if (currentPos.equals(LazySusanPositions.values()[i])) {
                            hardware.storage.moveSusanTo(LazySusanPositions.values()[(i + 1) % 6]);
                        }
                    }


                }

            } else if (changingPos && !(gamepad1.dpad_left || gamepad1.dpad_down || gamepad1.dpad_right || gamepad1.x || gamepad1.a || gamepad1.b || gamepad1.left_bumper || gamepad1.right_bumper)) {
                changingPos = false;
            }

            telemetry.addLine("Current Pos: " + currentPos.name());
            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
