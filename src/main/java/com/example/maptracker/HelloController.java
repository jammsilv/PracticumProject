package com.example.maptracker;

import javafx.application.Platform;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Button;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarFile;
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
    private File mainFile;
    private boolean editEnabled = false;
    private boolean saved = true;
    private boolean deleteEnabled = false;
    private final String[] colors = {"Black", "White", "Gray", "Red", "Blue",
                                "Yellow", "Green", "Blue", "Purple", "Pink"};
    private ArrayList<Label> note_markers = new ArrayList<Label>();
    private ArrayList<Label> note_list = new ArrayList<Label>();

// CURRENT TO DO LIST
    // Implement Saving/Loading from any directory, not a preset one [ DONE ]
        // Make Opening a Project clear the current Project
        // Make a "MAIN FILE" that lets you hit "Save" without pulling up the dialog each time
    // Implement Editing/Appending Notes
    // Edit how notes are displayed, making a new label below the current one that displays all the necessary information
         // Also add deletion/appending notes to this new method of display

    @FXML
    protected void onNewMapButtonClick() throws IOException {
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
            saved = false;
        }
    }

    @FXML
    protected void openProject() { //Finish implementation
        boolean contin = false;
        if (saved == false) {
            Dialog<ButtonType> saveWarning = new Dialog();
            saveWarning.setContentText("Warning! You are about to open a new project. Any data that is unsaved will be lost. Do you wish to continue?");
            saveWarning.getDialogPane().getButtonTypes().add(yes);
            saveWarning.getDialogPane().getButtonTypes().add(cancel); // Maybe add "Save and Continue"?
            Optional<ButtonType> bt = saveWarning.showAndWait();
            if (bt.get() == cancel) { // figure out how to check if the operation was cancelled
                contin = false;
            } else if (bt.get() == yes){
                contin = true;
            }
        } else {
            contin = true;
        }
            if (contin) {
                File selection = fileChoice.showOpenDialog(stage);
                if (selection != null) {
                    try {
                        FileInputStream fileIn = new FileInputStream(selection);
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        if (data != null) {
                            ArrayList<Point> temp_points = new ArrayList<>();
                            for (Map.Entry<Point, String> entry : data.getNoteContentList().entrySet()) {
                                temp_points.add(entry.getKey());
                            }
                            for (Point p : temp_points) {
                                data.removeNote(p);
                            }
                            for (Label temp : note_list) {
                                scroll_pane_vbox.getChildren().remove(temp);
                            }
                            note_list.clear();
                            for (Label temp : note_markers) {
                                anchor_window.getChildren().remove(temp);
                            }
                            note_markers.clear();
                        }
                        data = (MapData) in.readObject();
                        data.setImage(ImageIO.read(in));
                        map_image.setImage(SwingFXUtils.toFXImage(data.getImage(), null));
                        for (Map.Entry<Point, String> entry : data.getNoteContentList().entrySet()) {
                            int i = 0;
                            Label l = new Label();
                            l.setText("☆");
                            l.setId(Integer.toString(i));
                            l.setLayoutX(entry.getKey().getX() + 7);
                            l.setLayoutY(entry.getKey().getY() + 36);
                            l.setFont(new Font("Arial", 20));
                            note_markers.add(l);
                            anchor_window.getChildren().add(l);
                            Label l1 = new Label(data.getNoteTitle(entry.getKey()));
                            l1.setId(Integer.toString(i));
                            l1.setOnMouseClicked(e -> {
                                try {
                                    ContentDialog.display(data.getNoteTitle(entry.getKey()), data.getNoteContent(entry.getKey()));
                                } catch (IOException f) {

                                }
                            });
                            l.setOnMouseClicked(e -> {
                                if (deleteEnabled) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setContentText("Are you sure you want to delete this element?");
                                    alert.setTitle("Delete Note");
                                    alert.getButtonTypes().setAll(yes, cancel);
                                    alert.showAndWait().ifPresent(type -> {
                                        if (type == yes) {
                                            scroll_pane_vbox.getChildren().remove(l1);
                                            data.removeNote(entry.getKey());
                                            anchor_window.getChildren().remove(l);
                                            deleteEnabled = false;
                                            edit_status.setText("Edit Disabled");
                                        }
                                    });
                                }
                            });
                            note_list.add(l1);
                            scroll_pane_vbox.getChildren().add(l1);
                        }
                        for (String color : colors) {
                            color_choice_box.getItems().add(color);
                        }
                        in.close();
                        fileIn.close();
                    } catch (IOException i) {
                        i.printStackTrace();
                        return;
                    } catch (ClassNotFoundException c) {
                        System.out.println("Employee class not found");
                        c.printStackTrace();
                        return;
                    }
                    mainFile = selection;
                } else {
                    warningWindow("No File was Selected!");
                }
            }
    }
    @FXML
    protected void saveProject() { // Finish implementation
        File selection = fileChoice.showSaveDialog(stage);
        try {
            FileOutputStream fileOut = new FileOutputStream(selection);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            ImageIO.write(data.getImage(), "png", out);
            out.close();
            fileOut.close();
            mainFile = selection;
            saved = true;
        } catch (IOException i) {
            i.printStackTrace();
        }
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
                        b.setId(Integer.toString(data.getNoteMapSize() - 1));
                        b.setFont(new Font("Arial", 20));
                        note_markers.add(b);
                        anchor_window.getChildren().add(b);
                        Label l = new Label(title);
                        l.setId(Integer.toString(data.getNoteMapSize() - 1));
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
                        note_list.add(l);
                        scroll_pane_vbox.getChildren().add(l);
                        saved = false;
                    } // End of Note Addition
                } // End of Content cancel check
            } // End of Title cancel check
        } // End of editEnabled check
    } // End of addMapNote()
}