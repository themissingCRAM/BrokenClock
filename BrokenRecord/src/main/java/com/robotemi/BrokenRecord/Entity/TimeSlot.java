package com.robotemi.BrokenRecord.Entity;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;


public class TimeSlot implements Serializable {

    private GregorianCalendar dateTime;
    // private ArrayList<String> locations;

    //locationVideos DOES NOT have home base as a location
    private HashMap<String, Multimedia[]> locationVideos;
    private int NextLocationPointer;
    private String name;

    public TimeSlot() {

    }


    public TimeSlot(GregorianCalendar dateTime, String name, HashMap<String, Multimedia[]> locationVideos) {
        super();
        this.dateTime = dateTime;
        // this.multimediaLinks = videoLinks;
        this.name = name;
        this.setNextLocationPointer(0);
        this.locationVideos = locationVideos;
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

//    /**
//     * @return list of points to travel to.
//     */
//    public ArrayList<String> getLocations() {
//        return locations;
//    }
//
//    /**
//     * @param locations set locations of points to travel to
//     */
//    public void setLocations(ArrayList<String> locations) {
//        this.locations = locations;
//    }


    /**
     * @return returns a number that shows which point of the
     */
    public int getNextLocationPointer() {
        if (NextLocationPointer == locationVideos.size())
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


    public HashMap<String, Multimedia[]> getLocationVideos() {
        return locationVideos;
    }

    public void setLocationVideos(HashMap<String, Multimedia[]> locationVideos) {
        this.locationVideos = locationVideos;
    }
}
