package com.robotemi.BrokenRecord.Entity;

import com.robotemi.BrokenRecord.Enumeration.MediaType;

public class YouTubeVideo extends Multimedia{
    private String title;
    private String videoId;


    public YouTubeVideo(String title, String videoId) {
        super(videoId, title, MediaType.video, true);
        this.title = title;
        this.videoId = videoId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
