package org.pionerds.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Storage class manages the robot's lazy susan mechanism, intake system, and artifact inventory.
 * The lazy susan is a rotating platform with 6 positions (3 INTAKE positions and 3 OUTPUT positions)
 * that can hold and transfer artifacts (game pieces).
 */
public class Storage {
    private Hardware hardware;

    // Servos and motors for storage mechanism
    private CRServo feederServo;           // Continuous rotation servo that feeds artifacts to launcher
    private DcMotorEx susanMotorEx;        // Motor that rotates the lazy susan platform
    private DcMotorEx intakeMotorEx;       // Motor that pulls artifacts into the system
    private Servo bumpUpServo;             // Servo that lifts artifacts for feeding
    
    // Position tracking for lazy susan motor
    private int susanTargetTicks = 0;      // Target encoder position for lazy susan motor
    private final int susanVelocityRequest = 300;  // Requested velocity for susan motor
    private final int gearRatio = 3;       // Gear ratio: (90/30) = 3:1
    private final int TPR = 288 * gearRatio; // Ticks per revolution after gearing (288 * 3 = 864)

    // Initialization and state tracking
    private boolean isInitialized = false;  // Tracks if hardware components are successfully initialized
    private Artifact[] inventory = new Artifact[] {Artifact.EMPTY, Artifact.EMPTY, Artifact.EMPTY};  // Stores what artifact is in each of 3 storage slots
    private LazySusanPositions currentSusanPositionEnum = LazySusanPositions.INTAKE1;  // Current position of lazy susan
    
    Storage() {}

    /**
     * Initializes the storage system by retrieving and configuring all hardware components.
     * Maps servo and motor names to actual hardware devices and sets up initial configurations.
     * 
     * @param hardware The main hardware object containing hardware map and telemetry
     */
    public void init(Hardware hardware) {
        this.hardware = hardware;

        // Retrieve hardware components from the hardware map
        CRServo feeder = this.hardware.mapping.getContinuousServo("feeder", DcMotorSimple.Direction.FORWARD);
        Servo bumpUpFeeder = this.hardware.mapping.getServoMotor("bumpUp");
        DcMotorEx intake = this.hardware.mapping.getMotor("intakeMotor",40.0,DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT);
        DcMotorEx susan = this.hardware.mapping.getMotor("susanMotor", 40.0, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);

        // Verify all components were successfully mapped
        if (feeder != null && bumpUpFeeder != null && susan != null && intake != null) {
            feederServo = feeder;
            bumpUpServo = bumpUpFeeder;
            susanMotorEx = susan;
            susanMotorEx.setTargetPositionTolerance(1);  // Set precision for position control (within 1 tick)
            intakeMotorEx = intake;
            isInitialized = true;  // Mark system as ready
        } else {
            // If any component failed to initialize, alert the user via telemetry
            hardware.telemetry.clearAll();
            hardware.telemetry.addLine("STORAGE IS NOT INITIALIZED!");
        }
    }

    /**
     * Activates the feeder mechanism to transfer artifacts from storage to the launcher.
     * Only activates if the lazy susan is in the correct position (within 1 tick tolerance).
     */
    public void enableFeeder() {
        if (!isInitialized) return;
        feederServo.setPower(1);  // Start continuous rotation servo
        if(isSusanInPosition(1)) {  // Verify susan is precisely positioned
            bumpUpServo.setPosition(0.05);  // Lift artifact into feeding position
        } else {
            hardware.telemetry.addLine("Susan Not In Position.");
        }
    }

    /**
     * Stops the feeder mechanism and returns the bump-up servo to neutral position.
     */
    public void disableFeeder() {
        if (!isInitialized) return;
        feederServo.setPower(0);     // Stop feeder servo
        bumpUpServo.setPosition(0);  // Lower bump-up servo
    }

    /**
     * Returns the current inventory array showing artifacts in each storage slot.
     * @return Array of 3 Artifact objects representing slots 0, 1, and 2
     */
    public Artifact[] getInventory(){
        return inventory;
    }

