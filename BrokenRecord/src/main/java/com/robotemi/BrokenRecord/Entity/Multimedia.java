package com.robotemi.BrokenRecord.Entity;

import com.robotemi.BrokenRecord.Enumeration.MediaType;

public class Multimedia {
    private String multiMediaLink;
    private String name;
    private MediaType mediaType;

    /**
     * default constructor
     */
    public Multimedia() {
    }

    /**
     * @param multiMediaLink online or offline link
     * @param name name of multimedia
     * @param mediaType what media type is this? Audio or Video?
     */
    public Multimedia(String multiMediaLink, String name, MediaType mediaType) {
        super();
        this.setMultiMediaLink(multiMediaLink);
        this.setName(name);
        this.setMediaType(mediaType);
    }

    /**
     * @return returns the online or offline link
     */
    public String getMultiMediaLink() {
        return multiMediaLink;
    }
    /**
     * @param multiMediaLink set the online or offline link
     */
    public void setMultiMediaLink(String multiMediaLink) {
        this.multiMediaLink = multiMediaLink;
    }
    /**
     * @return returns the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name set the name of the multimedia
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return returns the media type, either audio or video
     */
    public MediaType getMediaType() {
        return mediaType;
    }
    /**
     * @param mediaType sets the media type, either audio or video
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
}
