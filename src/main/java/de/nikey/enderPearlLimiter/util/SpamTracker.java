package de.nikey.enderPearlLimiter.util;

public class SpamTracker {
    private final int timeWindow;
    private final int maxPearls;
    private final long[] timestamps;
    private int index = 0;

    public SpamTracker(int timeWindow, int maxPearls) {
        this.timeWindow = timeWindow;
        this.maxPearls = maxPearls;
        this.timestamps = new long[maxPearls];
    }

    public boolean increment() {
        long now = System.currentTimeMillis();
        timestamps[index] = now;
        index = (index + 1) % timestamps.length;

        int count = 0;
        for (long ts : timestamps) {
            if (ts > now - timeWindow) count++;
        }

        return count >= maxPearls;
    }
}
