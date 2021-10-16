package com.example.festivalapp;

public class FestivalInfo {
    private String contentid;
    private String image;
    private String title;
    private String eventstartdate;
    private String eventenddate;
    private String eventplace;

    public FestivalInfo(String contentid, String image, String title, String eventstartdate, String eventenddate, String eventplace){
        this.contentid = contentid;
        this.image = image;
        this.title = title;
        this.eventstartdate=eventstartdate;
        this.eventenddate=eventenddate;
        this.eventplace=eventplace;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventstartdate() {
        return eventstartdate;
    }

    public void setEventstartdate(String eventstartdate) {
        this.eventstartdate = eventstartdate;
    }

    public String getEventenddate() {
        return eventenddate;
    }

    public void setEventenddate(String eventenddate) {
        this.eventenddate = eventenddate;
    }

    public String getEventplace() {
        return eventplace;
    }

    public void setEventplace(String eventplace) {
        this.eventplace = eventplace;
    }
}
