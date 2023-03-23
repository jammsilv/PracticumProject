package com.example.maptracker;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapData {

    private BufferedImage mapImage;
    private Map<Point, String> notes_content;
    private Map<Point, String> notes_titles;

    //DO NOT USE THIS ONE -- ONLY MEANT TO BE FILLER FOR NOW
    public MapData() {
        this.mapImage = null;
        this.notes_content = new HashMap<Point, String>();
        this.notes_titles = new HashMap<Point, String>();
    }
    // creating a New Map
    public MapData(BufferedImage mapImage) {
        this.mapImage = mapImage;
        this.notes_content = new HashMap<Point, String>();
        this.notes_titles = new HashMap<Point, String>();
    }

    public MapData(BufferedImage mapImage, Map<Point, String> notes_content, Map<Point, String> notes_titles) {
        this.mapImage = mapImage;
        this.notes_content = notes_content;
        this.notes_titles = notes_titles;
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

    private void modifyNote(Point p, String content, String title) {
        notes_content.replace(p, content);
        notes_titles.replace(p, title);
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
