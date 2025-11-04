package com.image.imageprocessing.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Adjusts the contrast of an image.
 * Factor > 1.0 increases contrast, factor < 1.0 decreases it.
 */
public class ContrastFilter implements ImageFilter {

    private final double factor;

    public ContrastFilter(double factor) {
        this.factor = factor;
    }

    @Override
    public BufferedImage filter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage contrastedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                r = adjustContrast(r);
                g = adjustContrast(g);
                b = adjustContrast(b);

                int newRgb = new Color(r, g, b).getRGB();
                contrastedImage.setRGB(x, y, newRgb);
            }
        }
        return contrastedImage;
    }

    private int adjustContrast(int value) {
        double result = ((value - 128) * factor) + 128;
        return clamp((int) result);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
