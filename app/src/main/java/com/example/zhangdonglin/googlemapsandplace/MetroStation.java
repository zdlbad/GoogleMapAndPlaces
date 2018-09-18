package com.example.zhangdonglin.googlemapsandplace;

import java.util.Arrays;

public class MetroStation {

    private String he_loop;
    private String lift;
    private String pids;
    private String station;
    private Double lat;
    private Double lon;

    public MetroStation() {
        this.he_loop = "";
        this.lift = "";
        this.pids = "";
        this.station = "";
        this.lat = 0.0;
        this.lon = 0.0;
    }

    public String getHe_loop() {
        return he_loop;
    }

    public void setHe_loop(String he_loop) {
        this.he_loop = he_loop;
    }

    public String getLift() {
        return lift;
    }

    public void setLift(String lift) {
        this.lift = lift;
    }

    public String getPids() {
        return pids;
    }

    public void setPids(String pids) {
        this.pids = pids;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = Double.parseDouble(lat);
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = Double.parseDouble(lon);
    }

    @Override
    public String toString() {
        return  " He_loop='" + he_loop + '\'' + '\n' +
                " Lift='" + lift + '\'' + '\n' +
                " Pids='" + pids + '\'' + '\n' +
                " Station='" + station + '\'';
    }

    public boolean checkWithSample(MetroStation sample) {
        if (sample.getHe_loop() != ""){
            if (!sample.getHe_loop().equals(this.he_loop)){
                return false;
            }
        }

        if (sample.getStation() != ""){
            if (!sample.getStation().equals(this.station)){
                return false;
            }
        }

        if (sample.getLift() != ""){
            if (!sample.getLift().equals(this.lift)){
                return false;
            }
        }

        if (sample.getPids() != ""){
            if (!sample.getPids().equals(this.pids)){
                return false;
            }
        }

        return true;

    }
}
