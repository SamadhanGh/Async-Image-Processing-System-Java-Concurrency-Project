package com.image.imageprocessing.concurrency;

import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.utils.PerformanceMetrics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Advanced asynchronous image processor using Java 21's StructuredTaskScope
 * for efficient parallel image processing with proper resource management.
 */
public class AsyncImageProcessor {

    private final int tileSize;

    public AsyncImageProcessor(int tileSize) {
        this.tileSize = tileSize;
    }

    /**
     * Processes an image using StructuredTaskScope for structured concurrency.
     * Divides the image into tiles and processes each tile in parallel using virtual threads.
     *
     * @param image The source image to process
     * @param filter The filter to apply
     * @param metrics Performance metrics tracker
     * @return The processed image
     * @throws InterruptedException if processing is interrupted
     * @throws ExecutionException if processing fails
     */
    public BufferedImage processWithStructuredConcurrency(
            BufferedImage image,
            ImageFilter filter,
            PerformanceMetrics metrics)
            throws InterruptedException, ExecutionException {

        long startTime = System.currentTimeMillis();
        int width = image.getWidth();
        int height = image.getHeight();

        int numHorizontalTiles = (width + tileSize - 1) / tileSize;
        int numVerticalTiles = (height + tileSize - 1) / tileSize;
        int totalTiles = numHorizontalTiles * numVerticalTiles;

        metrics.setTotalTiles(totalTiles);

        BufferedImage resultImage = new BufferedImage(width, height, image.getType());

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<Subtask<TileResult>> tasks = new ArrayList<>();

            for (int i = 0; i < numHorizontalTiles; i++) {
                for (int j = 0; j < numVerticalTiles; j++) {
                    final int x = i * tileSize;
                    final int y = j * tileSize;
                    final int tileWidth = Math.min(tileSize, width - x);
                    final int tileHeight = Math.min(tileSize, height - y);

                    Subtask<TileResult> task = scope.fork(() -> {
                        BufferedImage subImage = image.getSubimage(x, y, tileWidth, tileHeight);
                        BufferedImage processedTile = filter.filter(subImage);
                        metrics.incrementProcessedTiles();
                        return new TileResult(processedTile, x, y);
                    });

                    tasks.add(task);
                }
            }

            scope.join();
            scope.throwIfFailed();

            for (Subtask<TileResult> task : tasks) {
                TileResult result = task.get();
                int[] rgb = new int[result.tile.getWidth() * result.tile.getHeight()];
                result.tile.getRGB(0, 0, result.tile.getWidth(), result.tile.getHeight(),
                                  rgb, 0, result.tile.getWidth());
                resultImage.setRGB(result.x, result.y, result.tile.getWidth(),
                                  result.tile.getHeight(), rgb, 0, result.tile.getWidth());
            }
        }

        long endTime = System.currentTimeMillis();
        metrics.setProcessingTime(endTime - startTime);
        metrics.setThreadsUsed(Runtime.getRuntime().availableProcessors());

        return resultImage;
    }

    /**
     * Processes multiple images concurrently using virtual threads.
     *
     * @param images List of images to process
     * @param filter Filter to apply to all images
     * @param metrics Performance metrics tracker
     * @return List of processed images
     * @throws InterruptedException if processing is interrupted
     * @throws ExecutionException if processing fails
     */
    public List<BufferedImage> processMultipleImages(
            List<BufferedImage> images,
            ImageFilter filter,
            PerformanceMetrics metrics)
            throws InterruptedException, ExecutionException {

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<BufferedImage>> futures = new ArrayList<>();

            for (BufferedImage image : images) {
                CompletableFuture<BufferedImage> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return processWithStructuredConcurrency(image, filter, metrics);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process image", e);
                    }
                }, executor);

                futures.add(future);
            }

            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        }
    }

    /**
     * Internal class to hold tile processing results.
     */
    private static class TileResult {
        final BufferedImage tile;
        final int x;
        final int y;

        TileResult(BufferedImage tile, int x, int y) {
            this.tile = tile;
            this.x = x;
            this.y = y;
        }
    }
}
