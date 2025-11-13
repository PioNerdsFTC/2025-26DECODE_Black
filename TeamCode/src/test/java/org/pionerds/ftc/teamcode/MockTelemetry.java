package org.pionerds.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of Telemetry for unit testing.
 * Captures all telemetry output so it can be inspected during tests.
 */
public class MockTelemetry implements Telemetry {
    private final List<String> lines = new ArrayList<>();
    private final List<String> data = new ArrayList<>();
    private boolean autoClear = true;

    @Override
    public Item addData(String caption, String format, Object... args) {
        String entry = caption + ": " + String.format(format, args);
        data.add(entry);
        System.out.println("[TELEMETRY] " + entry);
        return new MockItem(entry);
    }

    @Override
    public Item addData(String caption, Object value) {
        String entry = caption + ": " + value;
        data.add(entry);
        System.out.println("[TELEMETRY] " + entry);
        return new MockItem(entry);
    }

    @Override
    public <T> Item addData(String caption, Func<T> valueProducer) {
        String entry = caption + ": " + valueProducer.value();
        data.add(entry);
        System.out.println("[TELEMETRY] " + entry);
        return new MockItem(entry);
    }

    @Override
    public <T> Item addData(String caption, String format, Func<T> valueProducer) {
        String entry = caption + ": " + String.format(format, valueProducer.value());
        data.add(entry);
        System.out.println("[TELEMETRY] " + entry);
        return new MockItem(entry);
    }

    @Override
    public boolean removeItem(Item item) {
        return false;
    }

    @Override
    public void clear() {
        if (autoClear) {
            lines.clear();
            data.clear();
        }
    }

    @Override
    public void clearAll() {
        lines.clear();
        data.clear();
    }

    @Override
    public Object addAction(Runnable action) {
        return null;
    }

    @Override
    public boolean removeAction(Object token) {
        return false;
    }

    @Override
    public void speak(String text) {
        System.out.println("[TELEMETRY SPEAK] " + text);
    }

    @Override
    public void speak(String text, String languageCode, String countryCode) {
        System.out.println("[TELEMETRY SPEAK] " + text);
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public Line addLine() {
        return new MockLine();
    }

    @Override
    public Line addLine(String lineCaption) {
        lines.add(lineCaption);
        System.out.println("[TELEMETRY] " + lineCaption);
        return new MockLine(lineCaption);
    }

    @Override
    public boolean removeLine(Line line) {
        return false;
    }

    @Override
    public boolean isAutoClear() {
        return autoClear;
    }

    @Override
    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    @Override
    public int getMsTransmissionInterval() {
        return 250;
    }

    @Override
    public void setMsTransmissionInterval(int msTransmissionInterval) {
    }

    @Override
    public String getItemSeparator() {
        return " | ";
    }

    @Override
    public void setItemSeparator(String itemSeparator) {
    }

    @Override
    public String getCaptionValueSeparator() {
        return " : ";
    }

    @Override
    public void setCaptionValueSeparator(String captionValueSeparator) {
    }

    @Override
    public void setDisplayFormat(DisplayFormat displayFormat) {
    }

    @Override
    public Log log() {
        return new MockLog();
    }

    public List<String> getLines() {
        return new ArrayList<>(lines);
    }

    public List<String> getData() {
        return new ArrayList<>(data);
    }

    private static class MockItem implements Item {
        private final String value;

        MockItem(String value) {
            this.value = value;
        }

        @Override
        public String getCaption() {
            return value.split(":")[0];
        }

        @Override
        public Item setCaption(String caption) {
            return this;
        }

        @Override
        public Item setValue(String format, Object... args) {
            return this;
        }

        @Override
        public Item setValue(Object value) {
            return this;
        }

        @Override
        public <T> Item setValue(Func<T> valueProducer) {
            return this;
        }

        @Override
        public <T> Item setValue(String format, Func<T> valueProducer) {
            return this;
        }

        @Override
        public Item setRetained(Boolean retained) {
            return this;
        }

        @Override
        public boolean isRetained() {
            return false;
        }

        @Override
        public Item addData(String caption, String format, Object... args) {
            return this;
        }

        @Override
        public Item addData(String caption, Object value) {
            return this;
        }

        @Override
        public <T> Item addData(String caption, Func<T> valueProducer) {
            return this;
        }

        @Override
        public <T> Item addData(String caption, String format, Func<T> valueProducer) {
            return this;
        }
    }

    private class MockLine implements Line {
        private String caption;

        MockLine() {
            this("");
        }

        MockLine(String caption) {
            this.caption = caption;
        }

        @Override
        public Item addData(String caption, String format, Object... args) {
            return new MockItem(caption + ": " + String.format(format, args));
        }

        @Override
        public Item addData(String caption, Object value) {
            return new MockItem(caption + ": " + value);
        }

        @Override
        public <T> Item addData(String caption, Func<T> valueProducer) {
            return new MockItem(caption + ": " + valueProducer.value());
        }

        @Override
        public <T> Item addData(String caption, String format, Func<T> valueProducer) {
            return new MockItem(caption + ": " + String.format(format, valueProducer.value()));
        }
    }

    private static class MockLog implements Log {
        @Override
        public int getCapacity() {
            return 100;
        }

        @Override
        public void setCapacity(int capacity) {
        }

        @Override
        public DisplayOrder getDisplayOrder() {
            return DisplayOrder.NEWEST_FIRST;
        }

        @Override
        public void setDisplayOrder(DisplayOrder displayOrder) {
        }

        @Override
        public void add(String entry) {
            System.out.println("[TELEMETRY LOG] " + entry);
        }

        @Override
        public void add(String format, Object... args) {
            System.out.println("[TELEMETRY LOG] " + String.format(format, args));
        }
    }
}