    /**
     * Manually sets an artifact type in a specific inventory slot.
     * @param artifact The type of artifact (EMPTY, PURPLE, or GREEN)
     * @param position Index of the storage slot (0, 1, or 2)
     */
    public void setArtifact(Artifact artifact, int position){
        inventory[position] = artifact;
    }

    /**
     * Starts the intake motor to pull artifacts into the storage system.
     */
    public void enableIntake(){
        if (!isInitialized) return;
        intakeMotorEx.setPower(0.7);
    }

    /**
     * Stops the intake motor.
     */
    public void disableIntake(){
        if (!isInitialized) return;
        intakeMotorEx.setPower(0);
    }

    /**
     * Stops the intake motor and updates the inventory with the collected artifact.
     * Uses the current lazy susan position to determine which slot to update.
     * 
     * @param artifact The type of artifact that was just collected
     */
    public void disableIntake(Artifact artifact){
        if (!isInitialized) return;
        intakeMotorEx.setPower(0);
        // Extract position index from enum name (e.g., "INTAKE1" or "OUTPUT1" -> index 0)
        try {
            char lastChar = currentSusanPositionEnum.name().charAt(currentSusanPositionEnum.name().length() - 1);
            if (Character.isDigit(lastChar)) {
                int index = Character.getNumericValue(lastChar) - 1; // Convert 1-based to 0-based
                if (index >= 0 && index < inventory.length) {
                    inventory[index] = artifact;
                }
            }
        } catch (Exception e) {
            // Log error but don't crash
            hardware.telemetry.addLine("Error updating inventory: " + e.getMessage());
        }
    }

    /**
     * Rotates the lazy susan to a specified position using the shortest path.
     * The lazy susan can rotate continuously, so this method calculates whether to reach
     * the target position in the current revolution, previous revolution, or next revolution.
     * 
     * @param susanPosition Target position enum (INTAKE1-3 or OUTPUT1-3)
     */
    public void moveSusanTo(LazySusanPositions susanPosition) {
        if (!isInitialized) return;
        if (susanPosition.equals(currentSusanPositionEnum)) return;  // Already at target

        int tickOffset = 0; // default: ((0 * 360)*(TPR/360)) = 0
        int currentPos = susanMotorEx.getCurrentPosition();
        int revolutions = currentPos / TPR; // Current full revolution count (integer division truncates)

        // Calculate tick offset for each position within a revolution
        // The lazy susan has 6 positions spaced around a circle:
        // INTAKE positions are at 0°, 120°, and 240°
        // OUTPUT positions are at 60°, 180°, and 300°
        switch (susanPosition) {
            case INTAKE2:
                tickOffset = 96; // ((1/3 * 360)*(TPR/360)) = 96 ticks (120 degrees)
                break;
            case INTAKE3:
                tickOffset = 192; // ((2/3 * 360)*(TPR/360)) = 192 ticks (240 degrees)
                break;
            case OUTPUT1:
                tickOffset = 144; // (((180 - (0) * 360))*(TPR/360)) = 144 ticks (180 degrees)
                break;
            case OUTPUT2:
                tickOffset = 240; // (((180 + (1/3) * 360))*(tpr/360)) = 240 ticks (300 degrees)
                break;
            case OUTPUT3:
                tickOffset = 48; // (((180 - (1/3) * 360))*(TPR/360)) = 48 ticks (60 degrees)
                break;
        }
        currentSusanPositionEnum = susanPosition;  // Update tracked position
        tickOffset *= gearRatio;  // Apply gear ratio to get actual motor ticks

        // Calculate three possible target positions (previous, current, and next revolution)
        int currentRevolutionTick = revolutions * TPR + tickOffset;      // Target in current revolution
        int lessRevolutionTick = currentRevolutionTick - TPR;            // Target in previous revolution
        int moreRevolutionTick = currentRevolutionTick + TPR;            // Target in next revolution

        // Calculate distances to each possible target
        int distanceBetweenNowAndCurrent = Math.abs(
            currentRevolutionTick - currentPos
        );
        int distanceBetweenNowAndPrevious = Math.abs(
            lessRevolutionTick - currentPos
        );
        int distanceBetweenNowAndNext = Math.abs(
            moreRevolutionTick - currentPos
        );

        // Choose the closest target (shortest rotation path)
        if (
            distanceBetweenNowAndPrevious <= distanceBetweenNowAndCurrent &&
            distanceBetweenNowAndPrevious <= distanceBetweenNowAndNext
        ) {
            susanTargetTicks = lessRevolutionTick;
        } else if (
            distanceBetweenNowAndCurrent <= distanceBetweenNowAndPrevious &&
            distanceBetweenNowAndCurrent <= distanceBetweenNowAndNext
        ) {
            susanTargetTicks = currentRevolutionTick;
        } else {
            susanTargetTicks = moreRevolutionTick;
        }

        // Output debugging information to telemetry
        hardware.telemetry.addLine("\n\n");
        hardware.telemetry.addLine(
            "susanPosition: " + susanMotorEx.getCurrentPosition()
        );
        hardware.telemetry.addLine("susanTarget: " + susanTargetTicks);
        hardware.telemetry.addLine("susanRunMode: " + susanMotorEx.getMode());

        updateSusan();  // Apply the new target position
    }

