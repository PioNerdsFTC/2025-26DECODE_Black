package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.configuration.annotations.MotorType;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.pionerds.ftc.teamcode.Hardware.Hardware;

@Autonomous(name = "Raiser Auto")
public class RaisingIt2 extends LinearOpMode {

    private final Hardware hardware = new Hardware();
    private final String[] motorNames = {"motor0", "motor1", "motor2", "motor3"};


    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);

        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        waitForStart();

        for (String motorName : motorNames) {
            hardwareMap.get(DcMotorEx.class, motorName).setMotorType();

        }

        hardware.stop();
    }
}
