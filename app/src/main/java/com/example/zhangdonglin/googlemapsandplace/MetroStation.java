package com.example.zhangdonglin.googlemapsandplace;

import java.util.Arrays;

public class MetroStation {

    private String he_loop;
    private String lift;
    private String pids;
    private String station;
    private Double[] coordinates;

    public MetroStation() {
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

    public Double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Double[] coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "MetroStation{" +
                "he_loop='" + he_loop + '\'' +
                ", lift='" + lift + '\'' +
                ", pids='" + pids + '\'' +
                ", station='" + station + '\'' +
                ", coordinates=" + Arrays.toString(coordinates) +
                '}';
    }
}
