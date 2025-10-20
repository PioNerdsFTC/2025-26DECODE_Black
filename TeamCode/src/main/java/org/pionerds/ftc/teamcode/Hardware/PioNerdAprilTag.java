package org.pionerds.ftc.teamcode.Hardware;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class PioNerdAprilTag {

    private final AprilTagDetection aprilTagDetection;
    private final double MULTIPLY_TO_DISTANCE = 1; // not 0.5

    public PioNerdAprilTag(AprilTagDetection aprilTagDetectionInput) {
        this.aprilTagDetection = aprilTagDetectionInput;
    }

    public AprilTagDetection getAprilTagDetection() {
        return aprilTagDetection;
    }

    public double x() {
        return aprilTagDetection.ftcPose.x * MULTIPLY_TO_DISTANCE;
    }

    public double x(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.x *
                    MULTIPLY_TO_DISTANCE *
                    Math.pow(10.0, decimalPlaces)
            ) /
            Math.pow(10.0, decimalPlaces)
        );
    }

    public double y() {
        return aprilTagDetection.ftcPose.y * MULTIPLY_TO_DISTANCE;
    }

    public double y(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.y *
                    MULTIPLY_TO_DISTANCE *
                    Math.pow(10.0, decimalPlaces)
            ) /
            Math.pow(10.0, decimalPlaces)
        );
    }

    public double z() {
        return aprilTagDetection.ftcPose.z * MULTIPLY_TO_DISTANCE;
    }

    public double z(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.z *
                    MULTIPLY_TO_DISTANCE *
                    Math.pow(10.0, decimalPlaces)
            ) /
            Math.pow(10.0, decimalPlaces)
        );
    }

    public double range() {
        return aprilTagDetection.ftcPose.range * MULTIPLY_TO_DISTANCE;
    }

    public double range(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.range *
                    MULTIPLY_TO_DISTANCE *
                    Math.pow(10.0, decimalPlaces)
            ) /
            Math.pow(10.0, decimalPlaces)
        );
    }
}
