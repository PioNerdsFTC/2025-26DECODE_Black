package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Class for all the hardware functions of the robot.
 * This should include helper classes and direct controllers for the hardware.
 */
final public class Hardware {

    /**
     * Whether the hardware class is able to continue running.
     */
    public boolean continueRunning = true;

    /**
     * The hardware map.
     */
    HardwareMap map;

    public void init(HardwareMap hardwareMap) {

    }

    public void stop() {
        continueRunning = false;
    }
}
