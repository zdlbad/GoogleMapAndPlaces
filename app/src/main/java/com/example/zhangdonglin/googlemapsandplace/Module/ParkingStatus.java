package com.example.zhangdonglin.googlemapsandplace.Module;

public class ParkingStatus {
    private int bayId;
    private Double lat;
    private Double lon;
    private String status;
    private Double distance;

    public ParkingStatus(int bayId, Double lat, Double lon, String status, Double distance) {
        this.bayId = bayId;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.distance = distance;
    }

    public int getBayId() {
        return bayId;
    }

    public void setBayId(int bayId) {
        this.bayId = bayId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
