package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;

@TeleOp(name = "PracticeOp")
public class PracticeOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();
    final DriverControls driverControls1 = new LucasDriverControls(
        "Lucas Schwietz",
        true,
        1.0f
    );

    final DriverControls blankDriverControls = new DriverControls(
            "Blank Man",
            1.0f
    ) {
        @Override
        public void tickControls(Gamepad gamepad, Hardware hardware) {
            return;
        }
    };

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry,driverControls1,blankDriverControls);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart(); // Wait for start!
        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        hardware.storage.resetEncoderSusan();

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();
        boolean changingPos = false;
        boolean changingLauncherSpeed = false;
        boolean changingIntakeSpeed = false;
        boolean changingFeederSpeed = false;
        boolean launcherOn = false;
        boolean feederOn = false;
        boolean intakeOn = false;


        // Main loop!
        while (opModeIsActive()) {


            hardware.tick(gamepad1,gamepad2);
            //hardware.storage.testRotateSusan(1);

            //telemetry.addLine("susanPosition: "+hardware.storage.susanMotorEx.getCurrentPosition());

            //hardware.vision.printTagDistanceToTelemetry(AprilTagNames.BlueTarget);

            //driverControls1.tickControls(gamepad1,hardware);

            LazySusanPositions currentPos = hardware.storage.getCurrentSusanPositionEnum();

            if (!changingPos) {
                if (gamepad2.dpad_left) {
                    hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
                } else if (gamepad2.dpad_down) {
                    hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
                } else if (gamepad2.dpad_right) {
                    hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
                } else if (gamepad2.x) {
                    hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT1);
                } else if (gamepad2.a) {
                    hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT2);
                } else if (gamepad2.b) {
                    hardware.storage.moveSusanTo(LazySusanPositions.OUTPUT3);
                } else if (gamepad2.left_bumper) {
                    for (int i = 0; i < LazySusanPositions.values().length; i++) {
                        if (currentPos.equals(LazySusanPositions.values()[i])) {
                            hardware.storage.moveSusanTo(LazySusanPositions.values()[(i - 1 + 6) % 6]);
                        }
                    }
                } else if (gamepad2.right_bumper) {
                    for (int i = 0; i < LazySusanPositions.values().length; i++) {
                        if (currentPos.equals(LazySusanPositions.values()[i])) {
                            hardware.storage.moveSusanTo(LazySusanPositions.values()[(i + 1) % 6]);
                        }
                    }


                }

            } else if (changingPos && !(gamepad2.dpad_left || gamepad2.dpad_down || gamepad2.dpad_right || gamepad2.x || gamepad2.a || gamepad2.b || gamepad2.left_bumper || gamepad2.right_bumper)) {
                changingPos = false;
            }


            if(!changingLauncherSpeed && gamepad2.left_stick_button){
                if(!launcherOn) {
                    hardware.launcher.setLauncherVelocity(1890);
                }
                if(launcherOn) {
                    hardware.launcher.setLauncherVelocity(0);
                }
                launcherOn = !launcherOn;
                changingLauncherSpeed = true;
            } else if(!gamepad2.left_stick_button) {
                changingLauncherSpeed = false;
            }

            if(!changingFeederSpeed && gamepad2.dpad_up) {
                if (!feederOn) {
                    hardware.storage.enableFeederManual();
                }
                if (feederOn) {
                    hardware.storage.disableFeeder();
                }
                feederOn = !launcherOn;
                changingFeederSpeed = true;
            } else if(!gamepad2.dpad_up) {
                feederOn = false;
            }

            if(!changingIntakeSpeed && gamepad2.right_stick_button){
                if(!intakeOn) hardware.storage.enableIntake();
                if(intakeOn) hardware.storage.disableIntake();
                intakeOn = !intakeOn;
                changingIntakeSpeed = true;
            } else if(!gamepad2.right_stick_button){
                changingIntakeSpeed = false;
            }

            telemetry.addLine("Current Pos: " + currentPos.name());
            telemetry.addLine("Launcher: " + launcherOn);
            telemetry.addLine("Intake: " + intakeOn);
            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
