package com.example.zhangdonglin.googlemapsandplace.Module;

import com.example.zhangdonglin.googlemapsandplace.MyTools;

public class Garden extends Object{
    private String name;
    private Double lat;
    private Double lon;
    private Double distance;

    public Garden() {
        this.name = "";
        this.lat = 0.00;
        this.lon = 0.00;
        this.distance = 137.34;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        if (distance >= 1000){
            return  "Distance:  " + MyTools.roundDouble(distance/1000) + " km " + '\n' +
                    "Name: " + name + " ";
        }else{
            return  "Distance:  " +  MyTools.roundDoubleWithOutDecimal(distance) + " m " + '\n' +
                    "Name: " + name + " ";
        }
    }

    public boolean checkWithSample(Garden sample){
        return true;
    }

    public boolean checkLng(Double south, Double north){
        if (this.lon >= south && this.lon <= north){
            return true;
        }else{
            return false;
        }

    }
}
