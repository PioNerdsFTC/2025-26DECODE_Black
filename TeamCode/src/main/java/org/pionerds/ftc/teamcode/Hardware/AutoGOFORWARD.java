package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;

@Disabled
@Autonomous(name = "AutoGOFORWARD")
public class AutoGOFORWARD extends LinearOpMode {

    final Hardware hardware = new Hardware();


    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        AprilTagNames target = AprilTagNames.BlueTarget;
        hardware.storage.resetEncoderSusan();


        waitForStart(); // Wait for start!



        ElapsedTime elapsedTime = new ElapsedTime();
        hardware.addElapsedTime(elapsedTime);

        telemetry.addLine("Robot runtime started! (TeleOp)");
        telemetry.update();

        /*hardware.storage.moveSusanTo(LazySusanPositions.INTAKE1);
        hardware.sleep(5000);
        hardware.storage.moveSusanTo(LazySusanPositions.INTAKE2);
        hardware.sleep(5000);
        hardware.storage.moveSusanTo(LazySusanPositions.INTAKE3);
        hardware.sleep(5000);*/



        // START AI CODE

// FTC Autonomous Path - Generated Code
// Robot Start: (12", 114") @ 120°

// Step 1
        hardware.raiser.driveByInches(70.00);
      ///z ////////////////////////////////////////


        // Main loop!
        while (opModeIsActive()) {

            hardware.raiser.tunePrint();
            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
