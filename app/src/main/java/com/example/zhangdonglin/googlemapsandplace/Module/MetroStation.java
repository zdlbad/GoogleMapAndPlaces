package com.example.zhangdonglin.googlemapsandplace.Module;

import com.example.zhangdonglin.googlemapsandplace.MyTools;

public class MetroStation extends Object{

    private String he_loop;
    private String lift;
    private String pids;
    private String station;
    private Double lat;
    private Double lon;
    private Double distance;

    public MetroStation() {
        this.he_loop = "";
        this.lift = "";
        this.pids = "";
        this.station = "";
        this.lat = 0.0;
        this.lon = 0.0;
        this.distance = 52.128;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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
        if (distance >= 1000){
            return  "Distance:  " + MyTools.roundDouble(distance/1000) + " km " + '\n' +
                    "Station: " + station + '\n' +
                    "Ramp: " + he_loop + '\n' +
                    "Lift: " + lift ;
        }else{
            return  "Distance:  " +  MyTools.roundDoubleWithOutDecimal(distance) + " m " + '\n' +
                    "Station: " + station + '\n' +
                    "Ramp: " + he_loop + '\n' +
                    "Lift: " + lift ;
        }
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
