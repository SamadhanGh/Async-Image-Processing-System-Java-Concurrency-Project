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
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * Enhanced JavaFX controller for the image processing application.
 * Implements drag-and-drop, multiple filters, performance metrics, save functionality,
 * and a slow-motion tile-by-tile reveal animation for the processed image.
 */
public class EnhancedImageProcessingController {

    // ===== Overlay (processing mask) =====
    @FXML private StackPane processingOverlay;
    @FXML private Label processingLabel;
    @FXML private ProgressIndicator overlayLoader;

    // ===== Main UI =====
    @FXML private ImageView originalImageView;
    @FXML private ImageView processedImageView;
    @FXML private Button selectImageButton;
    @FXML private Button processImageButton;
    @FXML private Button saveImageButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Label metricsLabel;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TextArea logTextArea;
    @FXML private StackPane dragDropPane;
    @FXML private Label dragDropLabel;

    private BufferedImage currentImage;
    private BufferedImage processedImage;
    private AsyncImageProcessor asyncProcessor;
    private PerformanceMetrics metrics;

    // ===== Animation knobs (tweak as you like) =====
    private static final int REVEAL_TILE_SIZE = 1;     // pixels per tile in reveal
    private static final int REVEAL_DELAY_MS  = 9;     // delay between tiles

    /**
     * Initializes the controller and sets up UI components.
     */
    public void initialize() {
        asyncProcessor = new AsyncImageProcessor(50);
        metrics = new PerformanceMetrics();

        processImageButton.setDisable(true);
        saveImageButton.setDisable(true);
        progressIndicator.setVisible(false);
        progressBar.setVisible(false);

        // overlay off initially
        processingOverlay.setVisible(false);

        filterComboBox.getItems().addAll(
                "Grayscale",
                "Sepia",
                "Blur",
                "Sharpen",
                "Edge Detection",
                "Brightness (+50)",
                "Brightness (-50)",
                "Contrast (High)",
                "Contrast (Low)"
        );
        filterComboBox.setValue("Grayscale");

        setupDragAndDrop();
        loadDefaultImage();
        logMessage("Application initialized. Ready to process images.");
    }

    // =========================== Drag & Drop ===========================

    private void setupDragAndDrop() {
        dragDropPane.setOnDragOver(this::handleDragOver);
        dragDropPane.setOnDragDropped(this::handleDragDropped);
        dragDropPane.setOnDragExited(event ->
                dragDropPane.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-style: dashed;")
        );
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != dragDropPane && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            dragDropPane.setStyle("-fx-border-color: #27ae60; -fx-border-width: 3; -fx-border-style: dashed;");
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            File file = db.getFiles().getFirst();
            if (ImageIOUtil.isSupportedImageFormat(file)) {
                loadImageFromFile(file);
                success = true;
            } else {
                showError("Invalid File", "Please drop a valid image file (JPG, PNG, BMP, GIF).");
            }
        }

