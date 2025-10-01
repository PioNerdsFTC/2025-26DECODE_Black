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
    final DriverControls driverControls1 = new LucasDriverControls("Lucas",1.0f);
    final DriverControls driverControls2 = new LucasDriverControls("Liam",1.0f);

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
            hardware.tick(gamepad1,gamepad2);


          
            // Add AprilTagPoseFtc data to Telemetry
            PioNerdAprilTag blueTargetAprilTag;
            blueTargetAprilTag = hardware.vision.getPioNerdAprilTag(AprilTagNames.BlueTarget);
            if(blueTargetAprilTag != null){
                telemetry.addLine("BlueTarget Distances");
                telemetry.addLine("x: "+blueTargetAprilTag.x(2));
                telemetry.addLine("y: "+blueTargetAprilTag.y(2));
                telemetry.addLine("z: "+blueTargetAprilTag.z(2));
                telemetry.addLine("Range: "+blueTargetAprilTag.range(2));
                telemetry.addLine("Pythag A,B: "+(Math.sqrt(Math.pow((blueTargetAprilTag.x(2)),2)) + Math.pow((blueTargetAprilTag.x(2)),2)));
                hardware.launcher.launcher0.setVelocity(blueTargetAprilTag.range(2));
                hardware.launcher.launcher1.setVelocity(blueTargetAprilTag.range(2));
            }


            telemetry.update();
            sleep(1);
        }


        hardware.stop();
    }
}
