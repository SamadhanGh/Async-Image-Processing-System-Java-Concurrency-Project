package com.image.imageprocessing.filter;

import java.awt.image.BufferedImage;

/**
 * Interface for image filtering operations.
 * All image filters must implement this interface to be compatible
 * with the async image processing system.
 */
public interface ImageFilter {

    /**
     * Applies the filter to the given image.
     *
     * @param image The source image to filter
     * @return A new BufferedImage with the filter applied
     */
    BufferedImage filter(BufferedImage image);
}
