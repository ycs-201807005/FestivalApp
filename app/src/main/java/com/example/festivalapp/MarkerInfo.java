package com.example.festivalapp;

public class MarkerInfo {
    private String contentid;
    private String title;
    private String eventplace;

    public MarkerInfo(String contentid, String title, String eventplace){
        this.contentid = contentid;
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

}