        event.setDropCompleted(success);
        event.consume();
        dragDropPane.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-style: dashed;");
    }

    // =========================== Loaders ===========================

    private void loadDefaultImage() {
        try (InputStream imageStream = getClass().getResourceAsStream("/test.jpg")) {
            if (imageStream != null) {
                Image image = new Image(imageStream);
                originalImageView.setImage(image);
                currentImage = SwingFXUtils.fromFXImage(image, null);
                processImageButton.setDisable(false);
                statusLabel.setText("Default image loaded. Select a filter and click Process.");
                dragDropLabel.setText("Default image loaded");
                logMessage("Default test image loaded successfully.");
            }
        } catch (Exception e) {
            logMessage("Could not load default image: " + e.getMessage());
        }
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        Stage stage = (Stage) selectImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            loadImageFromFile(selectedFile);
        }
    }

    private void loadImageFromFile(File file) {
        try {
            currentImage = ImageIOUtil.readImage(file.getAbsolutePath());

            if (currentImage == null) {
                showError("Failed to Load Image", "Unable to read the selected image file.");
                return;
            }

            Image fxImage = SwingFXUtils.toFXImage(currentImage, null);
            originalImageView.setImage(fxImage);
            processedImageView.setImage(null);
            processImageButton.setDisable(false);
            saveImageButton.setDisable(true);
            statusLabel.setText("Image loaded: " + file.getName());
            dragDropLabel.setText(file.getName());
            metricsLabel.setText("");
            logMessage("Image loaded: " + file.getName() +
                    " (Size: " + currentImage.getWidth() + "x" + currentImage.getHeight() + ")");
        } catch (Exception e) {
            showError("Error Loading Image", "An error occurred while loading the image: " + e.getMessage());
            logMessage("ERROR: Failed to load image - " + e.getMessage());
        }
    }

    // =========================== Processing ===========================

    @FXML
    private void handleProcessImage() {
        if (currentImage == null) {
            showError("No Image", "Please select an image first.");
            return;
        }

        String filterName = filterComboBox.getValue();
        processImageButton.setDisable(true);
        selectImageButton.setDisable(true);
        filterComboBox.setDisable(true);
        saveImageButton.setDisable(true);
        progressIndicator.setVisible(true);
        progressBar.setVisible(true);
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        statusLabel.setText("Processing image with " + filterName + " filter...");
        processedImageView.setImage(null);
        logMessage("Started processing with " + filterName + " filter...");

        // Show overlay
        processingOverlay.setVisible(true);
        processingLabel.setText("Processing " + filterName + "...");
        overlayLoader.setVisible(true);

        metrics.reset();
        metrics.setFilterName(filterName);
        metrics.captureMemoryUsage();

        ImageFilter selectedFilter = getSelectedFilter(filterName);

        Thread.ofVirtual().start(() -> {
            try {
                processedImage = asyncProcessor.processWithStructuredConcurrency(
                        currentImage,
                        selectedFilter,
                        metrics
                );

                metrics.captureMemoryUsage();

                // hide overlay, then animate reveal
                Platform.runLater(() -> {
                    processingOverlay.setVisible(false);
                    progressIndicator.setVisible(false);
                    progressBar.setVisible(false);

                    // 🔥 slow-motion tile-by-tile reveal (no FXML changes needed)
                    animateSlowMotionReveal(processedImage, () -> {
                        // enable UI after animation completes
                        processImageButton.setDisable(false);
                        selectImageButton.setDisable(false);
                        filterComboBox.setDisable(false);
                        saveImageButton.setDisable(false);
                        statusLabel.setText("Processing completed successfully!");
                        metricsLabel.setText(metrics.toShortString());
                        logMessage("Processing completed: " + metrics.toShortString());
                        logMessage(metrics.toString());
                    });
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    processingOverlay.setVisible(false);
                    progressIndicator.setVisible(false);
                    progressBar.setVisible(false);
                    processImageButton.setDisable(false);
                    selectImageButton.setDisable(false);
                    filterComboBox.setDisable(false);
                    statusLabel.setText("Error processing image.");
                    showError("Processing Error", "An error occurred during processing: " + e.getMessage());
                    logMessage("ERROR: Processing failed - " + e.getMessage());
                });
            }
        });
    }
    @FXML
    private void handleSaveImage() {
        if (processedImage == null) {
            showError("No Processed Image", "Please process an image first.");
            return;
        }

        try {
            String savedPath = ImageIOUtil.saveImage(processedImage, filterComboBox.getValue());
            showInfo("Image Saved", "Image saved successfully to:\n" + savedPath);
            logMessage("Image saved to: " + savedPath);
        } catch (Exception e) {
            showError("Save Error", "Failed to save image: " + e.getMessage());
            logMessage("ERROR: Failed to save image - " + e.getMessage());
        }
    }

    /**
     * Slow-motion reveal of the processed image:
     * copies tiles from the processed BufferedImage into a partial image and updates the ImageView per tile.
     * @param img processed image (final)
     * @param onDone runs on FX thread after animation completes (enable UI, update labels, etc.)
     */
    private void animateSlowMotionReveal(BufferedImage img, Runnable onDone) {
        // Use ARGB buffer to avoid type issues while revealing
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int tile = Math.max(8, REVEAL_TILE_SIZE); // safety lower bound

        new Thread(() -> {
            try {
                BufferedImage partial = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

                // reveal row-by-row tiles for a smooth motion feel
                for (int y = 0; y < h; y += tile) {
                    for (int x = 0; x < w; x += tile) {

                        // copy one tile
                        for (int yy = y; yy < y + tile && yy < h; yy++) {
                            for (int xx = x; xx < x + tile && xx < w; xx++) {
                                partial.setRGB(xx, yy, img.getRGB(xx, yy));
                            }
                        }

                        // push this frame to UI
                        Image fxFrame = SwingFXUtils.toFXImage(partial, null);
                        Platform.runLater(() -> processedImageView.setImage(fxFrame));

                        // pacing for slow-motion feel
                        try { Thread.sleep(REVEAL_DELAY_MS); } catch (InterruptedException ignored) {}
                    }
                }

                // final render with exact processed image (ensures no artifacts)
                Platform.runLater(() -> {
                    processedImageView.setImage(SwingFXUtils.toFXImage(img, null));
                    if (onDone != null) onDone.run();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Animation Error", ex.getMessage()));
            }
        }, "reveal-animation-thread").start();
    }

    // =========================== Filters ===========================

    private ImageFilter getSelectedFilter(String filterName) {
        return switch (filterName) {
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

    // =========================== Utils ===========================

    private void logMessage(String message) {
        Platform.runLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
            );
            logTextArea.appendText("[" + timestamp + "] " + message + "\n");
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleShareImage() {
        if (processedImage == null) {
            showError("No Processed Image", "Please process an image before sharing.");
            return;
        }

        // Convert BufferedImage → JavaFX Image
        Image fxImage = SwingFXUtils.toFXImage(processedImage, null);

        // Copy to clipboard
        ClipboardContent content = new ClipboardContent();
        content.putImage(fxImage);
        Clipboard.getSystemClipboard().setContent(content);

        // Status + log + toast
        statusLabel.setText("📋 Image copied! Paste it anywhere (Ctrl + V).");
        logMessage("Image copied to clipboard for sharing.");
        showToast("✅ Image copied to clipboard!");
    }

    // =========================== Toast Notification ===========================

    private void showToast(String message) {
        Platform.runLater(() -> {
            Label toast = new Label(message);
            toast.setStyle("-fx-background-color: rgba(0,0,0,0.75); "
                    + "-fx-text-fill: white; "
                    + "-fx-padding: 10 20; "
                    + "-fx-background-radius: 20; "
                    + "-fx-font-size: 13px;");
            toast.setOpacity(0);

            // Place toast in the center of your root stack (processingOverlay's parent)
            StackPane root = (StackPane) processingOverlay.getParent();
            root.getChildren().add(toast);
            StackPane.setAlignment(toast, javafx.geometry.Pos.BOTTOM_CENTER);

            // Fade in
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(400), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1.0);

            // Fade out
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(800), toast);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0);
            fadeOut.setDelay(javafx.util.Duration.seconds(2));

            fadeOut.setOnFinished(e -> root.getChildren().remove(toast));

            fadeIn.play();
            fadeIn.setOnFinished(e -> fadeOut.play());
        });
    }

}
