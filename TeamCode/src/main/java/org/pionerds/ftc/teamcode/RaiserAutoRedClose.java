package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.pionerds.ftc.teamcode.Hardware.AprilTagNames;
import org.pionerds.ftc.teamcode.Hardware.Artifact;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Hardware.LazySusanPositions;



@Autonomous(name = "RaiserAutoRedClose")
public class RaiserAutoRedClose extends LinearOpMode {

    final Hardware hardware = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {
        hardware.init(hardwareMap, telemetry);
        telemetry.addLine("Robot initialized! (TeleOp)");
        telemetry.update();

        AprilTagNames target = AprilTagNames.RedTarget;
        hardware.storage.resetEncoderSusan();


        waitForStart(); // Wait for start!


        hardware.vision.getArtifactPattern();
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

        hardware.vision.getArtifactPattern();
        telemetry.addLine("Ob Id: "+hardware.vision.getObeliskIdentified());
        hardware.sleep(1000);

        telemetry.addLine("Getting pattern...");
        Artifact[] pattern = hardware.vision.getArtifactPattern();
        for(Artifact art : pattern){
            telemetry.addLine("object: "+art.name());
        }

        LazySusanPositions[] inputEnums = new LazySusanPositions[]{LazySusanPositions.INTAKE1, LazySusanPositions.INTAKE2, LazySusanPositions.INTAKE3};
        LazySusanPositions[] outputEnums = new LazySusanPositions[]{LazySusanPositions.OUTPUT1, LazySusanPositions.OUTPUT2, LazySusanPositions.OUTPUT3};

        // pattern specific
        LazySusanPositions[] intake_GPP = {inputEnums[0],inputEnums[1],inputEnums[2]};
        LazySusanPositions[] intake_PGP = {inputEnums[1],inputEnums[0],inputEnums[2]};
        LazySusanPositions[] intake_PPG = {inputEnums[1],inputEnums[2],inputEnums[0]};

        LazySusanPositions[] output_GPP = {outputEnums[0],outputEnums[1],outputEnums[2]};
        LazySusanPositions[] output_PGP = {outputEnums[1],outputEnums[0],outputEnums[2]};
        LazySusanPositions[] output_PPG = {outputEnums[1],outputEnums[2],outputEnums[0]};

        LazySusanPositions[] selectedIntake = intake_GPP;
        LazySusanPositions[] selectedOutput = output_GPP;

        if(pattern[0].equals(Artifact.GREEN) && pattern[1].equals(Artifact.PURPLE) && pattern[2].equals(Artifact.PURPLE)){
            selectedIntake = intake_GPP;
            selectedOutput = output_GPP;
            telemetry.addLine("PATTERN IS GPP");
        } else if(pattern[0].equals(Artifact.PURPLE) && pattern[1].equals(Artifact.GREEN) && pattern[2].equals(Artifact.PURPLE)){
            selectedIntake = intake_PGP;
            selectedOutput = output_PGP;
            telemetry.addLine("PATTERN IS PGP");
        } else if(pattern[0].equals(Artifact.PURPLE) && pattern[1].equals(Artifact.PURPLE) && pattern[2].equals(Artifact.GREEN)){
            selectedIntake = intake_PPG;
            selectedOutput = output_PPG;
            telemetry.addLine("PATTERN IS PPG");
        } else {
            telemetry.addLine("PATTERN NOT IN COMPARISONS");
            telemetry.addLine("Pattern:");
            telemetry.addLine(pattern[0].name());
            telemetry.addLine(pattern[1].name());
            telemetry.addLine(pattern[2].name());
        }
        telemetry.addLine("ob Id? "+hardware.vision.getObeliskIdentified());
        telemetry.update();
        hardware.sleep(5000);

        // START AI CODE


// FTC Autonomous Path - Generated Code
// Robot Start: (114", 114") @ -120°

// Step 1
        hardware.raiser.driveByInches(30.00);

// Step 2
        hardware.raiser.driveByDegrees(-1 * 120.00);

// Step 3
        hardware.sleep(1000);

// Step 4
        hardware.raiser.driveByDegrees(-1 * 60.00);

// Step 5
        hardware.storage.disableFeeder();

// Step 6
        hardware.launcher.setLauncherVelocity(hardware.aimbot.calculateMotorVelocity(target));

// Step 7
        hardware.sleep(1500);

// Step 8
        hardware.storage.moveSusanTo(selectedOutput[0]);
        hardware.sleep(1500);

// Step 9
        hardware.storage.enableFeederManual();

// Step 10
        hardware.sleep(2000);

// Step 11
        hardware.storage.disableFeeder();

// Step 12
        hardware.storage.moveSusanTo(selectedOutput[1]);
        hardware.sleep(1500);

// Step 13
        hardware.storage.enableFeederManual();

// Step 14
        hardware.sleep(2000);

// Step 15
        hardware.storage.disableFeeder();

// Step 16
        hardware.storage.moveSusanTo(selectedOutput[2]);
        hardware.sleep(1500);

// Step 17
        hardware.storage.enableFeederManual();

// Step 18
        hardware.sleep(2000);

// Step 19
        hardware.storage.disableFeeder();

// Step 20
        hardware.raiser.driveByDegrees(-1 * 165.00);

// Step 21
        hardware.raiser.driveByInches(81.00);

// Step 22
        hardware.raiser.driveByInches(6.00);




        /// ////////////////////////////////////////


        // Main loop!
        while (opModeIsActive()) {

            hardware.raiser.tunePrint();
            telemetry.update();

            sleep(1);
        }

        hardware.stop();
    }
}
