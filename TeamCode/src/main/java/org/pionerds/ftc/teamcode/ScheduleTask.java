package org.pionerds.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.Iterator;

public class ScheduleTask {
    private static ElapsedTime elapsedTime;
    private static ArrayList<Runnable> runnableList = new ArrayList<Runnable>();
    private static ArrayList<Double> timeList = new ArrayList<Double>();


    public static void initTime(ElapsedTime time){
        elapsedTime = time;
    }

    public static void add(Runnable runnable, double time){
        runnableList.add(runnable);
        timeList.add(time);
    }

    public static void runTasks() {
        if (elapsedTime == null) return;
        double currentTime = elapsedTime.milliseconds();
        
        // Use iterator for safe removal during iteration
        Iterator<Runnable> runnableIterator = runnableList.iterator();
        Iterator<Double> timeIterator = timeList.iterator();
        
        while (runnableIterator.hasNext() && timeIterator.hasNext()) {
            Runnable runnable = runnableIterator.next();
            Double time = timeIterator.next();
            
            if (time < currentTime) {
                runnable.run();
                runnableIterator.remove();
                timeIterator.remove();
            }
        }
    }

}
