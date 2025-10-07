package org.pionerds.ftc.teamcode.Pathfinding.Experimental;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * Weighted lookup table for FTC robot control.
 * Stores discretized robot states (position + heading) with weighted actions.
 * Can load from CSV and persist to JSON.
 */
public class WeightedLookupTable {

    private final Map<String, Map<String, Double>> lut = new HashMap<>();
    private final File saveFile;

    private final double posSector;     // quantization step for X/Y position
    private final double headingSector; // quantization step for heading

    public WeightedLookupTable(File directory, String filename, double posSector, double headingSector) {
        this.saveFile = new File(directory, filename);
        this.posSector = posSector;
        this.headingSector = headingSector;
        load(); // attempt to load existing LUT
    }

    /** Quantize a continuous state into a discrete key */
    private String quantizeState(double x, double y, double heading) {
        int qx = (int) Math.round(x / posSector);
        int qy = (int) Math.round(y / posSector);
        int qh = (int) Math.round(heading / headingSector);
        return qx + "," + qy + "," + qh;
    }

    /** Record a new action for a given state */
    public void record(double x, double y, double heading, String action) {
        String key = quantizeState(x, y, heading);
        lut.putIfAbsent(key, new HashMap<>());
        Map<String, Double> actions = lut.get(key);
        actions.put(action, actions.getOrDefault(action, 0.0) + 1.0);
        normalize(actions);
    }

    /** Return the highest weighted action for a state */
    public String getBest(double x, double y, double heading) {
        String key = quantizeState(x, y, heading);
        Map<String, Double> actions = lut.get(key);
        if (actions == null || actions.isEmpty()) return null;
        return actions.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /** Return a random action based on weights */
    public String getRandom(double x, double y, double heading) {
        String key = quantizeState(x, y, heading);
        Map<String, Double> actions = lut.get(key);
        if (actions == null || actions.isEmpty()) return null;

        double r = Math.random();
        double cumulative = 0.0;
        for (Map.Entry<String, Double> entry : actions.entrySet()) {
            cumulative += entry.getValue();
            if (r <= cumulative) return entry.getKey();
        }
        return null;
    }

    /** Normalize the action weights to sum to 1 */
    private void normalize(Map<String, Double> actions) {
        double sum = actions.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum == 0) return;
        for (String key : actions.keySet()) {
            actions.put(key, actions.get(key) / sum);
        }
    }

    /** Build LUT from a CSV file */
    public void buildFromCSV(File csvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length < 8) continue;

                double posX = Double.parseDouble(tokens[1]);
                double posY = Double.parseDouble(tokens[2]);
                double leftX = Double.parseDouble(tokens[3]);
                double leftY = Double.parseDouble(tokens[4]);
                double rightX = Double.parseDouble(tokens[5]);
                double rightY = Double.parseDouble(tokens[6]);
                double heading = Double.parseDouble(tokens[7]);

                String action = leftX + "," + leftY + "," + rightX + "," + rightY;
                record(posX, posY, heading, action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Save LUT to JSON file */
    public void save() {
        try (FileWriter writer = new FileWriter(saveFile)) {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, Map<String, Double>> entry : lut.entrySet()) {
                JSONObject actionsJson = new JSONObject(entry.getValue());
                json.put(entry.getKey(), actionsJson);
            }
            writer.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Load LUT from JSON file */
    public void load() {
        if (!saveFile.exists()) return;
        try (FileReader reader = new FileReader(saveFile)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            JSONObject json = new JSONObject(sb.toString());
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String stateKey = keys.next();
                JSONObject actionsJson = json.getJSONObject(stateKey);
                Map<String, Double> actions = new HashMap<>();
                Iterator<String> actIter = actionsJson.keys();
                while (actIter.hasNext()) {
                    String action = actIter.next();
                    actions.put(action, actionsJson.getDouble(action));
                }
                lut.put(stateKey, actions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Debug: print the LUT */
    public void printTable() {
        for (Map.Entry<String, Map<String, Double>> entry : lut.entrySet()) {
            System.out.println("State: " + entry.getKey());
            for (Map.Entry<String, Double> a : entry.getValue().entrySet()) {
                System.out.printf("  Action: %s -> %.2f%n", a.getKey(), a.getValue());
            }
        }
    }

    /** Example usage */
    public static void main(String[] args) {
        WeightedLookupTable lut = new WeightedLookupTable(new File("."), "lut.json", 0.5, 15);
        File csv = new File("training_data.csv");
        lut.buildFromCSV(csv);
        lut.save();
        lut.printTable();

        String best = lut.getBest(1.2, 3.7, 90);
        System.out.println("Best action: " + best);
    }
}