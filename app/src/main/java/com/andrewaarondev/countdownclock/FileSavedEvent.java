package com.andrewaarondev.countdownclock;


public class FileSavedEvent {
    private String filename;
    FileSavedEvent(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
