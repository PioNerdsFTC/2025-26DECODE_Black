package org.pionerds.ftc.teamcode.Pathfinding;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {

    public static FollowerConstants followerConstants = new FollowerConstants()
//        .forwardZeroPowerAcceleration(-58.206219180121224)
//        .lateralZeroPowerAcceleration(-55.64415956334193)
        .mass(12.3);

    public static MecanumConstants driveConstants = new MecanumConstants()
        .maxPower(1)
        .xVelocity(31.66627410386635)
        .yVelocity(32.07176731087411)
        .rightFrontMotorName("motor0")
        .rightRearMotorName("motor3")
        .leftRearMotorName("motor2")
        .leftFrontMotorName("motor1")
        .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
        .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
        .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
        .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE);

    public static DriveEncoderConstants localizerConstants =
        new DriveEncoderConstants()
            .robotWidth(15)
            .robotLength(11.825)
            .forwardTicksToInches(0.007301349826240794)
            .strafeTicksToInches(0.007301349826240794)
            //.strafeTicksToInches(0.00741590883728377)
            .turnTicksToInches(0.015268271207998244)
            .rightFrontMotorName("motor0")
            .rightRearMotorName("motor3")
            .leftRearMotorName("motor2")
            .leftFrontMotorName("motor1")
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.REVERSE)
            .rightFrontEncoderDirection(Encoder.REVERSE)
            .rightRearEncoderDirection(Encoder.REVERSE);

    public static PathConstraints pathConstraints = new PathConstraints(
        0.99,
        100,
        1,
        1
    );

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
            .mecanumDrivetrain(driveConstants)
            .pathConstraints(pathConstraints)
            .driveEncoderLocalizer(localizerConstants)
            .build();
    }
}
