package com.example.festivalapp;

public class Memberinfo {
    private String name;
    private double mapx;
    private double mapy;
    private String address;

    public Memberinfo(String name, double mapx, double mapy, String address) {
        this.name = name;
        this.mapx = mapx;
        this.mapy = mapy;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMapx() {
        return mapx;
    }

    public void setMapx(double mapx) {
        this.mapx = mapx;
    }

    public double getMapy() {
        return mapy;
    }

    public void setMapy(double mapy) {
        this.mapy = mapy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
