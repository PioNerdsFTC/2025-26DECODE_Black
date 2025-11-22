package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

public class ScheduleTask {
    private static final ArrayList<Runnable> runnableList = new ArrayList<Runnable>();
    private static final ArrayList<Double> timeList = new ArrayList<Double>();
    private static ElapsedTime elapsedTime;

    public static void initTime(ElapsedTime time) {
        elapsedTime = time;
    }

    public static void add(Runnable runnable, double time) {
        runnableList.add(runnable);
        timeList.add(time);
    }

    public static void runTasks() {
        if (elapsedTime == null) return;
        for (int i = 0; i < runnableList.size(); i++) {
            if (timeList.get(i) < elapsedTime.milliseconds()) {
                runnableList.get(i).run();
                runnableList.remove(i);
                timeList.remove(i);
                i--;
            }
        }
    }

}
