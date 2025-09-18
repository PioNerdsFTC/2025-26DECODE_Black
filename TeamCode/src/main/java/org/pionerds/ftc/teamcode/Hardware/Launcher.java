package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Launcher {
    Hardware hardware = null;

    public DcMotorEx launcher0;
    public DcMotorEx launcher1;

    Launcher() {

    }

    public void init(Hardware hardware) {
        this.hardware = hardware;

        launcher0 = this.hardware.mapping.getMotor("launcher0", 3.0, DcMotorSimple.Direction.FORWARD, DcMotorEx.ZeroPowerBehavior.FLOAT);
        launcher1 = this.hardware.mapping.getMotor("launcher1", 3.0, DcMotorSimple.Direction.REVERSE, DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    public void setLauncherVelocity(double velocity) {
        launcher0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher0.setVelocity(velocity);
        launcher1.setVelocity(velocity);
    }

    public void launcherButton(Gamepad operatorGamepad) {
        double currentVelocity = 6000;
        if (operatorGamepad.y) {
            setLauncherVelocity(currentVelocity);
        }
        else {
            setLauncherVelocity(0);
        }
        if (operatorGamepad.x) {
            setLauncherVelocity(currentVelocity-100);
        }
        if (operatorGamepad.b) {
            setLauncherVelocity(currentVelocity+100);
        }
    }
}