    /**
     * Determines which artifact type should be scored next based on the number of balls on the ramp.
     * Uses the vision system's artifact pattern and cycles through it using modulo arithmetic.
     * 
     * @param ballsOnRamp Number of balls currently on the ramp (used to determine position in pattern)
     * @return The artifact type that should be scored next
     */
    public Artifact bestArtifact(int ballsOnRamp){
        Artifact[] pattern = hardware.vision.getArtifactPattern();
        // Use modulo 3 to cycle through the pattern (pattern has 3 artifacts)
        return pattern[ballsOnRamp%3];
    }

    /**
     * Automatically moves the lazy susan to the optimal position for the next artifact.
     * This is the main automation method that combines artifact selection with positioning.
     * 
     * @param ballsOnRamp Number of balls currently scored (determines next artifact to score)
     */
    public void automatedSusan(int ballsOnRamp){
        // Move to the best position for the next artifact that should be scored
        moveSusanTo(bestBallPos(currentSusanPositionEnum, bestArtifact(ballsOnRamp).name(),true));
    }

    public void goToEmptySusan(){
        moveSusanTo(bestBallPos(currentSusanPositionEnum,"EMPTY",false));
    }

    /**
     * Displays the current state of the storage automation algorithm on telemetry.
     * Shows current position, target position, best artifact, and inventory contents.
     * 
     * @param ballsOnRamp Number of balls scored (used to calculate next artifact)
     */
    public void printAlgorithmData(int ballsOnRamp){
        Artifact bestArtifact = bestArtifact(ballsOnRamp);
        LazySusanPositions targetPos = bestBallPos(currentSusanPositionEnum, bestArtifact.name(),true);
        hardware.telemetry.addLine("\n========== STORAGE ==========");
        hardware.telemetry.addLine("CurrentPos: "+currentSusanPositionEnum.name());
        hardware.telemetry.addLine("TargetPos: "+targetPos.name());
        hardware.telemetry.addLine("BestArtifact: "+bestArtifact.name());
        hardware.telemetry.addLine("\nInventory:");
        hardware.telemetry.addLine(inventory[0].name());
        hardware.telemetry.addLine(inventory[1].name());
        hardware.telemetry.addLine(inventory[2].name());
        hardware.telemetry.addLine("\n=============================");
    }

