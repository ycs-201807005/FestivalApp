package com.example.festivalapp;

public class FestivalInfo {
    private String contentid;
    private int dist;
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

    public FestivalInfo(String contentid, int dist, String image, String title, String eventstartdate, String eventenddate, String eventplace){
        this.contentid = contentid;
        this.dist = dist;
        this.image = image;
        this.title = title;
        this.eventstartdate=eventstartdate;
        this.eventenddate=eventenddate;
        this.eventplace=eventplace;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
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

    public int compareTo(FestivalInfo o2) {
        if(this.dist>o2.dist) return 1;
        else if (this.dist<o2.dist) return -1;
        else return 0;
    }
}
