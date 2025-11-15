package org.pionerds.ftc.teamcode.Utils;

import java.util.ArrayList;

/**
 * Goal of this is to make a unified interface for writing errors and logs cleanly.
 * Right now we really don't have a solution for proper logs outside of actually writing to the screen.
 * Eventually we should be able to configure different levels of reporting (and maybe some way to export logs?)
 */
public class Logger {

    // java 8 doesn't have tuples... We'll have to figure something out.
    public static ArrayList<String> logs = new ArrayList<String>();

    /**
     * Log an error
     */
    public void error() {
    }

    /**
     * Log a simple log message
     */
    public void info() {
    }

    /**
     * Log a debug log message
     */
    public void debug() {
    }

    public enum LogType {
        ERROR,
        INFO,
        DEBUG,
    }
}
