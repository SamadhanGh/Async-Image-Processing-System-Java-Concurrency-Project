package com.image.imageprocessing.concurrency;

import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.utils.PerformanceMetrics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import java.util.concurrent.StructuredTaskScope;

/**
 * Advanced asynchronous image processor using Java 22 StructuredTaskScope
 * for efficient parallel image processing with proper resource management.
 */
public class AsyncImageProcessor {

    private final int tileSize;

    public AsyncImageProcessor(int tileSize) {
        this.tileSize = tileSize;
    }

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

            List<StructuredTaskScope.Subtask<TileResult>> tasks = new ArrayList<>();

            for (int i = 0; i < numHorizontalTiles; i++) {
                for (int j = 0; j < numVerticalTiles; j++) {

                    final int x = i * tileSize;
                    final int y = j * tileSize;
                    final int tileWidth = Math.min(tileSize, width - x);
                    final int tileHeight = Math.min(tileSize, height - y);

                    var task = scope.fork(() -> {
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

            for (var task : tasks) {
                TileResult result = task.get();
                int[] rgb = new int[result.tile.getWidth() * result.tile.getHeight()];

                result.tile.getRGB(
                        0, 0,
                        result.tile.getWidth(),
                        result.tile.getHeight(),
                        rgb,
                        0,
                        result.tile.getWidth()
                );

                resultImage.setRGB(
                        result.x,
                        result.y,
                        result.tile.getWidth(),
                        result.tile.getHeight(),
                        rgb,
                        0,
                        result.tile.getWidth()
                );
            }
        }

        long endTime = System.currentTimeMillis();
        metrics.setProcessingTime(endTime - startTime);

        return resultImage;
    }

    public List<BufferedImage> processMultipleImages(
            List<BufferedImage> images,
            ImageFilter filter,
            PerformanceMetrics metrics)
            throws InterruptedException, ExecutionException {

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<CompletableFuture<BufferedImage>> futures = new ArrayList<>();

            for (BufferedImage image : images) {

                var future = CompletableFuture.supplyAsync(() -> {
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
