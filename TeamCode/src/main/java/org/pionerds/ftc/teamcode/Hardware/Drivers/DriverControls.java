package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.pionerds.ftc.teamcode.Hardware.Hardware;

public abstract class DriverControls {

    private String driverName = "";
    private boolean isDriver = false;
    private float maxSpeed = 1.0f;
    private float speedX = 0.0f;
    private float speedY = 0.0f;
    private float maxRotationSpeed = 1.0f;

    float rotationSpeed = 0.0f;

    float speedMultiplier = 1;

    public DriverControls(String driverName, boolean isDriver, float maxSpeed) {
        this.driverName = driverName;
        this.isDriver = isDriver;
        this.maxSpeed = maxSpeed;
    }

    public abstract void tickControls(Gamepad gamepad, Hardware hardware);

    public String getDriverName() {
        return driverName;
    }

    public boolean getIsDriver() {
        return isDriver;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public float getMaxRotationSpeed() {
        return maxRotationSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setMaxRotationSpeed(float maxRotationSpeed) {
        this.maxRotationSpeed = maxRotationSpeed;
    }

    public void setIsDriver(boolean isDriver) {
        this.isDriver = isDriver;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }
}
