package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mock implementation of HardwareMap for unit testing.
 * Creates simulated motor and sensor objects.
 */
public class MockHardwareMap extends HardwareMap {
    private final Map<String, HardwareDevice> deviceMap = new HashMap<>();

    public MockHardwareMap() {
        super(null);
        initializeDevices();
    }

    private void initializeDevices() {
        // Create mock motors for the drivetrain
        deviceMap.put("motor0", createMockMotor("motor0")); // rightFront
        deviceMap.put("motor1", createMockMotor("motor1")); // leftFront
        deviceMap.put("motor2", createMockMotor("motor2")); // leftRear
        deviceMap.put("motor3", createMockMotor("motor3")); // rightRear
        
        // Create mock motors for storage system
        deviceMap.put("intakeMotor", createMockMotor("intakeMotor"));
        deviceMap.put("susanMotor", createMockMotor("susanMotor"));
    }

    private DcMotorEx createMockMotor(String name) {
        DcMotorEx motor = Mockito.mock(DcMotorEx.class);
        
        // Set up default behaviors
        Mockito.when(motor.getCurrentPosition()).thenReturn(0);
        Mockito.when(motor.getTargetPosition()).thenReturn(0);
        Mockito.when(motor.getPower()).thenReturn(0.0);
        Mockito.when(motor.getMode()).thenReturn(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        // Allow setting and getting values
        final int[] position = {0};
        final double[] power = {0.0};
        final DcMotor.RunMode[] mode = {DcMotor.RunMode.RUN_WITHOUT_ENCODER};
        
        Mockito.doAnswer(invocation -> {
            position[0] = invocation.getArgument(0);
            return null;
        }).when(motor).setTargetPosition(Mockito.anyInt());
        
        Mockito.doAnswer(invocation -> {
            power[0] = invocation.getArgument(0);
            return null;
        }).when(motor).setPower(Mockito.anyDouble());
        
        Mockito.doAnswer(invocation -> {
            mode[0] = invocation.getArgument(0);
            return null;
        }).when(motor).setMode(Mockito.any(DcMotor.RunMode.class));
        
        Mockito.when(motor.getCurrentPosition()).thenAnswer(invocation -> position[0]);
        Mockito.when(motor.getTargetPosition()).thenAnswer(invocation -> position[0]);
        Mockito.when(motor.getPower()).thenAnswer(invocation -> power[0]);
        Mockito.when(motor.getMode()).thenAnswer(invocation -> mode[0]);
        
        System.out.println("[MOCK HARDWARE] Created motor: " + name);
        return motor;
    }

    @Override
    public <T> T get(Class<? extends T> classOrInterface, String deviceName) {
        HardwareDevice device = deviceMap.get(deviceName);
        if (device == null) {
            System.out.println("[MOCK HARDWARE] WARNING: Device not found: " + deviceName);
            // Return a mock for any missing device
            if (DcMotorEx.class.isAssignableFrom(classOrInterface)) {
                device = createMockMotor(deviceName);
                deviceMap.put(deviceName, device);
            }
        }
        return (T) device;
    }

    @Override
    public <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
        try {
            return get(classOrInterface, deviceName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public HardwareDevice get(String deviceName) {
        return deviceMap.get(deviceName);
    }

    @Override
    public <T> List<T> getAll(Class<? extends T> classOrInterface) {
        return null;
    }

    @Override
    public Set<String> getNamesOf(HardwareDevice device) {
        return null;
    }

    @Override
    public <T> T get(String deviceName) {
        return (T) deviceMap.get(deviceName);
    }
}
