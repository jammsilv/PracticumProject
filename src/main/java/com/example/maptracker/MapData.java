package com.example.maptracker;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapData implements java.io.Serializable {

    transient private BufferedImage mapImage;
    private Map<Point, String> notes_content;
    private Map<Point, String> notes_titles;

    // Creating a New Map
    public MapData() {
        this.mapImage = null;
        this.notes_content = new HashMap<>();
        this.notes_titles = new HashMap<>();
    }

    public void setImage(BufferedImage image) {
        mapImage = image;
    }

    public BufferedImage getImage() {
        return mapImage;
    }

    public void addNote(Point p, String content, String title) {
        notes_content.put(p, content);
        notes_titles.put(p, title);
    }

    public void removeNote(Point p) {
        notes_content.remove(p);
        notes_titles.remove(p);
    }

    public void modifyNote(Point p, String content, String title) {
        notes_content.replace(p, content);
        notes_titles.replace(p, title);
    }
    public Map<Point, String> getNoteTitleList() {
        return notes_titles;
    }
    public Map<Point, String> getNoteContentList() {
        return notes_content;
    }

    public String getNoteTitle(Point p) {
        return notes_titles.get(p);
    }

    public String getNoteContent(Point p) {
        return notes_content.get(p);
    }

    public int getNoteMapSize() {
        return notes_content.size();
    }
}
