package org.pionerds.ftc.teamcode.Hardware;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class PioNerdAprilTag {

    private final AprilTagDetection aprilTagDetection;
    public PioNerdAprilTag(AprilTagDetection aprilTagDetectionInput) {
        this.aprilTagDetection = aprilTagDetectionInput;
    }

    public AprilTagDetection getAprilTagDetection() {
        return aprilTagDetection;
    }

    public double x() {
        return aprilTagDetection.ftcPose.x;
    }

    public double x(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.x *
                    Math.pow(10.0, decimalPlaces)
            ) /
                Math.pow(10.0, decimalPlaces)
        );
    }

    public double y() {
        return aprilTagDetection.ftcPose.y;
    }

    public double y(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.y *
                    Math.pow(10.0, decimalPlaces)
            ) /
                Math.pow(10.0, decimalPlaces)
        );
    }

    public double z() {
        return aprilTagDetection.ftcPose.z;
    }

    public double z(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.z *
                    Math.pow(10.0, decimalPlaces)
            ) /
                Math.pow(10.0, decimalPlaces)
        );
    }

    public double range() {
        return aprilTagDetection.ftcPose.range;
    }
    public double bearing() {
        return aprilTagDetection.ftcPose.bearing;
    }

    public double range(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.range *
                    Math.pow(10.0, decimalPlaces)
            ) /
                Math.pow(10.0, decimalPlaces)
        );
    }
    public double bearing(int decimalPlaces) {
        return (
            Math.round(
                aprilTagDetection.ftcPose.bearing *
                    Math.pow(10.0, decimalPlaces)
            ) /
                Math.pow(10.0, decimalPlaces)
        );
    }
}
