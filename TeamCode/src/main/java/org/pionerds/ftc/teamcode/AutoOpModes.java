package org.pionerds.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.pionerds.ftc.teamcode.Pathfinding.Constants;

public class AutoOpModes {
    private static final Double robotLength = Constants.localizerConstants.robotLength;

    @Autonomous(name = "Red Auto Near")
    public static class RedNearAuto extends AutoOpMode {
        @Override
        public void init() {
            auto = new Auto(
                new Pose(56, robotLength / 2, 90),
                new Pose(38.75, 33.25, Math.toRadians(0)),
                this.telemetry,
                this.hardwareMap
            );
            auto.init();
        }
    }

    @Autonomous(name = "Red Auto Far")
    public static class RedFarAuto extends AutoOpMode {
        @Override
        public void init() {
            auto = new Auto(
                new Pose(118, 128, 35.954),
                new Pose(38.75, 33.25, Math.toRadians(0)),
                this.telemetry,
                this.hardwareMap
            );
            auto.init();
        }
    }

    @Autonomous(name = "Blue Auto Near")
    public static class BlueNearAuto extends AutoOpMode {
        @Override
        public void init() {
            auto = new Auto(
                new Pose(88, robotLength / 2, 90),
                new Pose(105.25, 110.75, Math.toRadians(0)),
                this.telemetry,
                this.hardwareMap
            );
            auto.init();
        }
    }

    @Autonomous(name = "Blue Auto Far")
    public static class BlueFarAuto extends AutoOpMode {
        @Override
        public void init() {
            auto = new Auto(
                new Pose(26, 128, Math.toRadians(144.046)),
                new Pose(105.25, 110.75, Math.toRadians(0)),
                this.telemetry,
                this.hardwareMap
            );
            auto.init();
        }
    }
}
