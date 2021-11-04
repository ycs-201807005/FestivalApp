package com.example.festivalapp;

import android.util.Log;

import java.util.ArrayList;

public class EndEventinfo {
    //private String[] searchFestivalReply = {"contentid","eventstartdate","eventenddate", "firstimage", "firstimage2","mapx","mapy","title", "addr1", "addr2"};
    //          {"콘텐츠ID","행사 시작일","행사 종료일", "대표이미지(원본)", "대표이미지(썸네일)", "GPS X좌표=경도=latitude", "GPS Y좌표=위도=longitude", "제목", "주소", "상세주소"}
    //private String[] detailIntroReply
    // = {"contentid","agelimit","bookingplace","discountinfofestival", "homepage", "eventplace","placeinfo","playtime","program","sponsor1","sponsor2","subevent","usetimefestival"};
    //private String[] detailCommonReply = {"homepage", "overview"};

    //1.행사정보 조회
    private String contentid;
    private String eventstartdate;
    private String eventenddate;
    private String firstimage;
    private String title;

    //2.소개정보 조회
    private String eventplace;

    //3. 공통정보 조회
    private String overview;

    //생성자
    public EndEventinfo(ArrayList<String> values) {
        this.contentid = values.get(0);
        this.eventstartdate = values.get(1);
        this.eventenddate = values.get(2);
        this.firstimage = values.get(3);

        this.title = values.get(4);

        this.eventplace = values.get(5);
        this.overview = values.get(6);

        Log.e("test_api_list" , "collection - EndEventinfo 생성자");
    }


    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getEventstartdate() {
        return eventstartdate;
    }

    public void setEventstartdate(String eventstartdate) {
        this.eventstartdate = eventstartdate;
    }

    public String getFirstimage() {
        return firstimage;
    }

    public void setFirstimage(String firstimage) {
        this.firstimage = firstimage;
    }


    public String getEventenddate() {
        return eventenddate;
    }

    public void setEventenddate(String eventenddate) {
        this.eventenddate = eventenddate;
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


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

}
