package com.robotemi.BrokenRecord.Entity;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class TimeSlot {

    private GregorianCalendar dateTime;
    private ArrayList<String> locations;
    private ArrayList<String> videoLinks;
    private int currentLocationPointer;
    public static final int AT_THE_BEGINNING = -1;
    public static final int AT_THE_END = -2;
    public TimeSlot() {

    }

    public TimeSlot(GregorianCalendar dateTime, ArrayList<String> locations, ArrayList<String> videoLinks) {
        super();
        this.setCurrentLocationPointer(AT_THE_BEGINNING);
        this.setDateTime(dateTime);

        this.setVideoLinks(videoLinks);
        this.setLocations(locations);
    }


    public GregorianCalendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(GregorianCalendar dateTime) {
        this.dateTime = dateTime;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<String> location) {
        this.locations = location;
    }

    public ArrayList<String> getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(ArrayList<String> videoLinks) {
        this.videoLinks = videoLinks;
    }

    public int getCurrentLocationPointer() {
        return currentLocationPointer;
    }

    public void setCurrentLocationPointer(int currentLocationPointer) {
        this.currentLocationPointer = currentLocationPointer;
    }
}
