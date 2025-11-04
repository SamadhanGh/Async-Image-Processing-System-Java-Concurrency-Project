package com.image.imageprocessing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the Async Image Processing System.
 * Demonstrates Java 21 concurrency features including Virtual Threads and StructuredTaskScope
 * combined with modern JavaFX UI for parallel image processing.
 *
 * @author Your Name
 * @version 2.0
 */
public class HelloApplication extends Application {

    /**
     * Starts the JavaFX application and loads the main UI.
     *
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            HelloApplication.class.getResource("/enhanced-image-processing-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Async Image Processing System - Java 21 | Virtual Threads | StructuredTaskScope");
        stage.setScene(scene);
        stage.setMinWidth(1400);
        stage.setMinHeight(800);
        stage.show();
    }

    /**
     * Application entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}