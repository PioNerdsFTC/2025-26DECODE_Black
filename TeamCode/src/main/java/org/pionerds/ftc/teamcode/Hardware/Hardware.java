package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.pionerds.ftc.teamcode.Utils.Environment;

import java.util.Map;

/**
 * Class for all the hardware functions of the robot.
 * This should include helper classes and direct controllers for the hardware.
 */
final public class Hardware {
    public Drivetrain drivetrain = new Drivetrain();
    public Mapping mapping = new Mapping();
    public Vision vision = new Vision();

    private Telemetry telemetry = null;


    /**
     * Whether the hardware class is able to continue running.
     */
    public boolean continueRunning = true;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        try {
            this.telemetry = telemetry;

            mapping.init(this, hardwareMap);
            drivetrain.init(this);
            vision.init(this);
        } catch(Exception e) {
            telemetry.addLine(e.getMessage());
            this.continueRunning = false;
        }
    }

    /** Runs for each iteration of the OpMode, may or may not be necessary */
    public void tick(Gamepad gamepad1) {
        try {
          this.drivetrain.driveDPad(gamepad1);
        } catch(Exception e) {
            this.telemetry.addLine(e.getMessage());
            if (!Environment.competing) this.continueRunning = false;
        }
    }
  
    public void stop() {
        continueRunning = false;
    }
}
