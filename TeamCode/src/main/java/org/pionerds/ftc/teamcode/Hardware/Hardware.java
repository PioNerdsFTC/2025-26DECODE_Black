package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.pionerds.ftc.teamcode.Hardware.Drivers.DriverControls;
import org.pionerds.ftc.teamcode.ScheduleTask;
import org.pionerds.ftc.teamcode.Utils.Environment;

/**
 * Class for all the hardware functions of the robot.
 * This should include helper classes and direct controllers for the hardware.
 */
public final class Hardware {

    // Do not make these variables final because they will be updated during TeleOp
    public ElapsedTime elapsedTime;
    public Drivetrain drivetrain = new Drivetrain();
    public Mapping mapping = new Mapping();
    public Vision vision = new Vision();
    public Launcher launcher = new Launcher();
    public Storage storage = new Storage();
    public Aimbot aimbot = new Aimbot();
    public DriverControls driverControls1;
    public DriverControls driverControls2;

    public Gyro gyro = new Gyro();

    public Telemetry telemetry = null;
    /**
     * Whether the hardware class is able to continue running.
     */
    public boolean continueRunning = true;
    private double maxDistanceLaunch;

    /**
     * Use in Autonomous
     *
     * @param hardwareMap
     * @param telemetry
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        try {
            this.telemetry = telemetry;

            mapping.init(this, hardwareMap);
            gyro.init(this);
            drivetrain.init(this, telemetry);
            vision.init(this);
            launcher.init(this);
            storage.init(this);
            gyro.init(this);
            aimbot.init(this, telemetry, AprilTagNames.BlueTarget, AimbotMotorMovement.VELOCITY);
            aimbot.setHardware(this);

        } catch (Exception e) {
            telemetry.addLine(e.getMessage());
            telemetry.update();
        }
    }

    /**
     * Use for TeleOp, where you define driver1 and driver2, which will tick gamepads.
     *
     * @param hardwareMap
     * @param telemetry
     * @param driverControls1
     * @param driverControls2
     */

    public void init(
        HardwareMap hardwareMap,
        Telemetry telemetry,
        DriverControls driverControls1,
        DriverControls driverControls2
    ) {
        try {
            this.telemetry = telemetry;

            mapping.init(this, hardwareMap);
            drivetrain.init(this, telemetry);
            vision.init(this);
            launcher.init(this);
            storage.init(this);
            gyro.init(this); //REMOVE WHEN AT COMPETITION I THINK
            aimbot.setHardware(this);

            this.driverControls1 = driverControls1;
            this.driverControls2 = driverControls2;

        } catch (Exception e) {
            telemetry.addLine(e.getMessage());
            telemetry.update();
        }
    }

    /**
     * Runs for each iteration of the OpMode, may or may not be necessary
     */
    public void tick(Gamepad gamepad1, Gamepad gamepad2) {
        //try {
        driverControls1.tickControls(gamepad1, this);
        driverControls2.tickControls(gamepad2, this);

        double[] angles = this.gyro.getAngles();
        telemetry.addLine("Gyro:");
        telemetry.addLine("Yaw: " + angles[0]);
        // this.launcher.launcherButton(gamepad1);
        //} catch (Exception e) {
        //    this.telemetry.addLine(e.getMessage());
        //    telemetry.update();
        //if (!Environment.competing) {
        //    telemetry.update();
        //    this.continueRunning = false;
        //}
        //}
    }

    public void addElapsedTime(ElapsedTime elapsedTime) {
        this.elapsedTime = elapsedTime;
        ScheduleTask.initTime(elapsedTime);
    }

    public void stop() {
        continueRunning = false;
    }
}
