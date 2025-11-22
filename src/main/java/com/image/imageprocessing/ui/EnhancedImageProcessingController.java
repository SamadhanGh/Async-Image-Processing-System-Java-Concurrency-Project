package com.image.imageprocessing.ui;

import com.image.imageprocessing.concurrency.AsyncImageProcessor;
import com.image.imageprocessing.filter.*;
import com.image.imageprocessing.utils.ImageIOUtil;
import com.image.imageprocessing.utils.PerformanceMetrics;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public class EnhancedImageProcessingController {

    @FXML private ImageView originalImageView;
    @FXML private ImageView processedImageView;
    @FXML private Button processImageButton;
    @FXML private Button selectImageButton;
    @FXML private Button saveImageButton;
    @FXML private Button shareImageButton;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Label statusLabel;
    @FXML private Label metricsLabel;
    @FXML private TextArea logTextArea;
    @FXML private ProgressIndicator progressIndicator;

    private BufferedImage currentImage;
    private BufferedImage liveRenderingImage;

    private AsyncImageProcessor asyncProcessor;
    private PerformanceMetrics metrics;

    /** Initialization */
    public void initialize() {
        asyncProcessor = new AsyncImageProcessor(50);
        metrics = new PerformanceMetrics();

        filterComboBox.getItems().addAll(
                "Grayscale", "Sepia", "Blur", "Sharpen", "Edge Detection",
                "Brightness (+50)", "Brightness (-50)", "Contrast (High)", "Contrast (Low)"
        );
        filterComboBox.setValue("Grayscale");

        loadDefaultImage();
        log("✔ Application initialized.");
    }

    /** Required by FXML */
    @FXML
    private void selectImage() { handleSelectImage(); }

    /** File chooser loader */
    @FXML
    private void handleSelectImage() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Image");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
            );

            Stage stage = (Stage) selectImageButton.getScene().getWindow();
            File file = chooser.showOpenDialog(stage);

            if (file != null) {
                currentImage = ImageIOUtil.readImage(file.getAbsolutePath());
                originalImageView.setImage(SwingFXUtils.toFXImage(currentImage, null));
                processedImageView.setImage(null);
                metricsLabel.setText("Ready");
                statusLabel.setText("Loaded: " + file.getName());
                log("📁 Image loaded: " + file.getName());
            }

        } catch (Exception e) {
            statusLabel.setText("❌ Error loading image");
            log("Error: " + e.getMessage());
        }
    }

    /** Process image + slow reveal animation */
    @FXML
    private void handleProcessImage() {
        if (currentImage == null) {
            statusLabel.setText("⚠ Please select an image first.");
            return;
        }

        ImageFilter filter = getSelectedFilter(filterComboBox.getValue());
        statusLabel.setText("Processing...");
        progressIndicator.setVisible(true);

        liveRenderingImage = new BufferedImage(
                currentImage.getWidth(),
                currentImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        processedImageView.setImage(SwingFXUtils.toFXImage(liveRenderingImage, null));

        // Store tile operations for later playback
        List<Runnable> revealQueue = Collections.synchronizedList(new ArrayList<>());

        Thread.ofVirtual().start(() -> {
            try {
                BufferedImage finalImage = asyncProcessor.processWithStructuredConcurrency(
                        currentImage,
                        filter,
                        metrics,
                        (tile, x, y) -> revealQueue.add(() -> revealTile(tile, x, y))
                );

                Platform.runLater(() -> startRevealAnimation(revealQueue, finalImage));

            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    statusLabel.setText("❌ Processing failed");
                    log("ERROR: " + e.getMessage());
                });
            }
        });
    }

    /** Smooth sequential reveal instead of instant update */
    private void startRevealAnimation(List<Runnable> revealQueue, BufferedImage finalImage) {
        statusLabel.setText("🎬 Revealing tiles...");
        progressIndicator.setVisible(false);


        final int delay = 40;

        new Thread(() -> {
            for (Runnable tileAction : revealQueue) {
                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
                Platform.runLater(tileAction);
            }

            Platform.runLater(() -> {
                processedImageView.setImage(SwingFXUtils.toFXImage(finalImage, null));
                statusLabel.setText("✔ Showcase Complete!");
                metricsLabel.setText(metrics.toShortString());
                log("🎉 Slow reveal finished.");
            });

        }, "Showcase-Reveal-Thread").start();
    }

    /** Tile drawing */
    private void revealTile(BufferedImage tile, int x, int y) {
        for (int yy = 0; yy < tile.getHeight(); yy++)
            for (int xx = 0; xx < tile.getWidth(); xx++)
                liveRenderingImage.setRGB(x + xx, y + yy, tile.getRGB(xx, yy));

        processedImageView.setImage(SwingFXUtils.toFXImage(liveRenderingImage, null));
    }

    /** Save */
    @FXML
    private void handleSaveImage() {
        if (liveRenderingImage == null) {
            statusLabel.setText("⚠ No image to save.");
            return;
        }
        try {
            String path = ImageIOUtil.saveImage(liveRenderingImage, filterComboBox.getValue());
            statusLabel.setText("💾 Saved: " + path);
            log("Saved: " + path);
        } catch (Exception e) {
            statusLabel.setText("❌ Save failed.");
            log("Save error: " + e.getMessage());
        }
    }

    /** Copy to clipboard */
    @FXML
    private void handleShareImage() {
        if (liveRenderingImage == null) return;

        ClipboardContent content = new ClipboardContent();
        content.putImage(SwingFXUtils.toFXImage(liveRenderingImage, null));
        Clipboard.getSystemClipboard().setContent(content);

        statusLabel.setText("📋 Copied to clipboard!");
        log("Copied to clipboard");
    }

    /** Default image */
    private void loadDefaultImage() {
        try (InputStream stream = getClass().getResourceAsStream("/test.jpg")) {
            if (stream != null) {
                Image fxImage = new Image(stream);
                currentImage = SwingFXUtils.fromFXImage(fxImage, null);
                originalImageView.setImage(fxImage);
            }
        } catch (Exception ignored) {}
    }

    /** Filter switch */
    private ImageFilter getSelectedFilter(String f) {
        return switch (f) {
            case "Sepia" -> new SepiaFilter();
            case "Blur" -> new BlurFilter();
            case "Sharpen" -> new SharpenFilter();
            case "Edge Detection" -> new EdgeDetectionFilter();
            case "Brightness (+50)" -> new BrightnessFilter(50);
            case "Brightness (-50)" -> new BrightnessFilter(-50);
            case "Contrast (High)" -> new ContrastFilter(1.5);
            case "Contrast (Low)" -> new ContrastFilter(0.5);
            default -> new GreyScaleFilter();
        };
    }

    private void log(String msg) {
        Platform.runLater(() -> logTextArea.appendText(msg + "\n"));
    }
}
