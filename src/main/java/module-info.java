module com.image.imageprocessing {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens com.image.imageprocessing to javafx.fxml;
    opens com.image.imageprocessing.ui to javafx.fxml;
    exports com.image.imageprocessing;
    exports com.image.imageprocessing.ui;
    exports com.image.imageprocessing.image;
    exports com.image.imageprocessing.filter;
    exports com.image.imageprocessing.processor;
}