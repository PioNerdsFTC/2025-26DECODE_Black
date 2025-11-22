package org.pionerds.ftc.teamcode.Utils;

public class DataStorage {

    static double[] angles = {0.0, 0., 0.0}; //yaw, pitch, roll

    public static double getStoredAngle(int index) {
        return angles[index];
    }

    public static void storeAngle(int index, double ang) {
        angles[index] = ang;
    }

    public static double[] getAllStoredAngles() {
        return angles;
    }

    public static void storeAllAngles(double[] angs) {
        angles[0] = angs[0];
        angles[1] = angs[1];
        angles[2] = angs[2];
    }

}
