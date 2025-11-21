package org.pionerds.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class AutoOpMode extends OpMode {

    private final Double robotWidth = 0.0;
    private final Double robotLength = 0.0;

    private final Auto auto = new Auto(
            new Pose(88, robotLength / 2, Math.toRadians(90)),
            new Pose(38.75, 33.25, Math.toRadians(0)),
            this.telemetry,
            this.hardwareMap
    );

    @Override
    public void init() {
        auto.init();
    }

    @Override
    public void loop() {
        auto.loop();
    }
}
