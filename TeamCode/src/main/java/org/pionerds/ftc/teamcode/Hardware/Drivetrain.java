package org.pionerds.ftc.teamcode.Hardware;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;

public class Drivetrain {

    private final DcMotorEx[] motors = {null, null, null, null}; //front right, front left, back left, back right
    private final double[] motorSpeed = {0.0, 0.0, 0.0, 0.0};
    private final String[] motorNames = {"motor0", "motor1", "motor2", "motor3"};
    Hardware hardware = null;
    private Telemetry telemetry = null;

    public DcMotorEx[] getMotors(){
        return motors;
    }

    public void init(Hardware hardware, Telemetry telemetry) {
        this.hardware = hardware;
        this.telemetry = telemetry;

        for (int i = 0; i < 4; i++) {
            motors[i] = this.hardware.mapping.getMotor(
                motorNames[i],
                40.0,
                Direction.FORWARD,
                DcMotor.ZeroPowerBehavior.BRAKE
            );
        }

        motors[2].setDirection(Direction.REVERSE);
    }

    /**
     * Sets the motor power to the current motorSpeed array values.
     */
    public void setDriveMotorsPow() {
        for (int i = 0; i < 4; i++) {
            motors[i].setPower(motorSpeed[i]);
            telemetry.addLine(
                "Motor " +
                    i +
                    " Pow: " +
                    (Math.round(motorSpeed[i] * 100) / 100.0)
            );
        }
    }

    /**
     * Scales the motor speeds to fit within the maximum power limit.
     *
     * @param bumperTurning  Whether the robot is turning with the bumper.
     * @param driverControls The driver controls.
     */
    public void scaleMotorsToFit(
        boolean bumperTurning,
        DriverControls driverControls
    ) {
        if (bumperTurning) {
            for (int i = 0; i < 4; i++) {
                motorSpeed[i] += driverControls.getRotationSpeed();
            }
        }

        boolean flag = false;

        for (double speed : motorSpeed) {
            if (Math.abs(speed) > 1) {
                flag = true;
                break;
            }
        }

        if (!flag) return;

        double maxMotorPow = Math.max(
            Math.max(motorSpeed[0], motorSpeed[1]),
            Math.max(motorSpeed[2], motorSpeed[3])
        );
        double minMotorPow = Math.min(
            Math.min(motorSpeed[0], motorSpeed[1]),
            Math.min(motorSpeed[2], motorSpeed[3])
        );

        double finalMotorDivisor = Math.max(maxMotorPow, Math.abs(minMotorPow));

        for (int i = 0; i < 4; i++) {
            motorSpeed[i] /= finalMotorDivisor;
        }

        telemetry.addLine("MotorSpeedDivisor: " + finalMotorDivisor);
    }

    /**
     * Stops all motors.
     */
    public void stopMotors() {
        motorSpeed[0] = 0.00;
        motorSpeed[1] = 0.00;
        motorSpeed[2] = 0.00;
        motorSpeed[3] = 0.00;
    }

    /**
     * Drives the robot with controls.
     *
     * @param driverControls The driver controls object.
     */
    public void robotCentricDrive(DriverControls driverControls) {
        double x = driverControls.getSpeedY();
        double y = driverControls.getSpeedX();

        if (Math.abs(x) < 0.2 && Math.abs(y) < 0.2) {
            stopMotors();
            return;
        }
        motorSpeed[0] = -x - y;
        motorSpeed[1] = -x + y;
        motorSpeed[2] = x + y;
        motorSpeed[3] = x - y;
    }

    /**
     * Drives the robot with controls.
     *
     * @param driverControls         The driver controls object.
     * @param hasDumbDrivePreference Whether to use dumb drive preference.
     * @param bumperTurnPreferred    Whether to prefer bumper turn.
     */
    public void driveWithControls(
        DriverControls driverControls,
        boolean hasDumbDrivePreference,
        boolean bumperTurnPreferred
    ) {
        // for laying flat, use Roll. for vertical, use YAW (test robot rn)
        if (hasDumbDrivePreference) {
            robotCentricDrive(driverControls);
        } else {
            stickDrive(driverControls, hardware.gyro.getAngles()[0]);
            stickTurn(driverControls);
        }

        scaleMotorsToFit(bumperTurnPreferred, driverControls);
        setDriveMotorsPow();
    }

    /**
     * Applies a drive to the drivetrain based on the speed and orientation.
     *
     * @param driverControls The driver controls object.
     * @param orientation    The orientation of the robot.
     */
    public void stickDrive(DriverControls driverControls, double orientation) {
        double x = -driverControls.getSpeedY();
        double y = driverControls.getSpeedX();

        double[] convertedAngle = convertOrientation(x, y, orientation);

        x = convertedAngle[0];
        y = convertedAngle[1];

        telemetry.addLine("\nDrivetrain:");
        telemetry.addLine("X: " + x + "\nY: " + y);

        if (Math.abs(x) < 0.2 && Math.abs(y) < 0.2) {
            stopMotors();
            return;
        }
        double mag = convertedAngle[2];

        motorSpeed[0] = ((-x - y)) * mag;
        motorSpeed[1] = ((-x + y)) * mag;
        motorSpeed[2] = ((x + y)) * mag;
        motorSpeed[3] = ((x - y)) * mag;
    }

    /**
     * Applies a turn to the drivetrain based on the rotation speed.
     *
     * @param driverControls The driver controls object.
     */
    public void stickTurn(DriverControls driverControls) {
        if (Math.abs(driverControls.getRotationSpeed()) > 0.2) {
            telemetry.addLine(
                "Rotation Speed: " + driverControls.getRotationSpeed()
            );

            double x = driverControls.getRotationSpeed();

            for (int i = 0; i < 4; i++) {
                motorSpeed[i] += x;
            }
        }
    }

    /**
     * Converts the orientation of the stick to the robot's orientation.
     *
     * @param x           The x-coordinate of the stick.
     * @param y           The y-coordinate of the stick.
     * @param orientation The orientation of the robot.
     * @return An array containing the converted x and y coordinates and the magnitude.
     */

    public double[] convertOrientation(double x, double y, double orientation) {
        x = -x;
        y = -y;

        orientation = Math.toRadians(orientation);

        double mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        double stickAngle = Math.atan2(y, x);
        double finalAngle = stickAngle + orientation;

        double return_x = Math.cos(finalAngle);
        double return_y = Math.sin(finalAngle);

        return new double[]{-return_x, -return_y, mag};
    }
}
