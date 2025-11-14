# AutoOpMode Testing Guide

This repository includes unit tests that **run your real AutoOpMode code** with simulated motor hardware and display telemetry output.

## Quick Start - Run Your REAL AutoOpMode Code

To run **your actual AutoOpMode.java code** with simulated hardware:

```bash
# Run the test that executes your real AutoOpMode code
gradle :TeamCode:test --tests AutoOpModeTest.testAutoOpModeSimulation
```

This executes your real AutoOpMode code - it calls your init(), start(), and loop() methods exactly as they would run on the robot, but with mock hardware instead of real motors.

**Important**: This is NOT a fake simulation! The test actually instantiates your AutoOpMode class and runs your code.

## What You'll See

When running your real AutoOpMode code with mock hardware, you'll see:
- **Hardware Initialization**: Output from your init() method
- **State Transitions**: Your actual state machine executing (START_TO_SCORE → PICKUP_BALLS → PARKING → DONE)
- **Telemetry Data**: Real telemetry output from your loop() method showing position (x, y), heading, and robot state
- **Motor Operations**: Your intake and motor control code executing
- **Path Following**: Your path following logic running with simulated hardware

This is NOT a fake simulation - it's your actual code running!

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

✅ **Runs your actual AutoOpMode.java code** - not a fake simulation!
✅ Test autonomous logic without robot hardware
✅ Rapid debugging and development
✅ See exactly what telemetry your code outputs
✅ Verify state machine transitions in your real code
✅ Catch bugs before deployment to the robot

## What's the Difference Between the Test Files?

- **AutoOpModeTest.java**: ✅ **RUNS YOUR REAL AutoOpMode CODE** (Recommended!)
- **AutoOpModeSimulator.java**: ❌ Hardcoded example only (does not run your actual code)

## Full Documentation

See `TeamCode/src/test/java/org/pionerds/ftc/teamcode/README.md` for complete documentation including:
- JUnit test suite details
- Mock object descriptions
- How to extend the tests
- Integration with CI/CD

## Files Created

- `AutoOpModeTest.java` - ⭐ **RECOMMENDED** - JUnit test suite that runs your real AutoOpMode code
- `AutoOpModeSimulator.java` - Hardcoded example only (does not run actual AutoOpMode code)
- `MockTelemetry.java` - Mock telemetry implementation
- `MockHardwareMap.java` - Mock hardware map implementation
- `MockFollower.java` - Mock path follower implementation
- `README.md` - Comprehensive test documentation in test directory
