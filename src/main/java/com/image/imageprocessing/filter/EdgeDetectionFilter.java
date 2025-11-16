package com.image.imageprocessing.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Implements edge detection using the Sobel operator.
 * Detects edges by computing gradients in horizontal and vertical directions.
 */
public class EdgeDetectionFilter implements ImageFilter {

    private static final int[][] SOBEL_X = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    private static final int[][] SOBEL_Y = {
        {-1, -2, -1},
        {0, 0, 0},
        {1, 2, 1}
    };

    @Override
    public BufferedImage filter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage grayImage = convertToGrayscale(originalImage);
        BufferedImage edgeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = grayImage.getRGB(x + kx, y + ky);
                        int gray = rgb & 0xFF;

                        gx += gray * SOBEL_X[ky + 1][kx + 1];
                        gy += gray * SOBEL_Y[ky + 1][kx + 1];
                    }
                }

                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
                magnitude = Math.min(255, magnitude);

                int newRgb = new Color(magnitude, magnitude, magnitude).getRGB();
                edgeImage.setRGB(x, y, newRgb);
            }
        }

        return edgeImage;
    }

    private BufferedImage convertToGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                int newRgb = new Color(gray, gray, gray).getRGB();
                grayImage.setRGB(x, y, newRgb);
            }
        }
        return grayImage;
    }
}
