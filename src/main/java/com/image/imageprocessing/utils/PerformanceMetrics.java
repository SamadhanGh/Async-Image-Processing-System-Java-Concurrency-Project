package com.image.imageprocessing.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks performance metrics for image processing operations.
 * Thread-safe implementation using atomic variables.
 */
public class PerformanceMetrics {

    private long processingTime;
    private int threadsUsed;
    private int totalTiles;
    private AtomicInteger processedTiles;
    private long memoryUsed;
    private String filterName;

    public PerformanceMetrics() {
        this.processedTiles = new AtomicInteger(0);
    }

    public void reset() {
        processingTime = 0;
        threadsUsed = 0;
        totalTiles = 0;
        processedTiles.set(0);
        memoryUsed = 0;
        filterName = null;
    }

    public void captureMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        this.memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    public void incrementProcessedTiles() {
        processedTiles.incrementAndGet();
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public int getThreadsUsed() {
        return threadsUsed;
    }

    public void setThreadsUsed(int threadsUsed) {
        this.threadsUsed = threadsUsed;
    }

    public int getTotalTiles() {
        return totalTiles;
    }

    public void setTotalTiles(int totalTiles) {
        this.totalTiles = totalTiles;
    }

    public int getProcessedTiles() {
        return processedTiles.get();
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    @Override
    public String toString() {
        return String.format(
            "Performance Metrics:\n" +
            "  Filter: %s\n" +
            "  Processing Time: %d ms\n" +
            "  Tiles Processed: %d/%d\n" +
            "  Threads Used: %d\n" +
            "  Memory Usage: %d MB",
            filterName != null ? filterName : "N/A",
            processingTime,
            processedTiles.get(),
            totalTiles,
            threadsUsed,
            memoryUsed
        );
    }

    public String toShortString() {
        return String.format("%d ms | %d tiles | %d MB",
            processingTime, processedTiles.get(), memoryUsed);
    }
}
