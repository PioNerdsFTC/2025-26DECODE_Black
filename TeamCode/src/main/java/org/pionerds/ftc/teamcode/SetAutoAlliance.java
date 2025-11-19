package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@TeleOp(name = "RedAutoOpMode", group = "Tools")
public class SetAutoAlliance extends OpMode {

    private static final String ALLIANCE_DIR = "/sdcard/FIRST";
    private static final String ALLIANCE_FILE = ALLIANCE_DIR + "/alliance.txt";
    private static final File file = new File(ALLIANCE_DIR + "/alliance.txt");
    private enum Alliance {
        RED,
        BLUE,
        ERROR
    }
    private Alliance alliance;
    private Alliance prevAlliance;

    @Override
    public void init() {
        telemetry.addData("Current Alliance", alliance);
        telemetry.addData("AllianceSetter", "Press A to toggle alliance and save");
        telemetry.update();
    }

    private List<String> getPrevAlliance(File textFile) throws IOException {
        return Files.readAllLines(textFile.toPath());
    }

    @Override
    public void loop() {

    }
}
