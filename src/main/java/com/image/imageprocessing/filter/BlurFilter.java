package com.image.imageprocessing.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BlurFilter implements ImageFilter {

    @Override
    public BufferedImage filter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int radius = 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int sumR = 0, sumG = 0, sumB = 0;
                int count = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;

                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int rgb = originalImage.getRGB(nx, ny);
                            sumR += (rgb >> 16) & 0xFF;
                            sumG += (rgb >> 8) & 0xFF;
                            sumB += rgb & 0xFF;
                            count++;
                        }
                    }
                }

                int avgR = sumR / count;
                int avgG = sumG / count;
                int avgB = sumB / count;

                int newRgb = new Color(avgR, avgG, avgB).getRGB();
                blurredImage.setRGB(x, y, newRgb);
            }
        }
        return blurredImage;
    }
}
