package org.pionerds.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

public class AutoOpModes {
    private static final Double robotLength = 18.0;

    @Autonomous(name = "Red Auto Near")
    public static class RedNearAuto extends AutoOpMode {
        @Override
        public void init() {
            auto = new Auto(
                new Pose(56, robotLength / 2, Math.toRadians(90)),
                new Pose(39.03125,  34.28125, Math.toRadians(0)),
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
                new Pose(118, 128, Math.toRadians(35.954)),
                new Pose(39.03125,  34.28125, Math.toRadians(0)),
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
                new Pose(88, robotLength / 2, Math.toRadians(90)),
                new Pose(104.96875,  34.28125, Math.toRadians(0)),
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
                new Pose(118, 128, Math.toRadians(144.046)),
                new Pose(104.96875,  34.28125, Math.toRadians(0)),
                this.telemetry,
                this.hardwareMap
            );
            auto.init();
        }
    }
}