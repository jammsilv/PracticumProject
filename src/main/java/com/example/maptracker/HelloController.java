package com.example.maptracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HelloController {

    @FXML
    private AnchorPane anchor_window;
    @FXML
    private VBox scroll_pane_vbox;
    @FXML
    private Label welcomeText;
    @FXML
    private Label edit_status;
    @FXML
    private ImageView map_image;
    @FXML
    private Button enable_edit;
    private MapData data = new MapData();
    private String saveName;
    private ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
    private FileChooser fileChoice = new FileChooser();
    private Stage stage;
    private boolean editEnabled = false;

    //Add a method for switching which map is being displayed
    //Add a method for updating the map when it is switched,
    // mainly to not have notes/doors that don't exist on the new map
    //Add a method for when the save button is clicked
    //Figure out what the method for saving everything is, most likely through making a directory
    //Will do further research into using serialization/deserialization for saving information


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void onNewMapButtonClick() throws IOException {
        // String project_name = "";
        // Create a dialogue window that asks for an image and a name for the map
        this.map_image = new ImageView();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("map-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = new Stage();
        stage.setTitle("Map");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void enableEdit() {
        editEnabled = !editEnabled;
        if (editEnabled) {
            edit_status.setText("Edit Enabled");
            edit_status.setTextFill(Paint.valueOf("Green"));
        } else {
            edit_status.setText("Edit Disabled");
            edit_status.setTextFill(Paint.valueOf("Red"));
        }
    }

    protected void warningWindow(String s) {
        Dialog<String> warningWindow = new Dialog();
        warningWindow.setContentText("Warning! " + s);
        warningWindow.getDialogPane().getButtonTypes().add(ok);
        warningWindow.show();
    }
    @FXML
    protected void uploadImage() {
        File file = fileChoice.showOpenDialog(stage);
        try {
            data.setImage(ImageIO.read(file));
        } catch (IOException e) {
            System.out.println("IMAGE READ ERROR!");
        }
        map_image.setImage(SwingFXUtils.toFXImage(data.getImage(), null));
    }

    protected MapData openProject() { //Finish implementation
        MapData temp = new MapData();

        return temp;
    }
    @FXML
    protected void saveProject() { // Finish implementation
        Dialog<String> saveWindow = new Dialog();
        saveWindow.setContentText("Test dialog for saving");
        saveWindow.getDialogPane().getButtonTypes().add(ok);
        saveWindow.showAndWait();
    }

    protected boolean closeProject() { // Finish implementation
        return false;
    }

    @FXML
    protected void addMapNote(MouseEvent event) {
        if (editEnabled) {
            Point p = new Point((int) event.getX(), (int) event.getY());
            TextInputDialog tid = new TextInputDialog();
            tid.setTitle("Add Note");
            tid.setContentText("Type the content of the note below.");
            tid.showAndWait();
            if (tid.getResult() == null) {
            } else {
                String note = tid.getEditor().getText();
                data.addNote(p, note);
                Button b = new Button();
                b.setLayoutX((int) event.getX() + 7);
                b.setLayoutY((int) event.getY() + 36);
                String x = Integer.toString((int) event.getX());
                String y = Integer.toString((int) event.getY());
                b.setId(x + "_" + y);
                anchor_window.getChildren().add(b);
                Label l = new Label(note);
                scroll_pane_vbox.getChildren().add(l);
            }
        }
    }
}