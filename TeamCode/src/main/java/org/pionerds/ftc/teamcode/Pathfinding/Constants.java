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
            .forwardZeroPowerAcceleration(-58.206219180121224)
            .lateralZeroPowerAcceleration(-55.64415956334193)
            .mass(4.2);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .xVelocity(31.66627410386635)
            .yVelocity(32.07176731087411)
            .rightFrontMotorName("motor0")
            .rightRearMotorName("motor3")
            .leftRearMotorName("motor2")
            .leftFrontMotorName("motor1")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD);

    public static DriveEncoderConstants localizerConstants = new DriveEncoderConstants()
            .robotWidth(11.5)
            .robotLength(10.5)
            .forwardTicksToInches(0.006425/2)
            .strafeTicksToInches(0.0066/2)
            .turnTicksToInches(0.006507362092869509)
            .rightFrontMotorName("motor0")
            .rightRearMotorName("motor3")
            .leftRearMotorName("motor2")
            .leftFrontMotorName("motor1")
            .leftFrontEncoderDirection(Encoder.REVERSE)
            .leftRearEncoderDirection(Encoder.REVERSE)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pathConstraints(pathConstraints)
                .driveEncoderLocalizer(localizerConstants)
                .build();
    }
}
