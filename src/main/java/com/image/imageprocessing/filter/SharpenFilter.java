package com.image.imageprocessing.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Applies a sharpening filter to enhance edges and details in the image.
 * Uses a 3x3 convolution kernel for sharpening.
 */
public class SharpenFilter implements ImageFilter {

    private static final double[][] SHARPEN_KERNEL = {
        {0, -1, 0},
        {-1, 5, -1},
        {0, -1, 0}
    };

    @Override
    public BufferedImage filter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage sharpenedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double sumR = 0, sumG = 0, sumB = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = originalImage.getRGB(x + kx, y + ky);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        double kernel = SHARPEN_KERNEL[ky + 1][kx + 1];
                        sumR += r * kernel;
                        sumG += g * kernel;
                        sumB += b * kernel;
                    }
                }

                int newR = clamp((int) sumR);
                int newG = clamp((int) sumG);
                int newB = clamp((int) sumB);

                int newRgb = new Color(newR, newG, newB).getRGB();
                sharpenedImage.setRGB(x, y, newRgb);
            }
        }

        copyBorders(originalImage, sharpenedImage);
        return sharpenedImage;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private void copyBorders(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        for (int x = 0; x < width; x++) {
            dst.setRGB(x, 0, src.getRGB(x, 0));
            dst.setRGB(x, height - 1, src.getRGB(x, height - 1));
        }

        for (int y = 0; y < height; y++) {
            dst.setRGB(0, y, src.getRGB(0, y));
            dst.setRGB(width - 1, y, src.getRGB(width - 1, y));
        }
    }
}
