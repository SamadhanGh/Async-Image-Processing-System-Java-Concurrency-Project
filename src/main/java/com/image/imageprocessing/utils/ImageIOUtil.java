package com.image.imageprocessing.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for image I/O operations.
 * Handles reading and writing images with proper error handling.
 */
public class ImageIOUtil {

    private static final String OUTPUT_DIRECTORY = "output";
    private static final String DEFAULT_FORMAT = "png";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Reads an image from the specified file path.
     *
     * @param filePath Path to the image file
     * @return BufferedImage object
     * @throws IOException if reading fails
     */
    public static BufferedImage readImage(String filePath) throws IOException {
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            throw new IOException("Image file not found: " + filePath);
        }
        return ImageIO.read(imageFile);
    }

    /**
     * Saves a processed image to the output directory with a timestamp.
     *
     * @param image The image to save
     * @param filterName The name of the filter applied
     * @return The path where the image was saved
     * @throws IOException if saving fails
     */
    public static String saveImage(BufferedImage image, String filterName) throws IOException {
        ensureOutputDirectoryExists();

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String fileName = String.format("%s_%s.%s", filterName.toLowerCase(), timestamp, DEFAULT_FORMAT);
        Path outputPath = Paths.get(OUTPUT_DIRECTORY, fileName);

        File outputFile = outputPath.toFile();
        ImageIO.write(image, DEFAULT_FORMAT, outputFile);

        return outputFile.getAbsolutePath();
    }

    /**
     * Saves an image to a specific file path.
     *
     * @param image The image to save
     * @param filePath The destination path
     * @throws IOException if saving fails
     */
    public static void saveImageToPath(BufferedImage image, String filePath) throws IOException {
        File outputFile = new File(filePath);
        String format = getFileExtension(filePath);

        if (format.isEmpty()) {
            format = DEFAULT_FORMAT;
        }

        ImageIO.write(image, format, outputFile);
    }

    /**
     * Ensures the output directory exists, creates it if necessary.
     */
    private static void ensureOutputDirectoryExists() throws IOException {
        Path outputDir = Paths.get(OUTPUT_DIRECTORY);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
    }

    /**
     * Extracts the file extension from a file path.
     *
     * @param filePath The file path
     * @return The file extension (without dot)
     */
    private static String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0) {
            return filePath.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Validates if the file is a supported image format.
     *
     * @param file The file to validate
     * @return true if supported, false otherwise
     */
    public static boolean isSupportedImageFormat(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
               name.endsWith(".png") || name.endsWith(".bmp") ||
               name.endsWith(".gif");
    }
}
