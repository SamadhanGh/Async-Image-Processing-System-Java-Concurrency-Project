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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * Enhanced JavaFX controller for the image processing application.
 * Implements drag-and-drop, multiple filters, performance metrics, and save functionality.
 */
public class EnhancedImageProcessingController {

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView processedImageView;

    @FXML
    private Button selectImageButton;

    @FXML
    private Button processImageButton;

    @FXML
    private Button saveImageButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label metricsLabel;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextArea logTextArea;

    @FXML
    private StackPane dragDropPane;

    @FXML
    private Label dragDropLabel;

    private BufferedImage currentImage;
    private BufferedImage processedImage;
    private AsyncImageProcessor asyncProcessor;
    private PerformanceMetrics metrics;

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

    /**
     * Sets up drag-and-drop functionality for the image area.
     */
    private void setupDragAndDrop() {
        dragDropPane.setOnDragOver(this::handleDragOver);
        dragDropPane.setOnDragDropped(this::handleDragDropped);
        dragDropPane.setOnDragExited(event -> {
            dragDropPane.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-style: dashed;");
        });
    }

    /**
     * Handles drag over events for drag-and-drop.
     */
    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != dragDropPane && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            dragDropPane.setStyle("-fx-border-color: #27ae60; -fx-border-width: 3; -fx-border-style: dashed;");
        }
        event.consume();
    }

    /**
     * Handles dropped files for drag-and-drop.
     */
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
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

    /**
     * Loads the default test image from resources.
     */
    private void loadDefaultImage() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/test.jpg");
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

    /**
     * Handles the select image button click.
     */
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

    /**
     * Loads an image from a file.
     */
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

    /**
     * Handles the process image button click.
     */
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

                Platform.runLater(() -> {
                    Image fxImage = SwingFXUtils.toFXImage(processedImage, null);
                    processedImageView.setImage(fxImage);
                    progressIndicator.setVisible(false);
                    progressBar.setVisible(false);
                    processImageButton.setDisable(false);
                    selectImageButton.setDisable(false);
                    filterComboBox.setDisable(false);
                    saveImageButton.setDisable(false);
                    statusLabel.setText("Processing completed successfully!");
                    metricsLabel.setText(metrics.toShortString());
                    logMessage("Processing completed: " + metrics.toShortString());
                    logMessage(metrics.toString());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
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

    /**
     * Handles the save image button click.
     */
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
     * Returns the appropriate filter based on the selected filter name.
     */
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

    /**
     * Logs a message to the log text area with timestamp.
     */
    private void logMessage(String message) {
        Platform.runLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
            );
            logTextArea.appendText("[" + timestamp + "] " + message + "\n");
        });
    }

    /**
     * Shows an error alert dialog.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an information alert dialog.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
