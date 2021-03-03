package com.robotemi.BrokenRecord.Entity;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class TimeSlot {

    private GregorianCalendar dateTime;
    private ArrayList<String> locations;
    private ArrayList<String> videoLinks;
    private int currentLocationPointer;
    private String name;

    public static final int AT_THE_BEGINNING = -1;
    public static final int AT_THE_END = -2;

    public TimeSlot() {

    }

    public TimeSlot(GregorianCalendar dateTime, ArrayList<String> locations, ArrayList<String> videoLinks, String name) {
        this.dateTime = dateTime;
        this.locations = locations;
        this.videoLinks = videoLinks;
        this.name = name;
        this.setCurrentLocationPointer(AT_THE_BEGINNING);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
