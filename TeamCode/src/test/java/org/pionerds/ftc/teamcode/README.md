# AutoOpMode Unit Tests

This directory contains unit tests for the AutoOpMode autonomous operation mode. These tests simulate the robot's hardware and allow you to see the telemetry output without needing actual robot hardware.

## Test Files

### AutoOpModeSimulator.java
A standalone simulator that can be run directly to see the telemetry output from the autonomous mode execution.

**How to run:**
```bash
# Navigate to the test directory
cd TeamCode/src/test/java

# Compile the simulator
javac org/pionerds/ftc/teamcode/AutoOpModeSimulator.java

# Run the simulator
java org.pionerds.ftc.teamcode.AutoOpModeSimulator
```

**What it does:**
- Simulates the complete autonomous mode execution
- Shows initialization of all hardware components (motors, sensors, etc.)
- Displays telemetry output that would normally appear on the driver station
- Simulates all four states: START_TO_SCORE → PICKUP_BALLS → PARKING → DONE
- Shows motor positions, headings, and state transitions
- Simulates intake operations for collecting balls

**Sample Output:**
```
================================================================================
AutoOpMode Simulator - Unit Test
This simulates the autonomous mode with mock hardware
================================================================================

PHASE 1: INITIALIZATION
--------------------------------------------------------------------------------
[INIT] Initializing hardware components...
[TELEMETRY] Robot initialized!
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

### AutoOpModeTest.java
A JUnit-based unit test suite that uses mock objects to test the AutoOpMode class.

**How to run:**
```bash
# Using Gradle (requires network connection)
gradle :TeamCode:test --tests AutoOpModeTest
```

**What it tests:**
- Complete autonomous mode simulation
- Telemetry output validation
- State transitions (START_TO_SCORE → PICKUP_BALLS → PARKING → DONE)
- Motor simulation and position tracking
- Path following behavior

### Mock Classes

#### MockTelemetry.java
Simulates the FTC telemetry system and captures all telemetry output for verification.

#### MockHardwareMap.java
Simulates the FTC HardwareMap and creates mock motor objects using Mockito.

#### MockFollower.java
Simulates the pedropathing Follower class for path following without actual hardware.

## Features

✅ **No Hardware Required** - Tests run completely in software simulation
✅ **Telemetry Display** - See all telemetry output as it would appear on the driver station
✅ **Motor Simulation** - Simulates motor positions, power, and encoder values
✅ **Path Following** - Simulates the robot following paths through the autonomous sequence
✅ **State Machine Testing** - Verifies correct state transitions
✅ **Intake Operations** - Simulates ball collection with intake motor control

## Why This is Useful

1. **Development Without Hardware** - Test autonomous logic without needing access to the robot
2. **Rapid Iteration** - Quickly test changes without deploying to the robot
3. **Debugging** - See detailed telemetry output to understand what the robot is doing
4. **CI/CD Integration** - Can be run in continuous integration pipelines
5. **Documentation** - Demonstrates the expected behavior of the autonomous mode

## Extending the Tests

To add more tests:

1. Add new test methods to `AutoOpModeTest.java`
2. Use the mock objects to set up specific scenarios
3. Verify behavior using JUnit assertions
4. For quick demonstrations, extend `AutoOpModeSimulator.java`

## Notes

- The simulator uses simplified physics and timing
- Path completion is simulated rather than calculated based on actual robot dynamics
- Motor encoder values are simplified for testing purposes
- Some hardware features (vision, gyro, etc.) may need additional mocking for complete tests
