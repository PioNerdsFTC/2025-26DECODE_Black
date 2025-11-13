package org.pionerds.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;

/**
 * Mock implementation of Follower for unit testing.
 * Simulates path following behavior without actual hardware.
 */
public class MockFollower extends Follower {
    private Pose currentPose;
    private boolean busy = false;
    private int updateCount = 0;
    private static final int UPDATES_PER_PATH = 50; // Simulate 50 updates to complete a path
    private int pathUpdateCount = 0;
    private boolean paused = false;

    public MockFollower() {
        super(null, null, null);
        this.currentPose = new Pose(0, 0, 0);
    }

    @Override
    public void setStartingPose(Pose pose) {
        this.currentPose = pose;
        System.out.println("[MOCK FOLLOWER] Starting pose set to: (" + pose.getX() + ", " + pose.getY() + ", " + Math.toDegrees(pose.getHeading()) + "°)");
    }

    @Override
    public Pose getPose() {
        return currentPose;
    }

    @Override
    public void followPath(Path path) {
        followPath(path, false);
    }

    @Override
    public void followPath(Path path, boolean holdEnd) {
        if (!paused) {
            busy = true;
            pathUpdateCount = 0;
            System.out.println("[MOCK FOLLOWER] Following path (holdEnd=" + holdEnd + ")");
        }
    }

    @Override
    public void followPath(PathChain pathChain) {
        followPath(pathChain, false);
    }

    @Override
    public void followPath(PathChain pathChain, boolean holdEnd) {
        if (!paused) {
            busy = true;
            pathUpdateCount = 0;
            System.out.println("[MOCK FOLLOWER] Following path chain (holdEnd=" + holdEnd + ")");
        }
    }

    @Override
    public void update() {
        updateCount++;
        
        if (busy && !paused) {
            pathUpdateCount++;
            
            // Simulate gradual movement along the path
            // For simplicity, we'll just increment position slightly each update
            double progress = (double) pathUpdateCount / UPDATES_PER_PATH;
            
            if (pathUpdateCount >= UPDATES_PER_PATH) {
                busy = false;
                System.out.println("[MOCK FOLLOWER] Path completed at update " + updateCount);
            }
            
            // Simulate position changes
            currentPose = new Pose(
                currentPose.getX() + 0.5,
                currentPose.getY() + 0.5,
                currentPose.getHeading() + 0.01
            );
        }
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    @Override
    public void pausePathFollowing() {
        paused = true;
        System.out.println("[MOCK FOLLOWER] Path following paused");
    }

    @Override
    public void resumePathFollowing() {
        paused = false;
        System.out.println("[MOCK FOLLOWER] Path following resumed");
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public boolean isPaused() {
        return paused;
    }

    // Simulate completing a path instantly for testing
    public void completePath() {
        pathUpdateCount = UPDATES_PER_PATH;
        busy = false;
        System.out.println("[MOCK FOLLOWER] Path force-completed");
    }
}
