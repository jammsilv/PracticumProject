package com.example.maptracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
//hi github

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
    private ChoiceBox color_choice_box;
    private MapData data = new MapData();
    private String saveName;
    private final ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private final ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    private final FileChooser fileChoice = new FileChooser();
    private Stage stage;
    private boolean editEnabled = false;
    private boolean deleteEnabled = false;
    private final String[] colors = {"Black", "White", "Gray", "Red", "Blue",
                                "Yellow", "Green", "Blue", "Purple", "Pink"};

    //Add a method for switching which map is being displayed
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
        if (data.getImage() != null) {
            editEnabled = !editEnabled;
            if (editEnabled) {
                deleteEnabled = false;
                edit_status.setText("Edit Enabled");
                edit_status.setTextFill(Paint.valueOf("Green"));
            } else {
                edit_status.setText("Edit Disabled");
                edit_status.setTextFill(Paint.valueOf("Red"));
            }
        }
    }

    @FXML
    protected void enableDeletion() {
        deleteEnabled = !deleteEnabled;
        if (deleteEnabled) {
            editEnabled = false;
            edit_status.setText("DELETE ENABLED");
            edit_status.setTextFill(Paint.valueOf("Red"));
        } else {
            edit_status.setText("Edit Disabled");
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
        if (file != null) {
            try {
                data.setImage(ImageIO.read(file));
            } catch (IOException e) {
                System.out.println("IMAGE READ ERROR!");
            }
            map_image.setImage(SwingFXUtils.toFXImage(data.getImage(), null));
            for (String color : colors) {
                color_choice_box.getItems().add(color);
            }
        }
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
            TextInputDialog tid1 = new TextInputDialog();
            tid1.setTitle("Add Note");
            tid1.setContentText("Type what the title of the note will be. A default will be given if left blank");
            tid1.showAndWait();
            if (tid1.getResult() != null) {
                String title;
                title = tid1.getEditor().getText();
                if (title.equals("")) {
                    int q = data.getNoteMapSize() + 1;
                    title = "Note " + q;
                }
                TextInputDialog tid = new TextInputDialog();
                tid.setTitle("Add Note");
                tid.setContentText("Type the content of the note below.");
                tid.showAndWait();
                if (tid.getResult() != null) {
                    String note = tid.getEditor().getText();
                    if (note.equals("")) {
                        warningWindow("No text was entered into the Content Window.");
                    } else {
                        data.addNote(p, note, title);
                        Label b = new Label();
                        b.setLayoutX((int) event.getX() + 7);
                        b.setLayoutY((int) event.getY() + 36);
                        b.setTextFill(Paint.valueOf(color_choice_box.getValue().toString()));
                        b.setText("☆");
                        b.setFont(new Font("Arial", 20));
                        anchor_window.getChildren().add(b);
                        Label l = new Label(title);
                        l.setId(title);
                        l.setOnMouseClicked(e -> {
                            try {
                                ContentDialog.display(data.getNoteTitle(p), data.getNoteContent(p));
                            } catch (IOException f) {

                            }
                        });
                        b.setOnMouseClicked(e -> {
                            if (deleteEnabled) {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setContentText("Are you sure you want to delete this element?");
                                alert.setTitle("Delete Note");
                                alert.getButtonTypes().setAll(yes, cancel);
                                alert.showAndWait().ifPresent(type -> {
                                    if (type == yes) {
                                        scroll_pane_vbox.getChildren().remove(l);
                                        data.removeNote(p);
                                        anchor_window.getChildren().remove(b);
                                        deleteEnabled = false;
                                        edit_status.setText("Edit Disabled");
                                    }
                                });
                            }
                        });
                        scroll_pane_vbox.getChildren().add(l);
                    } // End of Note Addition
                } // End of Content cancel check
            } // End of Title cancel check
        } // End of editEnabled check
    } // End of addMapNote()
}