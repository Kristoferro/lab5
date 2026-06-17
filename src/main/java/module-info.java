module imageprocessor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    exports app to javafx.graphics;
    opens app to javafx.fxml;
}