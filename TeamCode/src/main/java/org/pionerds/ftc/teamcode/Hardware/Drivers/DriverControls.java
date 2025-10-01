package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.Hardware;

public abstract class DriverControls {
    String driverName = "";
    float maxSpeed = 1.0f;
    float speedX = 0.0f;
    float speedY = 0.0f;
    float maxRotationSpeed = 0.5f;


    float rotationSpeed = 0.0f;

    float speedMultiplier = 1;

    public DriverControls(String driverName, float maxSpeed){
        this.driverName = driverName;
        this.maxSpeed = maxSpeed;
    }

    public abstract void tickControls(Gamepad gamepad, Hardware hardware);

}
