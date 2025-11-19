package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "Red Auto Near", group = "default")
public class RedAuto extends OpMode {

    private final Auto autoOpMode = new Auto(StartPosition.RED_NEAR, this.telemetry, this.hardwareMap);
    @Override
    public void init() {
        autoOpMode.init();
    }

    @Override
    public void loop() {
        autoOpMode.loop();
    }
}
