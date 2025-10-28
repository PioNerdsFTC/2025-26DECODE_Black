package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Launcher {

    Hardware hardware = null;

    public DcMotorEx launcher0;
    public DcMotorEx launcher1;

    Launcher() {}

    public void init(Hardware hardware) {
        this.hardware = hardware;

        launcher0 = this.hardware.mapping.getMotor(
            "launcher0",
            3.0,
            DcMotorSimple.Direction.FORWARD,
            DcMotorEx.ZeroPowerBehavior.FLOAT
        );
        launcher1 = this.hardware.mapping.getMotor(
            "launcher1",
            3.0,
            DcMotorSimple.Direction.REVERSE,
            DcMotorEx.ZeroPowerBehavior.FLOAT
        );

        launcher0.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        launcher1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Set launcher power to
     * @param power power to set launcher to
     */

    public void setLauncherPower(double power) {
        launcher0.setPower(power);
        launcher1.setPower(power);
    }

    /**
     * Set launcher velocity to
     * @param velocity velocity to set launcher to
     */
    public void setLauncherVelocity(double velocity) {
        launcher0.setVelocity(velocity);
        launcher1.setVelocity(velocity);
    }


    // deprecated
    public void launcherButton(Gamepad operatorGamepad) {
        if (operatorGamepad.y) {
            setLauncherPower(1);
        } else {
            setLauncherPower(0);
        }
    }

    /**
     * Stops the launchers
     */
    public void stopLaunchers() {
        launcher0.setPower(0);
        launcher1.setPower(0);
    }
}
