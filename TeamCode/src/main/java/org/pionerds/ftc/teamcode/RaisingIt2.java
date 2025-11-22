package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.configuration.annotations.MotorType;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

@Autonomous(name = "Raiser Auto")
public class RaisingIt2 extends LinearOpMode {

    private final Hardware hardware = new Hardware();



    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        final DcMotorEx[] motors = {
            hardwareMap.get(DcMotorEx.class, "motor0"),
            hardwareMap.get(DcMotorEx.class, "motor1"),
            hardwareMap.get(DcMotorEx.class, "motor2"),
            hardwareMap.get(DcMotorEx.class, "motor3")
        };
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart();

        for (DcMotorEx motor : motors) {
            motor.setTargetPosition(3000);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(1000);
        }

        long startTime = System.currentTimeMillis();
        long timeoutMillis = 5000; // 5 seconds timeout
        while ((motors[0].isBusy() || motors[1].isBusy() || motors[2].isBusy() || motors[3].isBusy())
                && (System.currentTimeMillis() - startTime < timeoutMillis)) {
            for (DcMotorEx motor : motors){
                telemetry.addData(motor.getDeviceName() + " Position", motor.getCurrentPosition());
            }
            telemetry.update();
        }
        if (System.currentTimeMillis() - startTime >= timeoutMillis) {
            telemetry.addLine("Timeout reached while waiting for motors to finish.");
            telemetry.update();
        }
        hardware.stop();
    }
}
