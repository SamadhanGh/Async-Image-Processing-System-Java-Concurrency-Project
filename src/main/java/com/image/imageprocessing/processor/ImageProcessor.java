package com.image.imageprocessing.processor;

import com.image.imageprocessing.filter.ImageFilter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ImageProcessor {

    private final ExecutorService virtualThreadExecutor;

    public ImageProcessor() {
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public BufferedImage processImageWithFilter(BufferedImage image, ImageFilter imageFilter) throws InterruptedException, ExecutionException {
        int tileSize = 50;
        int width = image.getWidth();
        int height = image.getHeight();

        int numHorizontalTiles = (width + tileSize - 1) / tileSize;
        int numVerticalTiles = (height + tileSize - 1) / tileSize;

        BufferedImage resultImage = new BufferedImage(width, height, image.getType());

        List<Future<TileResult>> futures = new ArrayList<>();

        for (int i = 0; i < numHorizontalTiles; i++) {
            for (int j = 0; j < numVerticalTiles; j++) {
                final int x = i * tileSize;
                final int y = j * tileSize;
                final int tileWidth = Math.min(tileSize, width - x);
                final int tileHeight = Math.min(tileSize, height - y);

                Future<TileResult> future = virtualThreadExecutor.submit(() -> {
                    BufferedImage subImage = image.getSubimage(x, y, tileWidth, tileHeight);
                    BufferedImage processedTile = imageFilter.filter(subImage);
                    return new TileResult(processedTile, x, y);
                });

                futures.add(future);
            }
        }

        for (Future<TileResult> future : futures) {
            try {
                TileResult result = future.get();
                int[] rgb = new int[result.tile.getWidth() * result.tile.getHeight()];
                result.tile.getRGB(0, 0, result.tile.getWidth(), result.tile.getHeight(), rgb, 0, result.tile.getWidth());
                resultImage.setRGB(result.x, result.y, result.tile.getWidth(), result.tile.getHeight(), rgb, 0, result.tile.getWidth());
            } catch (Exception ex) {
                System.err.println("Error processing tile: " + ex.getMessage());
                throw ex;
            }
        }

        return resultImage;
    }

    public void shutdown() {
        virtualThreadExecutor.shutdown();
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
