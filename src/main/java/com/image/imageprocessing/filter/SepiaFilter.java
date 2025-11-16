package com.image.imageprocessing.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Applies a sepia tone effect to create a warm, vintage appearance.
 * Uses the standard sepia tone matrix for color transformation.
 */
public class SepiaFilter implements ImageFilter {

    @Override
    public BufferedImage filter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage sepiaImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                tr = Math.min(255, tr);
                tg = Math.min(255, tg);
                tb = Math.min(255, tb);

                int newRgb = new Color(tr, tg, tb).getRGB();
                sepiaImage.setRGB(x, y, newRgb);
            }
        }
        return sepiaImage;
    }
}
