package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Map;

/**
 * Class for all the hardware functions of the robot.
 * This should include helper classes and direct controllers for the hardware.
 */
final public class Hardware {

    Drivetrain drivetrain = new Drivetrain();
    Mapping mapping = new Mapping();
    Vision vision = new Vision();

    /**
     * Whether the hardware class is able to continue running.
     */
    public boolean continueRunning = true;

    public void init(HardwareMap hardwareMap) {
        mapping.init(this, hardwareMap);
        drivetrain.init(this);
        vision.init(this);
    }

    /** Runs for each iteration of the OpMode, may or may not be necessary */
    public void tick() {

    }

    public void stop() {
        continueRunning = false;
    }
}