    /**
     * Determines the best lazy susan position to use for scoring a specific artifact color.
     * This method implements a smart rotation algorithm that:
     * 1. First checks if the current position has the desired artifact
     * 2. If not, checks adjacent positions (counter-clockwise then clockwise)
     * 3. If no match found, returns any non-empty slot
     * 
     * The algorithm minimizes rotation by preferring closer positions.
     * 
     * @param currentPosEnum Current position of the lazy susan
     * @param idealColor Name of the artifact color we want to score
     * @return The lazy susan position enum that should be moved to
     */
    public LazySusanPositions bestBallPos(LazySusanPositions currentPosEnum, String idealColor, boolean output) {
        String currentPos = currentPosEnum.name();
        String finalPos = "OUTPUT1";  // Default fallback position
        if(!output) finalPos = "INPUT1";

        String[] positions =
                {"OUTPUT1","OUTPUT2","OUTPUT3"};  // Output positions correspond to inventory indices
        if(!output) positions = new String[] {"INPUT1", "INPUT2", "INPUT3"};

        // Extract the numeric index from the position name (e.g., "OUTPUT1" -> 0, "INTAKE2" -> 1)
        // This works because both INTAKE and OUTPUT positions use 1-based numbering
        int pos = 0;
        try {
            char lastChar = currentPos.charAt(currentPos.length() - 1);
            if (Character.isDigit(lastChar)) {
                pos = Character.getNumericValue(lastChar) - 1; // Convert 1-based to 0-based index
                // Ensure pos is within valid range [0, 2]
                if (pos < 0 || pos > 2) {
                    pos = 0;
                }
            }
        } catch (Exception e) {
            pos = 0; // Default to first position on error
        }
        
        // Check if current position has the ideal artifact
        if (inventory[pos].name().equals(idealColor)) {
            finalPos = positions[pos];
        }
        // Check one position counter-clockwise (subtract 1, wrap around using modulo)
        // Example: if pos=0, (0+2)%3=2 (checks position 2)
        else if (inventory[((pos+2)%3)].name().equals(idealColor)) {
            finalPos = positions[((pos+2)%3)];
        }
        // Check one position clockwise (add 1, wrap around using modulo)
        // Example: if pos=2, (2+1)%3=0 (checks position 0)
        else if (inventory[((pos+1)%3)].name().equals(idealColor)) {
            finalPos = positions[((pos+1)%3)];
        }
        // If no position has the ideal artifact, find any non-empty position
        else {
            for(int i = 0; i<3; i++){
                if(!inventory[i].equals(Artifact.EMPTY)) finalPos = positions[i];
            }

        }
        
        // Convert the position string back to a LazySusanPositions enum
        for(LazySusanPositions susanPosReturn : LazySusanPositions.values()){
            if(susanPosReturn.name().equals(finalPos)){
                return susanPosReturn;
            }
        }

        if(!output) return  LazySusanPositions.INTAKE1;
        return LazySusanPositions.OUTPUT1;  // Final fallback
    }

    /**
     * Sends the lazy susan motor to its target position using RUN_TO_POSITION mode.
     * Sets full power to reach the target as quickly as possible.
     */
    public void updateSusan() {
        if (!isInitialized) return;
        susanMotorEx.setTargetPosition(susanTargetTicks);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        susanMotorEx.setPower(1);  // Full power for fast positioning
    }

    /**
     * Manually rotates the lazy susan at a specified power level for testing.
     * Uses RUN_USING_ENCODER mode for controlled rotation without a specific target.
     * 
     * @param power Power level from -1.0 to 1.0 (negative = reverse)
     */
    public void testRotateSusan(double power) {
        if (!isInitialized) return;
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        susanMotorEx.setPower(power);
    }

    /**
     * Immediately stops the lazy susan motor and switches to encoder mode.
     */
    public void stopSusan() {
        if (!isInitialized) return;
        susanMotorEx.setVelocity(0);
        susanMotorEx.setPower(0);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Resets the lazy susan motor encoder to zero.
     * Should be called during initialization to establish a known starting position.
     */
    public void resetEncoderSusan() {
        if (!isInitialized) return;
        susanMotorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        susanMotorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Gets the current encoder position of the lazy susan motor.
     * @return Current encoder tick count
     */
    public int getSusanCurrentTicks(){
        if (!isInitialized) return 0;
        return susanMotorEx.getCurrentPosition();
    }

    /**
     * Checks if the lazy susan is within tolerance of its target position.
     * @param ticks Tolerance in encoder ticks
     * @return true if within tolerance, false otherwise
     */
    public boolean isSusanInPosition(int ticks){
        if (!isInitialized) return false;
        return (Math.abs(susanTargetTicks-getSusanCurrentTicks())<ticks);
    }

    public LazySusanPositions getCurrentSusanPositionEnum(){
        return currentSusanPositionEnum;
    }

}
