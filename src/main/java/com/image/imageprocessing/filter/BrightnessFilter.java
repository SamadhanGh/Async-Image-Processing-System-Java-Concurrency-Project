package com.image.imageprocessing.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Adjusts the brightness of an image.
 * Positive values increase brightness, negative values decrease it.
 */
public class BrightnessFilter implements ImageFilter {

    private final int adjustment;

    public BrightnessFilter(int adjustment) {
        this.adjustment = adjustment;
    }

    @Override
    public BufferedImage filter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage brightenedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                r = clamp(r + adjustment);
                g = clamp(g + adjustment);
                b = clamp(b + adjustment);

                int newRgb = new Color(r, g, b).getRGB();
                brightenedImage.setRGB(x, y, newRgb);
            }
        }
        return brightenedImage;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
