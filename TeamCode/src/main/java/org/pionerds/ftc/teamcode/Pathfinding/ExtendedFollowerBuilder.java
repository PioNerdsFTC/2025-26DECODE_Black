package org.pionerds.ftc.teamcode.Pathfinding;

import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ExtendedFollowerBuilder extends FollowerBuilder {
    private final HardwareMap hardwareMap;

    public ExtendedFollowerBuilder(FollowerConstants constants, HardwareMap hardwareMap) {
        super(constants, hardwareMap);
        this.hardwareMap = hardwareMap;
    }

    public ExtendedFollowerBuilder xDrivetrain(XDriveConstants xDriveConstants) {
        return (ExtendedFollowerBuilder) setDrivetrain(new XDrive(hardwareMap, xDriveConstants));
    }
}
