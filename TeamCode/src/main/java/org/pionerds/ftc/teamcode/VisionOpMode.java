package org.pionerds.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.pionerds.ftc.teamcode.Vision;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
@TeleOp(name = "VisionOpMode")
public class VisionOpMode extends LinearOpMode {
    Vision visionMethods = new Vision(telemetry);

    @Override
    public void runOpMode() {

        //telemetry.addLine("Initialized");
        //telemetry.update();
        waitForStart();
        //visionMethods.controlVisionPortal(VisionCommands.RESUME);

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                //telemetryAprilTag();

                // Push telemetry to the Driver Station.
                //telemetry.update();

                // Save CPU resources; can resume streaming when needed.
                //if (gamepad1.dpad_down) {
                //    visionMethods.controlVisionPortal(VisionCommands.STOP);
                //} else if (gamepad1.dpad_up) {
                //    visionMethods.controlVisionPortal(VisionCommands.RESUME);
                //}

                // Share the CPU.
                //sleep(20);
            }
        }

        // Save more CPU resources when camera is no longer needed.
        //visionMethods.controlVisionPortal(VisionCommands.STOP);
        //visionMethods.controlVisionPortal(VisionCommands.CLOSE);
    }   // end method runOpMode()
}
