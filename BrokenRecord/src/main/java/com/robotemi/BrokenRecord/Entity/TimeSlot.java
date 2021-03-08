package com.robotemi.BrokenRecord.Entity;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class TimeSlot {

    private GregorianCalendar dateTime;
    private ArrayList<String> locations;
    private ArrayList<Multimedia> multimediaLinks;

    private int NextLocationPointer;
    private String name;

    public TimeSlot() {

    }

    public TimeSlot(GregorianCalendar dateTime, ArrayList<String> locations, ArrayList<Multimedia> videoLinks, String name) {
        super();
        this.dateTime = dateTime;
        this.locations = locations;
        this.multimediaLinks = videoLinks;
        this.name = name;
        this.setNextLocationPointer(1);
    }


    /**
     * @return gregorianCalendar object
     */
    public GregorianCalendar getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime for setting GregorianCalendar dateTime
     */
    public void setDateTime(GregorianCalendar dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return list of points to travel to.
     */
    public ArrayList<String> getLocations() {
        return locations;
    }

    /**
     * @param locations set locations of points to travel to
     */
    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    /**
     * @return list of links that could be audio or video, online and offline
     */
    public ArrayList<Multimedia> getMultimediaLinksLinks() {
        return this.multimediaLinks;
    }

    /**
     * @param  multimediaLinks list of links that could be audio or video, online and offline
     */
    public void setMultimediaLinksLinks(ArrayList<Multimedia> multimediaLinks) {
        this.multimediaLinks = multimediaLinks;
    }

    /**
     * @return returns a number that shows which point of the
     */
    public int getNextLocationPointer() {
        if (NextLocationPointer == locations.size())
            System.out.println("You are at the end of the travel sequence");
        return NextLocationPointer;
    }

    /**
     * @param nextLocationPointer set the next location to go to, if next location == location arraylist size, that means that in this timeslot, the robot has traveled all the way through
     */
    public void setNextLocationPointer(int nextLocationPointer) {
        this.NextLocationPointer = nextLocationPointer;
    }


    /**
     * @return Get name of timeslot
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Set name of timeslot
     */
    public void setName(String name) {
        this.name = name;
    }


}
