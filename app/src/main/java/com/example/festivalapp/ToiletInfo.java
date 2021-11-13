package com.example.festivalapp;

public class ToiletInfo {
    private String toiletNm;
    private String rdnmadr;
    private String openTime;
    private String latitude;
    private String longitude;

    public ToiletInfo(String toiletNm, String rdnmadr, String openTime, String latitude, String longitude){
        this.toiletNm = toiletNm;
        this.rdnmadr = rdnmadr;
        this.openTime = openTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getToiletNm() {
        return toiletNm;
    }

    public void setToiletNm(String toiletNm) {
        this.toiletNm = toiletNm;
    }

    public String getRdnmadr() {
        return rdnmadr;
    }

    public void setRdnmadr(String rdnmadr) {
        this.rdnmadr = rdnmadr;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
