package com.example.maptracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ContentDialog {
    public static void display(String title, String content) throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Label content_label = new Label(content);
        Label title_label = new Label(title);
        VBox window = new VBox(title_label, content_label);
        window.setAlignment(Pos.CENTER);
//        title_label.setLayoutX(150);
//        title_label.setLayoutY(50);
        title_label.setPrefSize(250,200);
        title_label.setFont(new Font("Arial", 16));
        title_label.setPadding(new Insets(10));
//        content_label.setLayoutX(25);
//        content_label.setLayoutY(100);
        content_label.setPrefSize(300, 275);
        content_label.setMaxSize(900, 1200);
        content_label.setFont(new Font("Arial", 14));
        content_label.setPadding(new Insets(10));
        content_label.setWrapText(true);
        Scene scene = new Scene(window, 350, 400);
        stage.setTitle("Content Window");
        stage.setScene(scene);
        stage.show();
    }
}
