package org.pionerds.ftc.teamcode.Pathfinding;

import com.pedropathing.Drivetrain;
import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import java.util.Arrays;
import java.util.List;

public class XDrive extends Drivetrain {
    public XDriveConstants constants;
    private final DcMotorEx leftFront;
    private final DcMotorEx leftRear;
    private final DcMotorEx rightFront;
    private final DcMotorEx rightRear;
    private final List<DcMotorEx> motors;
    private final VoltageSensor voltageSensor;
    private double motorCachingThreshold;
    private boolean useBrakeModeInTeleOp;
    private double staticFrictionCoefficient;

    public XDrive(HardwareMap hardwareMap, XDriveConstants xDriveConstants) {
        constants = xDriveConstants;
        this.maxPowerScaling = xDriveConstants.maxPower;
        this.motorCachingThreshold = xDriveConstants.motorCachingThreshold;
        this.useBrakeModeInTeleOp = xDriveConstants.useBrakeModeInTeleOp;
        voltageSensor = hardwareMap.voltageSensor.iterator().next();
        leftFront = hardwareMap.get(DcMotorEx.class, xDriveConstants.leftFrontMotorName);
        leftRear = hardwareMap.get(DcMotorEx.class, xDriveConstants.leftRearMotorName);
        rightRear = hardwareMap.get(DcMotorEx.class, xDriveConstants.rightRearMotorName);
        rightFront = hardwareMap.get(DcMotorEx.class, xDriveConstants.rightFrontMotorName);
        motors = Arrays.asList(leftFront, leftRear, rightFront, rightRear);
        for (DcMotorEx motor : motors) {
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            motor.setMotorType(motorConfigurationType);
        }
        setMotorsToFloat();
        breakFollowing();
    }

    public void updateConstants() {
        leftFront.setDirection(constants.leftFrontMotorDirection);
        leftRear.setDirection(constants.leftRearMotorDirection);
        rightFront.setDirection(constants.rightFrontMotorDirection);
        rightRear.setDirection(constants.rightRearMotorDirection);
        this.motorCachingThreshold = constants.motorCachingThreshold;
        this.useBrakeModeInTeleOp = constants.useBrakeModeInTeleOp;
        this.voltageCompensation = constants.useVoltageCompensation;
        this.nominalVoltage = constants.nominalVoltage;
        this.staticFrictionCoefficient = constants.staticFrictionCoefficient;
    }

    public double[] calculateDrive(Vector correctivePower, Vector headingPower, Vector pathingPower, double robotHeading) {
        correctivePower.setMagnitude(Math.min(correctivePower.getMagnitude(), maxPowerScaling));
        pathingPower.setMagnitude(Math.min(pathingPower.getMagnitude(), maxPowerScaling));
        Vector V_translational = correctivePower.plus(pathingPower);
        if (V_translational.getMagnitude() > maxPowerScaling) {
            V_translational.setMagnitude(maxPowerScaling);
        }
        V_translational.rotateVector(-robotHeading);
        double V_x = V_translational.getXComponent();
        double V_y = V_translational.getYComponent();
        double V_rotation = headingPower.getXComponent();
        if (Math.abs(V_rotation) > maxPowerScaling) {
            V_rotation = Math.signum(V_rotation) * maxPowerScaling;
        }
        double[] wheelPowers = new double[4];
        wheelPowers[0] = V_y + V_x + V_rotation;
        wheelPowers[1] = V_y - V_x - V_rotation;
        wheelPowers[2] = V_y - V_x + V_rotation;
        wheelPowers[3] = V_y + V_x - V_rotation;
        if (voltageCompensation) {
            double voltageNormalized = getVoltageNormalized();
            for (int i = 0; i < wheelPowers.length; i++) {
                wheelPowers[i] *= voltageNormalized;
            }
        }
        double wheelPowerMax = 0.0;
        for (double power : wheelPowers) {
            wheelPowerMax = Math.max(wheelPowerMax, Math.abs(power));
        }
        if (wheelPowerMax > maxPowerScaling) {
            double scalingFactor = maxPowerScaling / wheelPowerMax;
            for (int i = 0; i < wheelPowers.length; i++) {
                wheelPowers[i] *= scalingFactor;
            }
        }
        return wheelPowers;
    }

    private void setMotorsToBrake() {
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    private void setMotorsToFloat() {
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }

    public void breakFollowing() {
        for (DcMotorEx motor : motors) {
            motor.setPower(0);
        }
        setMotorsToFloat();
    }

    public void runDrive(double[] drivePowers) {
        for (int i = 0; i < motors.size(); i++) {
            if (Math.abs(motors.get(i).getPower() - drivePowers[i]) > motorCachingThreshold) {
                motors.get(i).setPower(drivePowers[i]);
            }
        }
    }

    @Override
    public void startTeleopDrive() {
        if (useBrakeModeInTeleOp) {
            setMotorsToBrake();
        }
    }

    @Override
    public void startTeleopDrive(boolean brakeMode) {
        if (brakeMode) {
            setMotorsToBrake();
        } else {
            setMotorsToFloat();
        }
    }

    public void getAndRunDrivePowers(Vector correctivePower, Vector headingPower, Vector pathingPower, double robotHeading) {
        runDrive(calculateDrive(correctivePower, headingPower, pathingPower, robotHeading));
    }

    public double xVelocity() {
        return constants.xVelocity;
    }

    public double yVelocity() {
        return constants.yVelocity;
    }

    public void setXVelocity(double xMovement) { constants.setXVelocity(xMovement); }
    public void setYVelocity(double yMovement) { constants.setYVelocity(yMovement); }

    public double getStaticFrictionCoefficient() {
        return staticFrictionCoefficient;
    }

    @Override
    public double getVoltage() {
        return voltageSensor.getVoltage();
    }

    private double getVoltageNormalized() {
        double voltage = getVoltage();
        return (nominalVoltage - (nominalVoltage * staticFrictionCoefficient)) / (voltage - ((nominalVoltage * nominalVoltage / voltage) * staticFrictionCoefficient));
    }

    public String debugString() {
        return "XDrive{" +
                " leftFront=" + leftFront +
                ", leftRear=" + leftRear +
                ", rightFront=" + rightFront +
                ", rightRear=" + rightRear +
                ", motors=" + motors +
                ", motorCachingThreshold=" + motorCachingThreshold +
                ", useBrakeModeInTeleOp=" + useBrakeModeInTeleOp +
                '}';
    }

    public List<DcMotorEx> getMotors() {
        return motors;
    }
}
