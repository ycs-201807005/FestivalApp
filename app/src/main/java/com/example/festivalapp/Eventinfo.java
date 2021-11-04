package com.example.festivalapp;

import android.util.Log;

import java.util.ArrayList;

public class Eventinfo {
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
    private String firstimage2;
    private String mapx;
    private String mapy;
    private String title;
    private String addr1;
    private String addr2;

    //2.소개정보 조회
    private String agelimit;
    private String bookingplace;
    private String discountinfofestival;
    private String eventplace;
    private String placeinfo;
    private String playtime;
    private String program;
    private String sponsor1;
    private String sponsor2;
    private String subevent;
    private String usetimefestival;

    //3. 공통정보 조회
    private String homepage;
    private String overview;

    //4.이미지정보조회
   private ArrayList<String> images;

   private String running;

    //생성자
    public Eventinfo(ArrayList<String> values) {
        this.contentid = values.get(0);
        this.eventstartdate = values.get(1);
        this.eventenddate = values.get(2);
        this.firstimage = values.get(3);
        this.firstimage2 = values.get(4);
        this.mapx = values.get(5);
        this.mapy = values.get(6);
        this.title = values.get(7);
        this.addr1 = values.get(8);
        this.addr2 = values.get(9);

        this.agelimit = values.get(10);
        this.bookingplace = values.get(11);
        this.discountinfofestival = values.get(12);
        this.eventplace = values.get(13);
        this.placeinfo = values.get(14);
        this.playtime = values.get(15);
        this.program = values.get(16);
        this.sponsor1 = values.get(17);
        this.sponsor2 = values.get(18);
        this.subevent = values.get(19);
        this.usetimefestival = values.get(20);

        this.homepage = values.get(21);
        this.overview = values.get(22);

        running = "N";

        Log.e("test_api_list" , "collection - Eventinfo 생성자");
    }

    //생성자
    public Eventinfo(ArrayList<String> values, ArrayList<String> images) {
        this.contentid = values.get(0);
        this.eventstartdate = values.get(1);
        this.eventenddate = values.get(2);
        this.firstimage = values.get(3);
        this.firstimage2 = values.get(4);
        this.mapx = values.get(5);
        this.mapy = values.get(6);
        this.title = values.get(7);
        this.addr1 = values.get(8);
        this.addr2 = values.get(9);

        this.agelimit = values.get(10);
        this.bookingplace = values.get(11);
        this.discountinfofestival = values.get(12);
        this.eventplace = values.get(13);
        this.placeinfo = values.get(14);
        this.playtime = values.get(15);
        this.program = values.get(16);
        this.sponsor1 = values.get(17);
        this.sponsor2 = values.get(18);
        this.subevent = values.get(19);
        this.usetimefestival = values.get(20);

        this.homepage = values.get(21);
        this.overview = values.get(22);

        this.images = images;

        running = "N";

        Log.e("test_api_list" , "collection - 이미지 리스트 포함 Eventinfo 생성자");
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

    public String getFirstimage2() {
        return firstimage2;
    }

    public void setFirstimage2(String firstimage2) {
        this.firstimage2 = firstimage2;
    }

    public String getEventenddate() {
        return eventenddate;
    }

    public void setEventenddate(String eventenddate) {
        this.eventenddate = eventenddate;
    }

    public String getMapx() {
        return mapx;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public String getMapy() {
        return mapy;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getAgelimit() {
        return agelimit;
    }

    public void setAgelimit(String agelimit) {
        this.agelimit = agelimit;
    }

    public String getBookingplace() {
        return bookingplace;
    }

    public void setBookingplace(String bookingplace) {
        this.bookingplace = bookingplace;
    }

    public String getDiscountinfofestival() {
        return discountinfofestival;
    }

    public void setDiscountinfofestival(String discountinfofestival) {
        this.discountinfofestival = discountinfofestival;
    }

    public String getEventplace() {
        return eventplace;
    }

    public void setEventplace(String eventplace) {
        this.eventplace = eventplace;
    }

    public String getPlaceinfo() {
        return placeinfo;
    }

    public void setPlaceinfo(String placeinfo) {
        this.placeinfo = placeinfo;
    }

    public String getPlaytime() {
        return playtime;
    }

    public void setPlaytime(String playtime) {
        this.playtime = playtime;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSponsor1() {
        return sponsor1;
    }

    public void setSponsor1(String sponsor1) {
        this.sponsor1 = sponsor1;
    }

    public String getSponsor2() {
        return sponsor2;
    }

    public void setSponsor2(String sponsor2) {
        this.sponsor2 = sponsor2;
    }

    public String getSubevent() {
        return subevent;
    }

    public void setSubevent(String subevent) {
        this.subevent = subevent;
    }

    public String getUsetimefestival() {
        return usetimefestival;
    }

    public void setUsetimefestival(String usetimefestival) {
        this.usetimefestival = usetimefestival;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
