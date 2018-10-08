package com.example.zhangdonglin.googlemapsandplace.Module;


//{"baby_facil":"no","female":"yes","lat":"-37.806121499077804","lon":"144.95653844268273",
// "male":"yes","name":"Public Toilet - Queen Victoria Market (153 Victoria Street)",
// "operator":"City of Melbourne","wheelchair":"no"}


import com.example.zhangdonglin.googlemapsandplace.MyTools;

public class Toilet extends Object{
    private String baby_facil;
    private String female;
    private String male;
    private String name;
    private String operator;
    private String wheelchair;
    private Double lat;
    private Double lon;
    private Double distance;

    public Toilet() {
        this.baby_facil = "";
        this.female = "";
        this.male = "";
        this.name = "";
        this.operator = "";
        this.wheelchair = "";
        this.lat = 0.00;
        this.lon = 0.00;
        this.distance = 137.34;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getBaby_facil() {
        return baby_facil;
    }

    public void setBaby_facil(String baby_facil) {
        this.baby_facil = baby_facil;
    }

    public String getFemale() {
        return female;
    }

    public void setFemale(String female) {
        this.female = female;
    }

    public String getMale() {
        return male;
    }

    public void setMale(String male) {
        this.male = male;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(String wheelchair) {
        this.wheelchair = wheelchair;
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
                    "Wheelchair Access: " + MyTools.captialFirstChar(wheelchair);
        }else{
            return  "Distance:  " + MyTools.roundDoubleWithOutDecimal(distance) + " m " + '\n' +
                    "Wheelchair Access: " + MyTools.captialFirstChar(wheelchair);
        }
    }

    public boolean checkWithSample(Toilet sample){
        if (sample.getBaby_facil() != ""){
            if (!sample.getBaby_facil().equals(this.baby_facil)){
                return false;
            }
        }

        if (sample.getFemale() != ""){
            if (!sample.getFemale().equals(this.female)){
                return false;
            }
        }

        if (sample.getMale() != ""){
            if (!sample.getMale().equals(this.male)){
                return false;
            }
        }

        if (sample.getName() != ""){
            if (!sample.getName().equals(this.name)){
                return false;
            }
        }

        if (sample.getOperator() != ""){
            if (!sample.getOperator().equals(this.operator)){
                return false;
            }
        }

        if (sample.getWheelchair() != ""){
            if (!sample.getWheelchair().equals(this.wheelchair)){
                return false;
            }
        }

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
