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
            .mass(4.0);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(0.8)
            .rightFrontMotorName("motor0")
            .rightRearMotorName("motor3")
            .leftRearMotorName("motor2")
            .leftFrontMotorName("motor1")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD);

    public static XDriveConstants driveConstants1 = new XDriveConstants()
            .maxPower(0.8)
            .rightFrontMotorName("motor0")
            .rightRearMotorName("motor3")
            .leftRearMotorName("motor2")
            .leftFrontMotorName("motor1")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD);

    public static DriveEncoderConstants localizerConstants = new DriveEncoderConstants()
            .robotWidth(11.5)
            .robotLength(10.5)
            .forwardTicksToInches(0.01285)
            .strafeTicksToInches(0.01285)
            .turnTicksToInches(0.01285)
            .rightFrontMotorName("motor0")
            .rightRearMotorName("motor3")
            .leftRearMotorName("motor2")
            .leftFrontMotorName("motor1")
            .leftFrontEncoderDirection(Encoder.REVERSE)
            .leftRearEncoderDirection(Encoder.FORWARD)
            .rightFrontEncoderDirection(Encoder.REVERSE)
            .rightRearEncoderDirection(Encoder.FORWARD);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new ExtendedFollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pathConstraints(pathConstraints)
                .driveEncoderLocalizer(localizerConstants)
                .build();
    }
}
