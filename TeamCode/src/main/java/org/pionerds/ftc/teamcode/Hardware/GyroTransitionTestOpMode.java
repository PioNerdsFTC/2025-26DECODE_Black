
package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.Hardware.Drivers.LucasDriverControls;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;
import org.pionerds.ftc.teamcode.Utils.DataStorage;

@Autonomous(name = "GyroTransitionTestOpMode")
public class GyroTransitionTestOpMode extends LinearOpMode {

    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();



        waitForStart(); // Wait for start!

        // Main loop!
        while (opModeIsActive()) {

            hardware.raiser.tunePrint();
            telemetry.update();

            sleep(1);
        }

        double[] current_angles = hardware.gyro.getAngles();
        DataStorage.storeAllAngles(current_angles);

        hardware.stop();
    }
}
