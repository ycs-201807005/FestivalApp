package com.example.festivalapp;

public class ReviewInfo {
    private String writedate;
    private String rid;
    private String contentid;
    private String title;
    private String contents;
    private String eventplace;
    private String star;

    public ReviewInfo(String writedate, String rid, String contentid, String title, String contents, String eventplace, String star){
        this.writedate = writedate;
        this.rid = rid;
        this.contentid = contentid;
        this.title = title;
        this.eventplace = eventplace;
        this.contents =contents;
        this.star = star;
    }

    public String getWritedate() {
        return writedate;
    }

    public void setWritedate(String writedate) {
        this.writedate = writedate;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
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

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
