package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Launcher {
    Hardware hardware = null;

    public DcMotor launcher0;
    public DcMotor launcher1;

    public void init(Hardware hardware) {
        this.hardware = hardware;

        launcher0 = this.hardware.mapping.getMotor("launcher0", 3.0, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT);
        launcher1 = this.hardware.mapping.getMotor("launcher1", 3.0, DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void setLauncherPower(double power) {
        launcher0.setPower(power);
        launcher1.setPower(power);
    }

    public void launcherButton(Gamepad operatorGamepad) {
        if (operatorGamepad.y) {
            setLauncherPower(1);
        }
        else {
            setLauncherPower(0);
        }
    }
}