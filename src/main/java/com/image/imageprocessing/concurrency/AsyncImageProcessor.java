package com.image.imageprocessing.concurrency;

import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.utils.PerformanceMetrics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class AsyncImageProcessor {

    private final int tileSize;

    @FunctionalInterface
    public interface TileUpdateCallback {
        void onTileProcessed(BufferedImage tile, int x, int y);
    }

    public AsyncImageProcessor(int tileSize) {
        this.tileSize = tileSize;
    }

    public BufferedImage processWithStructuredConcurrency(
            BufferedImage image,
            ImageFilter filter,
            PerformanceMetrics metrics,
            TileUpdateCallback callback)
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

                        // 🔥 Live UI tile update callback
                        if (callback != null) {
                            callback.onTileProcessed(processedTile, x, y);
                        }

                        return new TileResult(processedTile, x, y);
                    });

                    tasks.add(task);
                }
            }

            scope.join();
            scope.throwIfFailed();

            // Final merge (ensures final consistent output)
            for (var task : tasks) {
                TileResult result = task.get();
                resultImage.getGraphics().drawImage(result.tile, result.x, result.y, null);
            }
        }

        long endTime = System.currentTimeMillis();
        metrics.setProcessingTime(endTime - startTime);

        return resultImage;
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
