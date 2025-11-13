# AutoOpMode Testing Guide

This repository includes unit tests for the AutoOpMode autonomous operation that simulate motor hardware and display telemetry output.

## Quick Start

To run the AutoOpMode simulator and see telemetry output:

```bash
cd TeamCode/src/test/java
javac org/pionerds/ftc/teamcode/AutoOpModeSimulator.java
java org.pionerds.ftc.teamcode.AutoOpModeSimulator
```

## What You'll See

The simulator displays:
- **Hardware Initialization**: All motors and sensors being initialized
- **State Transitions**: Movement through START_TO_SCORE → PICKUP_BALLS → PARKING → DONE
- **Telemetry Data**: Position (x, y), heading, and robot state at each loop iteration
- **Motor Operations**: Intake motor activations for ball collection
- **Path Following**: Simulation of the robot following autonomous paths

## Example Output

```
================================================================================
AutoOpMode Simulator - Unit Test
This simulates the autonomous mode with mock hardware
================================================================================

PHASE 1: INITIALIZATION
--------------------------------------------------------------------------------
[INIT] Initializing hardware components...
[TELEMETRY] Robot initialized!

[INIT] Configuring motors:
  - motor0 (rightFront): INITIALIZED
  - motor1 (leftFront): INITIALIZED
  - motor2 (leftRear): INITIALIZED
  - motor3 (rightRear): INITIALIZED
  - intakeMotor: INITIALIZED
  - susanMotor: INITIALIZED
...

PHASE 3: AUTONOMOUS EXECUTION
--------------------------------------------------------------------------------
>>> STATE: START_TO_SCORE <<<
[TELEMETRY] Loop 0:
  path state: START_TO_SCORE
  x: 56.00
  y: 5.75
  heading: 90.00°
  waitingForIntake: false
  follower busy: true
...
```

## Benefits

✅ Test autonomous logic without robot hardware
✅ Rapid debugging and development
✅ See exactly what telemetry would display
✅ Verify state machine transitions
✅ Understand robot behavior before deployment

## Full Documentation

See `TeamCode/src/test/java/org/pionerds/ftc/teamcode/README.md` for complete documentation including:
- JUnit test suite details
- Mock object descriptions
- How to extend the tests
- Integration with CI/CD

## Files Created

- `AutoOpModeSimulator.java` - Standalone simulator with telemetry display
- `AutoOpModeTest.java` - JUnit test suite
- `MockTelemetry.java` - Mock telemetry implementation
- `MockHardwareMap.java` - Mock hardware map implementation
- `MockFollower.java` - Mock path follower implementation
- `README.md` - Comprehensive test documentation
