package com.image.imageprocessing.ui;

import com.image.imageprocessing.filter.BlurFilter;
import com.image.imageprocessing.filter.GreyScaleFilter;
import com.image.imageprocessing.filter.ImageFilter;
import com.image.imageprocessing.filter.SepiaFilter;
import com.image.imageprocessing.io.FileImageIO;
import com.image.imageprocessing.processor.ImageProcessor;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class ImageProcessingController {

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView processedImageView;

    @FXML
    private Button selectImageButton;

    @FXML
    private Button processImageButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> filterComboBox;

    private BufferedImage currentImage;
    private ImageProcessor imageProcessor;
    private FileImageIO imageIO;

    public void initialize() {
        imageProcessor = new ImageProcessor();
        imageIO = new FileImageIO();

        processImageButton.setDisable(true);
        progressIndicator.setVisible(false);

        filterComboBox.getItems().addAll("Grayscale", "Sepia", "Blur");
        filterComboBox.setValue("Grayscale");

        loadDefaultImage();
    }

    private void loadDefaultImage() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/test.jpg");
            if (imageStream != null) {
                Image image = new Image(imageStream);
                originalImageView.setImage(image);
                currentImage = SwingFXUtils.fromFXImage(image, null);
                processImageButton.setDisable(false);
                statusLabel.setText("Default image loaded. Ready to process.");
            } else {
                statusLabel.setText("No default image found. Please select an image.");
            }
        } catch (Exception e) {
            statusLabel.setText("Could not load default image. Please select one.");
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
            try {
                currentImage = imageIO.readImage(selectedFile.getAbsolutePath());

                if (currentImage == null) {
                    showError("Failed to Load Image", "Unable to read the selected image file.");
                    return;
                }

                Image fxImage = SwingFXUtils.toFXImage(currentImage, null);
                originalImageView.setImage(fxImage);
                processedImageView.setImage(null);
                processImageButton.setDisable(false);
                statusLabel.setText("Image loaded: " + selectedFile.getName());
            } catch (Exception e) {
                showError("Error Loading Image", "An error occurred while loading the image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleProcessImage() {
        if (currentImage == null) {
            showError("No Image", "Please select an image first.");
            return;
        }

        processImageButton.setDisable(true);
        selectImageButton.setDisable(true);
        filterComboBox.setDisable(true);
        progressIndicator.setVisible(true);
        statusLabel.setText("Processing image...");
        processedImageView.setImage(null);

        ImageFilter selectedFilter = getSelectedFilter();

        Thread.ofVirtual().start(() -> {
            try {
                BufferedImage processedImage = imageProcessor.processImageWithFilter(
                    currentImage,
                    selectedFilter
                );

                Platform.runLater(() -> {
                    Image fxImage = SwingFXUtils.toFXImage(processedImage, null);
                    processedImageView.setImage(fxImage);
                    progressIndicator.setVisible(false);
                    processImageButton.setDisable(false);
                    selectImageButton.setDisable(false);
                    filterComboBox.setDisable(false);
                    statusLabel.setText("Image processing completed successfully!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    processImageButton.setDisable(false);
                    selectImageButton.setDisable(false);
                    filterComboBox.setDisable(false);
                    statusLabel.setText("Error processing image.");
                    showError("Processing Error", "An error occurred during processing: " + e.getMessage());
                });
            }
        });
    }

    private ImageFilter getSelectedFilter() {
        String selectedFilterName = filterComboBox.getValue();
        return switch (selectedFilterName) {
            case "Sepia" -> new SepiaFilter();
            case "Blur" -> new BlurFilter();
            default -> new GreyScaleFilter();
        };
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
