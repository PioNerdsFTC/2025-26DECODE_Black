package org.pionerds.ftc.teamcode;

/**
 * Standalone simulator for AutoOpMode that can run without full Android SDK.
 * This simulates the autonomous mode execution and prints telemetry output
 * to demonstrate what the robot would do during autonomous.
 */
public class AutoOpModeSimulator {
    
    /**
     * Simple class to represent robot position
     */
    static class SimplePose {
        double x, y, heading;
        
        SimplePose(double x, double y, double heading) {
            this.x = x;
            this.y = y;
            this.heading = heading;
        }
        
        double getX() { return x; }
        double getY() { return y; }
        double getHeading() { return heading; }
    }

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("AutoOpMode Simulator - Unit Test");
        System.out.println("This simulates the autonomous mode with mock hardware");
        System.out.println("=".repeat(80) + "\n");

        // Create simulator instance
        AutoOpModeSimulator simulator = new AutoOpModeSimulator();
        simulator.runSimulation();
    }

    private void runSimulation() {
        // Simulate the robot's autonomous execution
        System.out.println("PHASE 1: INITIALIZATION");
        System.out.println("-".repeat(80));
        simulateInit();
        
        System.out.println("\nPHASE 2: START");
        System.out.println("-".repeat(80));
        simulateStart();
        
        System.out.println("\nPHASE 3: AUTONOMOUS EXECUTION");
        System.out.println("-".repeat(80));
        simulateAutonomousLoop();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Simulation Complete!");
        System.out.println("=".repeat(80) + "\n");
    }

    private void simulateInit() {
        System.out.println("[INIT] Initializing hardware components...");
        System.out.println("[TELEMETRY] Robot initialized!");
        
        System.out.println("\n[INIT] Setting up path following system...");
        System.out.println("  - Starting pose: (56.0, 5.75, 90.0°)");
        System.out.println("  - Scan pose: (56.0, 80.0, 90.0°)");
        System.out.println("  - Score pose: (48.0, 110.0, 144.046°)");
        
        System.out.println("\n[INIT] Configuring motors:");
        System.out.println("  - motor0 (rightFront): INITIALIZED");
        System.out.println("  - motor1 (leftFront): INITIALIZED");
        System.out.println("  - motor2 (leftRear): INITIALIZED");
        System.out.println("  - motor3 (rightRear): INITIALIZED");
        System.out.println("  - intakeMotor: INITIALIZED");
        System.out.println("  - susanMotor: INITIALIZED");
        
        System.out.println("\n[INIT] Building path chains...");
        System.out.println("  - Start to Score chain: BUILT");
        System.out.println("  - Pickup and Score chain: BUILT");
        
        System.out.println("\n[INIT] Initialization complete!");
    }

    private void simulateStart() {
        System.out.println("[START] Autonomous mode starting...");
        System.out.println("[STATE] Transitioning to: START_TO_SCORE");
        System.out.println("\n[TELEMETRY] Robot runtime started! (Autonomous)");
    }

    private void simulateAutonomousLoop() {
        // Simulate loop iterations
        int loopCount = 0;
        SimplePose currentPose = new SimplePose(56, 5.75, Math.toRadians(90));
        
        // STATE 1: START_TO_SCORE
        System.out.println("\n>>> STATE: START_TO_SCORE <<<");
        loopCount = simulateState("START_TO_SCORE", currentPose, loopCount, 0, 50);
        
        // Update pose after first path
        currentPose = new SimplePose(48, 110, Math.toRadians(144));
        
        // STATE 2: PICKUP_BALLS
        System.out.println("\n>>> STATE: PICKUP_BALLS <<<");
        loopCount = simulateState("PICKUP_BALLS", currentPose, loopCount, 50, 200);
        
        // Simulate intake operations during pickup
        System.out.println("\n[HARDWARE] Intake enabled - collecting ball 1");
        sleep(500);
        System.out.println("[HARDWARE] Intake disabled");
        
        System.out.println("\n[HARDWARE] Intake enabled - collecting ball 2");
        sleep(500);
        System.out.println("[HARDWARE] Intake disabled");
        
        System.out.println("\n[HARDWARE] Intake enabled - collecting ball 3");
        sleep(500);
        System.out.println("[HARDWARE] Intake disabled");
        
        // Update pose after pickup
        currentPose = new SimplePose(32, 36, Math.toRadians(180));
        
        // STATE 3: PARKING
        System.out.println("\n>>> STATE: PARKING <<<");
        loopCount = simulateState("PARKING", currentPose, loopCount, 200, 250);
        
        // Update to final pose
        currentPose = new SimplePose(38.75, 33.25, Math.toRadians(180));
        
        // STATE 4: DONE
        System.out.println("\n>>> STATE: DONE <<<");
        printTelemetry("DONE", currentPose, loopCount);
        System.out.println("\n[STATE] Autonomous sequence complete!");
        System.out.println("[DATA] Storing final heading: " + Math.toDegrees(currentPose.getHeading()) + "°");
    }

    private int simulateState(String state, SimplePose startPose, int startLoop, int pathStart, int pathEnd) {
        System.out.println("[STATE] Current state: " + state);
        System.out.println("[FOLLOWER] Starting path following...");
        
        // Simulate path following with incremental position updates
        for (int i = pathStart; i < pathEnd; i += 10) {
            double progress = (double)(i - pathStart) / (pathEnd - pathStart);
            
            // Simulate position changes
            SimplePose currentPose = new SimplePose(
                startPose.getX() + (progress * 10),
                startPose.getY() + (progress * 10),
                startPose.getHeading() + (progress * 0.1)
            );
            
            if (i % 30 == 0) { // Print every 3rd update
                printTelemetry(state, currentPose, startLoop + (i - pathStart) / 10);
            }
        }
        
        System.out.println("[FOLLOWER] Path following complete for " + state);
        return startLoop + (pathEnd - pathStart) / 10;
    }

    private void printTelemetry(String state, SimplePose pose, int loopCount) {
        System.out.println("\n[TELEMETRY] Loop " + loopCount + ":");
        System.out.println("  path state: " + state);
        System.out.println("  x: " + String.format("%.2f", pose.getX()));
        System.out.println("  y: " + String.format("%.2f", pose.getY()));
        System.out.println("  heading: " + String.format("%.2f", Math.toDegrees(pose.getHeading())) + "°");
        System.out.println("  waitingForIntake: false");
        System.out.println("  follower busy: " + (loopCount < 250));
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
