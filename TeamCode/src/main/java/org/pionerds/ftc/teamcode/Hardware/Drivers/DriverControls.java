package org.pionerds.ftc.teamcode.Hardware.Drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.pionerds.ftc.teamcode.Hardware.Hardware;

public abstract class DriverControls {

    float rotationSpeed = 0.0f;
    float speedMultiplier = 1;
    private String driverName = "";
    private boolean isDriver = false;
    private float maxSpeed = 1.0f;
    private float maxRotationSpeed = 1.0f;
    private float speedX = 0.0f;
    private float speedY = 0.0f;
    private float rotationMultiplier = 1.0f;

    public DriverControls(String driverName, float maxSpeed) {
        this.driverName = driverName;
        this.maxSpeed = maxSpeed;
    }

    public abstract void tickControls(Gamepad gamepad, Hardware hardware);

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public boolean getIsDriver() {
        return isDriver;
    }

    public void setIsDriver(boolean isDriver) {
        this.isDriver = isDriver;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public float getRotationMultiplier() {
        return rotationMultiplier;
    }

    public void setRotationMultiplier(float rotationMultiplier) {
        this.rotationMultiplier = rotationMultiplier;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public float getMaxRotationSpeed() {
        return maxRotationSpeed;
    }

    public void setMaxRotationSpeed(float maxRotationSpeed) {
        this.maxRotationSpeed = maxRotationSpeed;
    }
}
