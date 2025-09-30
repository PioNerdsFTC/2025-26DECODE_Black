package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

public class Storage {
    Hardware hardware = null;

    public Servo servo0;
    public Servo servo1;

    Storage() {

    }

    public void init(Hardware hardware) {
        this.hardware = hardware;

        servo0 = this.hardware.mapping.getServoMotor("servo0");
        servo1 = this.hardware.mapping.getServoMotor("servo1");
    }

    public void feed() {
        double pos = 1;
        servo0.setPosition(pos);
        servo1.setPosition(pos);
    }

    public void contract() {
        double pos = 0;
        servo0.setPosition(pos);
        servo1.setPosition(pos);
    }

    public void launcherButton(Gamepad operatorGamepad) {
        if (operatorGamepad.x) {
            feed();
        }
        if (operatorGamepad.y) {
            contract();
        }
    }
}
