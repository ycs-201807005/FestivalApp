package com.example.festivalapp;

public class ReviewInfo{
    private String reviewid;
    private String writer;
    private String writedate;
    private String contentid;
    private String title;
    private String contents;
    private double rating;

    //user for Review Write|Read
    public ReviewInfo(String writer, String writedate, String contentid, String title, String contents, double rating){
        this.writer = writer;
        this.writedate = writedate;
        this.contentid = contentid;
        this.title = title;
        this.contents =contents;
        this.rating = rating;
    }

    //use for User's Review List
    public ReviewInfo(String reviewid, String writedate, String title, String contents, double rating){
        this.reviewid = reviewid;
        this.writedate = writedate;
        this.title = title;
        this.contents =contents;
        this.rating = rating;
    }

    public String getReviewid() {
        return reviewid;
    }

    public void setReviewid(String reviewid) {
        this.reviewid = reviewid;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWritedate() {
        return writedate;
    }

    public void setWritedate(String writedate) {
        this.writedate = writedate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int compareTo(ReviewInfo o2) {
        return this.writedate.compareTo(o2.writedate);
    }
}
