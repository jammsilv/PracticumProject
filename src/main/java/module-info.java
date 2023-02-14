module com.example.maptracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.swing;

    opens com.example.maptracker to javafx.fxml;
    exports com.example.maptracker;
}