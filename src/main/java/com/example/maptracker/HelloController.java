package com.example.maptracker;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;


public class HelloController {

    @FXML
    private AnchorPane anchor_window;
    @FXML
    private VBox scroll_pane_vbox;
    @FXML
    private Label edit_status;
    @FXML
    private ImageView map_image;
    @FXML
    private ChoiceBox color_choice_box;
    // CONTENT WINDOW  V
    @FXML
    private Button delete_note_button;
    @FXML
    private Button edit_note_button;
    @FXML
    private Label note_title_label;
    @FXML
    private Label note_content_label;
    // CONTENT WINDOW ^
    private MapData data = new MapData();
    private final ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private final ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    private final ButtonType current_note_title = new ButtonType("Title");
    private final ButtonType current_note_content = new ButtonType("Content");
    private final ButtonType current_note_both = new ButtonType("Both Title and Content");
    private final FileChooser fileChoice = new FileChooser();
    private Stage stage;
    private File mainFile = null;
    private String current_file_name = "New Map";
    private boolean editEnabled = false;
    private boolean saved = true;
    private final String[] colors = {"Black", "White", "Gray", "Red", "Blue",
                                "Yellow", "Green", "Blue", "Purple", "Pink"};
    private ArrayList<Label> note_markers = new ArrayList<>();
    private ArrayList<Label> note_list = new ArrayList<>();
    private EventHandler handleClick;

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
                edit_status.setText("Edit Enabled");
                edit_status.setTextFill(Paint.valueOf("Green"));
            } else {
                edit_status.setText("Edit Disabled");
                edit_status.setTextFill(Paint.valueOf("Red"));
            }
        }
    }

    protected void warningWindow(String s) {
        Dialog<String> warningWindow = new Dialog<>();
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
        if (!saved) {
            Dialog<ButtonType> saveWarning = new Dialog<>();
            saveWarning.setContentText("Warning! You are about to open a new project. Any data that is unsaved will be lost. Do you wish to continue?");
            saveWarning.getDialogPane().getButtonTypes().add(yes);
            saveWarning.getDialogPane().getButtonTypes().add(cancel); // Maybe add "Save and Continue"?
            Optional<ButtonType> bt = saveWarning.showAndWait();
            if (bt.isPresent()) {
                if (bt.get() == cancel) { // figure out how to check if the operation was cancelled
                    contin = false;
                } else if (bt.get() == yes) {
                    contin = true;
                }
            } else {
                contin = false;
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
                            mainFile = selection;
                            current_file_name = selection.getName();
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
                            l.setTooltip(new Tooltip("Title: " + data.getNoteTitle(entry.getKey()) + "\n"
                                    + "Content: " + data.getNoteContent(entry.getKey())));
                            note_markers.add(l);
                            anchor_window.getChildren().add(l);
                            Label l1 = new Label(data.getNoteTitle(entry.getKey()));
                            l1.setId(Integer.toString(i));
                            String fTitle = data.getNoteTitle(entry.getKey());
                            String fNote = data.getNoteContent(entry.getKey());
                            handleClick = (EventHandler<MouseEvent>) mouseEvent -> sideWindowLoader(fTitle, fNote, entry.getKey(), l1, l);
                            l.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            l1.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
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
                }
            }
    }
    @FXML
    protected void saveProject() {
        if (data.getImage() != null) {
            File selection;
            if (mainFile == null) {
                selection = fileChoice.showSaveDialog(stage);
                if (selection != null) {
                    mainFile = selection;
                }
            } else {
                selection = mainFile;
            }
            if (selection != null) {
                try {
                    FileOutputStream fileOut = new FileOutputStream(selection);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(data);
                    ImageIO.write(data.getImage(), "png", out);
                    out.close();
                    fileOut.close();
                    mainFile = selection;
                    saved = true;
                    Dialog saved = new Dialog();
                    saved.setTitle("Saved");
                    saved.setContentText("Saved to file '" + selection.getName() + "'.");
                    saved.getDialogPane().getButtonTypes().add(ok);
                    saved.showAndWait();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        }
    }

    @FXML
    protected void saveAsButton() {
        if (data.getImage() != null) {
            File selection = fileChoice.showSaveDialog(stage);
            if (selection != null) {
                try {
                    FileOutputStream fileOut = new FileOutputStream(selection);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(data);
                    ImageIO.write(data.getImage(), "png", out);
                    out.close();
                    fileOut.close();
                    mainFile = selection;
                    saved = true;
                    Dialog saved = new Dialog();
                    saved.setTitle("Saved");
                    saved.setContentText("Saved to file '" + selection.getName() + "'.");
                    saved.getDialogPane().getButtonTypes().add(ok);
                    saved.showAndWait();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        }
    }

    protected void sideWindowLoader(String title, String note, Point p, Label l, Label b) {
        note_title_label.setText(title);
        note_content_label.setText(note);
        edit_note_button.setOnAction(f -> {
            Dialog<ButtonType> editWindow = new Dialog<>();
            editWindow.setTitle("Edit Note");
            editWindow.setContentText("What do you want to edit?");
            editWindow.getDialogPane().getButtonTypes().add(current_note_title);
            editWindow.getDialogPane().getButtonTypes().add(current_note_content);
            editWindow.getDialogPane().getButtonTypes().add(current_note_both);
            editWindow.getDialogPane().getButtonTypes().add(cancel);
            Optional<ButtonType> bt = editWindow.showAndWait();
            if (bt.isPresent()) {
                if (bt.get() != cancel) {
                    if (bt.get() == current_note_title) {
                        TextInputDialog editTitle = new TextInputDialog(data.getNoteTitle(p));
                        editTitle.setTitle("Edit Title");
                        editTitle.setContentText("Please enter what you would like the new title to be.");
                        editTitle.showAndWait();
                        if (editTitle.getResult() != null) {
                            l.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            b.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            String new_title = editTitle.getEditor().getText();
                            data.modifyNote(p, data.getNoteContent(p), new_title);
                            note_title_label.setText(data.getNoteTitle(p));
                            b.setTooltip(new Tooltip("Title: " + data.getNoteTitle(p) + "\n" + "Content: " + data.getNoteContent(p)));
                            l.setText(data.getNoteTitle(p));
                            handleClick = (EventHandler<MouseEvent>) mouseEvent -> sideWindowLoader(data.getNoteTitle(p), data.getNoteContent(p), p, l, b);
                            l.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            b.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            saved = false;
                        }
                    } else if (bt.get() == current_note_content) {
                        TextInputDialog editContent = new TextInputDialog(data.getNoteContent(p));
                        editContent.setTitle("Edit Content");
                        editContent.setContentText("Please enter what you would like the content to be.");
                        editContent.showAndWait();
                        if (editContent.getResult() != null) {
                            l.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            b.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            String new_content = editContent.getEditor().getText();
                            data.modifyNote(p, new_content, data.getNoteTitle(p));
                            note_content_label.setText(data.getNoteContent(p));
                            b.setTooltip(new Tooltip("Title: " + data.getNoteTitle(p) + "\n" + "Content: " + data.getNoteContent(p)));
                            l.setText(data.getNoteTitle(p));
                            handleClick = (EventHandler<MouseEvent>) mouseEvent -> sideWindowLoader(data.getNoteTitle(p), data.getNoteContent(p), p, l, b);
                            l.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            b.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                            saved = false;
                        }
                    } else if (bt.get() == current_note_both) {
                        TextInputDialog editTitle = new TextInputDialog(data.getNoteTitle(p));
                        editTitle.setTitle("Edit Title");
                        editTitle.setContentText("Please enter what you would like the new title to be.");
                        editTitle.showAndWait();
                        if (editTitle.getResult() != null) {
                            String new_title = editTitle.getEditor().getText();
                            TextInputDialog editContent = new TextInputDialog(data.getNoteContent(p));
                            editContent.setTitle("Edit Content");
                            editContent.setContentText("Please enter what you would like the content to be.");
                            editContent.showAndWait();
                            if (editContent.getResult() != null) {
                                l.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                                b.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                                String new_content = editContent.getEditor().getText();
                                data.modifyNote(p, new_content, new_title);
                                note_title_label.setText(data.getNoteTitle(p));
                                note_content_label.setText(data.getNoteContent(p));
                                b.setTooltip(new Tooltip("Title: " + data.getNoteTitle(p) + "\n" + "Content: " + data.getNoteContent(p)));
                                l.setText(data.getNoteTitle(p));
                                handleClick = (EventHandler<MouseEvent>) mouseEvent -> sideWindowLoader(data.getNoteTitle(p), data.getNoteContent(p), p, l, b);
                                l.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                                b.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                                saved = false;
                            }
                        }
                    } // End Last Check Case
                } // End Cancel Check
            } // End IsPresent Check
        });
        delete_note_button.setOnAction(f -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to delete this element?");
            alert.setTitle("Delete Note");
            alert.getButtonTypes().setAll(yes, cancel);
            alert.showAndWait().ifPresent(type -> {
                if (type == yes) {
                    scroll_pane_vbox.getChildren().remove(l);
                    note_list.remove(l);
                    data.removeNote(p);
                    anchor_window.getChildren().remove(b);
                    note_markers.remove(b);
                    note_title_label.setText("");
                    note_content_label.setText("");
                    saved = false;
                }
            });
        });
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
                    data.addNote(p, note, title);
                    Label b = new Label();
                    b.setLayoutX((int) event.getX() + 7);
                    b.setLayoutY((int) event.getY() + 36);
                    b.setTextFill(Paint.valueOf(color_choice_box.getValue().toString()));
                    b.setText("☆");
                    b.setId(Integer.toString(data.getNoteMapSize() - 1));
                    b.setFont(new Font("Arial", 20));
                    b.setTooltip(new Tooltip("Title: " + data.getNoteTitle(p) + "\n" + "Content: " + data.getNoteContent(p)));
                    note_markers.add(b);
                    anchor_window.getChildren().add(b);
                    Label l = new Label(title);
                    l.setId(Integer.toString(data.getNoteMapSize() - 1));
                    String fTitle = title;
                    handleClick = (EventHandler<MouseEvent>) mouseEvent -> sideWindowLoader(fTitle, note, p, l, b);
                    l.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                    b.addEventHandler(MouseEvent.MOUSE_CLICKED, handleClick);
                    note_list.add(l);
                    scroll_pane_vbox.getChildren().add(l);
                    saved = false;
                } // End of Content cancel check
            } // End of Title cancel check
        } // End of editEnabled check
    } // End of addMapNote()
}