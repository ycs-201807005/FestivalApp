package com.example.festivalapp;

public class MarkerInfo {
    private String contentid;
    private String image;
    private String title;
    private String eventplace;

    public MarkerInfo(String contentid, String image, String title, String eventplace){
        this.contentid = contentid;
        this.image = image;
        this.title = title;
        this.eventplace = eventplace;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventplace() {
        return eventplace;
    }

    public void setEventplace(String eventplace) {
        this.eventplace = eventplace;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
