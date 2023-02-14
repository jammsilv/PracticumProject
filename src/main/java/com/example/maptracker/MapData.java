package com.example.maptracker;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapData {

    private BufferedImage mapImage;
    private Map<Point, String> notes;

    //DO NOT USE THIS ONE -- ONLY MEANT TO BE FILLER FOR NOW
    public MapData() {
        this.mapImage = null;
        this.notes = new HashMap<Point, String>();
    }
    public MapData(BufferedImage mapImage) {
        this.mapImage = mapImage;
        this.notes = new HashMap<Point, String>();
    }

    public MapData(BufferedImage mapImage, Map<Point, String> notes) {
        this.mapImage = mapImage;
        this.notes = notes;
    }

    public void setImage(BufferedImage image) {
        mapImage = image;
    }

    public BufferedImage getImage() {
        return mapImage;
    }

    public void addNote(Point p, String note) {
        notes.put(p, note);
    }

    public void removeNote(Point p) {
        notes.remove(p);
    }

    private void modifyNote(Point p, String note) {
        notes.replace(p, note);
    }
}
