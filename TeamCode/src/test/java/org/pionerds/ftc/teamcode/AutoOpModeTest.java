package org.pionerds.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.Before;
import org.junit.Test;
import org.pionerds.ftc.teamcode.Hardware.Hardware;
import org.pionerds.ftc.teamcode.Pathfinding.Constants;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Unit test for AutoOpMode that simulates motor hardware and displays telemetry.
 * 
 * ⭐ IMPORTANT: This test runs your REAL AutoOpMode code! ⭐
 * 
 * This is NOT a fake simulation. The test:
 * 1. Creates an actual instance of your AutoOpMode class
 * 2. Calls your real init() method
 * 3. Calls your real start() method
 * 4. Calls your real loop() method repeatedly
 * 
 * The only difference from running on the robot is that hardware (motors, sensors)
 * is simulated using mock objects. This lets you test your autonomous logic without
 * needing physical robot hardware.
 */
public class AutoOpModeTest {

    private AutoOpMode autoOpMode;
    private MockTelemetry mockTelemetry;
    private MockHardwareMap mockHardwareMap;
    private MockFollower mockFollower;

    @Before
    public void setUp() throws Exception {
        // Create mock objects
        mockTelemetry = new MockTelemetry();
        mockHardwareMap = new MockHardwareMap();
        mockFollower = new MockFollower();

        // Create the AutoOpMode instance
        autoOpMode = new AutoOpMode();

        // Inject mock objects into the AutoOpMode
        injectMocks();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Starting AutoOpMode Unit Test");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Use reflection to inject mock objects into AutoOpMode's private fields
     */
    private void injectMocks() throws Exception {
        // Inject telemetry
        Field telemetryField = AutoOpMode.class.getSuperclass().getDeclaredField("telemetry");
        telemetryField.setAccessible(true);
        telemetryField.set(autoOpMode, mockTelemetry);

        // Inject hardwareMap
        Field hardwareMapField = AutoOpMode.class.getSuperclass().getDeclaredField("hardwareMap");
        hardwareMapField.setAccessible(true);
        hardwareMapField.set(autoOpMode, mockHardwareMap);

        System.out.println("[TEST SETUP] Mock telemetry and hardwareMap injected");
    }

    /**
     * Inject the mock follower after initialization
     */
    private void injectMockFollower() throws Exception {
        Field followerField = AutoOpMode.class.getDeclaredField("follower");
        followerField.setAccessible(true);
        followerField.set(autoOpMode, mockFollower);
        System.out.println("[TEST SETUP] Mock follower injected");
    }

    /**
     * Main test that simulates the complete autonomous mode execution
     */
    @Test
    public void testAutoOpModeSimulation() throws Exception {
        System.out.println("\n--- Test: Complete Autonomous Mode Simulation ---\n");

        // Initialize the OpMode
        System.out.println("Initializing AutoOpMode...");
        autoOpMode.init();

        // Inject the mock follower after init
        injectMockFollower();

        // Start the OpMode
        System.out.println("\nStarting AutoOpMode...\n");
        autoOpMode.start();

        // Verify initial state
        assertEquals("Should start in START_TO_SCORE state", 
                     AutoOpMode.State.START_TO_SCORE, 
                     getPathState());

        // Simulate the autonomous loop
        simulateAutonomousExecution();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Test completed successfully!");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Test the telemetry output during autonomous mode
     */
    @Test
    public void testTelemetryOutput() throws Exception {
        System.out.println("\n--- Test: Telemetry Output ---\n");

        autoOpMode.init();
        injectMockFollower();
        autoOpMode.start();

        // Run a few loop iterations
        for (int i = 0; i < 5; i++) {
            autoOpMode.loop();
            mockFollower.update();
        }

        // Verify telemetry contains expected data
        assertTrue("Telemetry should contain path state", 
                   containsTelemetryData("path state"));
        assertTrue("Telemetry should contain x position", 
                   containsTelemetryData("x"));
        assertTrue("Telemetry should contain y position", 
                   containsTelemetryData("y"));
        assertTrue("Telemetry should contain heading", 
                   containsTelemetryData("heading"));

        System.out.println("\nTelemetry output verified successfully!");
    }

    /**
     * Test state transitions through the autonomous sequence
     */
    @Test
    public void testStateTransitions() throws Exception {
        System.out.println("\n--- Test: State Transitions ---\n");

        autoOpMode.init();
        injectMockFollower();
        autoOpMode.start();

        // Should start in START_TO_SCORE
        AutoOpMode.State currentState = getPathState();
        System.out.println("Initial state: " + currentState);
        assertEquals(AutoOpMode.State.START_TO_SCORE, currentState);

        // Simulate completing the first path
        System.out.println("\nSimulating first path completion...");
        mockFollower.completePath();
        autoOpMode.loop();
        
        // Should transition to PICKUP_BALLS
        currentState = getPathState();
        System.out.println("State after first path: " + currentState);
        assertEquals(AutoOpMode.State.PICKUP_BALLS, currentState);

        // Simulate completing the pickup path
        System.out.println("\nSimulating pickup path completion...");
        runLoopsUntilNotBusy(100);
        mockFollower.completePath();
        autoOpMode.loop();
        
        // Should transition to PARKING
        currentState = getPathState();
        System.out.println("State after pickup: " + currentState);
        assertEquals(AutoOpMode.State.PARKING, currentState);

        // Simulate completing the parking path
        System.out.println("\nSimulating parking path completion...");
        mockFollower.completePath();
        autoOpMode.loop();
        
        // Should transition to DONE
        currentState = getPathState();
        System.out.println("State after parking: " + currentState);
        assertEquals(AutoOpMode.State.DONE, currentState);

        System.out.println("\nAll state transitions completed successfully!");
    }

    /**
     * Test motor simulation and position tracking
     */
    @Test
    public void testMotorSimulation() throws Exception {
        System.out.println("\n--- Test: Motor Simulation ---\n");

        autoOpMode.init();
        injectMockFollower();
        autoOpMode.start();

        // Get initial position
        double startX = mockFollower.getPose().getX();
        double startY = mockFollower.getPose().getY();
        System.out.println("Initial position: (" + startX + ", " + startY + ")");

        // Run several updates
        for (int i = 0; i < 20; i++) {
            autoOpMode.loop();
            mockFollower.update();
        }

        // Position should have changed
        double endX = mockFollower.getPose().getX();
        double endY = mockFollower.getPose().getY();
        System.out.println("Final position: (" + endX + ", " + endY + ")");

        assertTrue("X position should have changed", endX != startX);
        assertTrue("Y position should have changed", endY != startY);

        System.out.println("\nMotor simulation working correctly!");
    }

    /**
     * Simulate the complete autonomous execution
     */
    private void simulateAutonomousExecution() throws Exception {
        int loopCount = 0;
        final int MAX_LOOPS = 500; // Prevent infinite loops in test
        
        System.out.println("Starting autonomous loop simulation...\n");

        while (loopCount < MAX_LOOPS && getPathState() != AutoOpMode.State.DONE) {
            // Run one iteration of the loop
            autoOpMode.loop();
            mockFollower.update();

            // Print telemetry every 25 loops to avoid spam
            if (loopCount % 25 == 0) {
                printTelemetrySummary(loopCount);
            }

            loopCount++;
        }

        System.out.println("\nAutonomous simulation completed:");
        System.out.println("  Total loops: " + loopCount);
        System.out.println("  Final state: " + getPathState());
        System.out.println("  Final position: (" + 
                         mockFollower.getPose().getX() + ", " + 
                         mockFollower.getPose().getY() + ", " + 
                         Math.toDegrees(mockFollower.getPose().getHeading()) + "°)");
    }

    /**
     * Run loops until the follower is not busy or max loops reached
     */
    private void runLoopsUntilNotBusy(int maxLoops) throws Exception {
        int count = 0;
        while (mockFollower.isBusy() && count < maxLoops) {
            autoOpMode.loop();
            mockFollower.update();
            count++;
        }
    }

    /**
     * Print a summary of current telemetry data
     */
    private void printTelemetrySummary(int loopCount) {
        System.out.println("--- Loop " + loopCount + " ---");
        System.out.println("State: " + getPathState());
        System.out.println("Position: (" + 
                         String.format("%.2f", mockFollower.getPose().getX()) + ", " + 
                         String.format("%.2f", mockFollower.getPose().getY()) + ")");
        System.out.println("Heading: " + 
                         String.format("%.2f", Math.toDegrees(mockFollower.getPose().getHeading())) + "°");
        System.out.println("Follower busy: " + mockFollower.isBusy());
        System.out.println();
    }

    /**
     * Check if telemetry contains specific data
     */
    private boolean containsTelemetryData(String key) {
        for (String data : mockTelemetry.getData()) {
            if (data.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the current path state using reflection
     */
    private AutoOpMode.State getPathState() {
        try {
            Field pathStateField = AutoOpMode.class.getDeclaredField("pathState");
            pathStateField.setAccessible(true);
            return (AutoOpMode.State) pathStateField.get(autoOpMode);
        } catch (Exception e) {
            fail("Failed to get path state: " + e.getMessage());
            return null;
        }
    }
}
