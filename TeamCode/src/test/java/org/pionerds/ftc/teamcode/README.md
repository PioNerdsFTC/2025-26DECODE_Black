# AutoOpMode Unit Tests

This directory contains unit tests for the AutoOpMode autonomous operation mode. These tests simulate the robot's hardware and allow you to see the telemetry output without needing actual robot hardware.

## Test Files

### ⭐ AutoOpModeTest.java (RECOMMENDED - Runs Your REAL Code!)
**This test runs your ACTUAL AutoOpMode.java code** with simulated hardware. This is NOT a fake simulation!

The test instantiates your real AutoOpMode class and calls your actual init(), start(), and loop() methods exactly as they would execute on the robot. The only difference is that hardware (motors, sensors) is simulated using mock objects.

**How to run:**
```bash
# Using Gradle (requires network connection for dependencies)
gradle :TeamCode:test --tests AutoOpModeTest

# Or run specific test methods:
gradle :TeamCode:test --tests AutoOpModeTest.testAutoOpModeSimulation
gradle :TeamCode:test --tests AutoOpModeTest.testTelemetryOutput
gradle :TeamCode:test --tests AutoOpModeTest.testStateTransitions
```

**What it does:**
- ✅ Runs your actual AutoOpMode.java code (not a fake!)
- ✅ Calls your real init(), start(), and loop() methods
- ✅ Executes your state machine logic (START_TO_SCORE → PICKUP_BALLS → PARKING → DONE)
- ✅ Shows real telemetry output from your code
- ✅ Simulates hardware responses (motors, encoders, path following)
- ✅ Validates state transitions and behavior

**Key Point**: This executes the exact same code that runs on your robot! You can add breakpoints, debug, and see exactly what your autonomous mode does.

### AutoOpModeSimulator.java (Example Only - Not Real Code)
A standalone hardcoded simulation that shows **example** output. This does NOT run your actual AutoOpMode code.

**How to run:**
```bash
cd TeamCode/src/test/java
javac org/pionerds/ftc/teamcode/AutoOpModeSimulator.java
java org.pionerds.ftc.teamcode.AutoOpModeSimulator
```

**What it does:**
- ❌ Does NOT run your AutoOpMode code
- Shows a hardcoded example of what autonomous execution might look like
- Useful for understanding the expected flow without running actual code

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



### Mock Classes

#### MockTelemetry.java
Simulates the FTC telemetry system and captures all telemetry output for verification.

#### MockHardwareMap.java
Simulates the FTC HardwareMap and creates mock motor objects using Mockito.

#### MockFollower.java
Simulates the pedropathing Follower class for path following without actual hardware.

## Features

✅ **Runs Real Code** - AutoOpModeTest and RunAutoOpModeWithMockHardware execute your actual AutoOpMode.java
✅ **No Hardware Required** - Tests run completely in software simulation
✅ **Telemetry Display** - See all telemetry output as it would appear on the driver station
✅ **Motor Simulation** - Simulates motor positions, power, and encoder values
✅ **Path Following** - Simulates the robot following paths through the autonomous sequence
✅ **State Machine Testing** - Verifies correct state transitions
✅ **Intake Operations** - Simulates ball collection with intake motor control

## Which Test Should I Use?

- **Want to test your actual AutoOpMode code?** → Use `AutoOpModeTest.java` ⭐
- **Just want to see example output?** → Use `AutoOpModeSimulator.java`

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
