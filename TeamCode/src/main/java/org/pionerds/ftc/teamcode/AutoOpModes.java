package org.pionerds.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

public class AutoOpModes {
    private final Double robotWidth = 0.0;
    private final Double robotLength = 0.0;

    @Autonomous(name = "Red Auto Near")
    public class RedNearAuto extends AutoOpMode {
        private final Auto auto = new Auto(
            new Pose(56, robotLength / 2, 90),
            new Pose(38.75, 33.25, Math.toRadians(0)),
            this.telemetry,
            this.hardwareMap
        );
    }

    @Autonomous(name = "Red Auto Far")
    public static class RedFarAuto extends AutoOpMode {
        private final Auto auto = new Auto(
            new Pose(118, 128, 35.954),
            new Pose(38.75, 33.25, Math.toRadians(0)),
            this.telemetry,
            this.hardwareMap
        );
    }

    @Autonomous(name = "Blue Auto Near")
    public class BlueNearAuto extends AutoOpMode {
        private final Auto auto = new Auto(
            new Pose(88, robotLength / 2, 90),
            new Pose(105.25, 110.75, Math.toRadians(0)),
            this.telemetry,
            this.hardwareMap
        );
    }

    @Autonomous(name = "Blue Auto Far")
    public class BlueFarAuto extends AutoOpMode {
        private final Auto auto = new Auto(
            new Pose(26, 128, 144.046),
            new Pose(105.25, 110.75, Math.toRadians(0)),
            this.telemetry,
            this.hardwareMap
        );
    }
}
