package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class AutoOpMode extends OpMode {

    protected Auto auto;

    @Override
    public void init() {
        // Auto will be initialized in subclasses
    }

    @Override
    public void start() {
        if (auto != null) {
            auto.start();
        }
    }

    @Override
    public void loop() {
        if (auto != null) {
            auto.loop();
        }
    }
}