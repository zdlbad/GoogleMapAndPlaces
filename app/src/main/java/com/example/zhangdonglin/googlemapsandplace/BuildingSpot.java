package com.example.zhangdonglin.googlemapsandplace;


//{"baby_facil":"no","female":"yes","lat":"-37.806121499077804","lon":"144.95653844268273",
// "male":"yes","name":"Public Toilet - Queen Victoria Market (153 Victoria Street)",
// "operator":"City of Melbourne","wheelchair":"no"}


public class BuildingSpot extends Object{
    private String building_name;
    private int accessibility_rating;
    private String accessibility_type;
    private String accessibility_type_description;
    private String street_address;
    private Double y_coordinate;
    private Double x_coordinate;
    private Double distance;

    public BuildingSpot() {
        this.building_name = "";
        this.accessibility_type = "";
        this.accessibility_type_description = "";
        this.street_address = "";
        this.accessibility_rating = 0;
        this.x_coordinate = 0.00;
        this.y_coordinate = 0.00;
        this.distance = 137.34;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public void setBuilding_name(String building_name) {
        this.building_name = building_name;
    }

    public int getAccessibility_rating() {
        return accessibility_rating;
    }

    public void setAccessibility_rating(String accessibility_rating) {
        this.accessibility_rating = Integer.parseInt(accessibility_rating);
    }

    public String getAccessibility_type() {
        return accessibility_type;
    }

    public void setAccessibility_type(String accessibility_type) {
        this.accessibility_type = accessibility_type;
    }

    public String getAccessibility_type_description() {
        return accessibility_type_description;
    }

    public void setAccessibility_type_description(String accessibility_type_description) {
        this.accessibility_type_description = accessibility_type_description;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public Double getX_coordinate() {
        return x_coordinate;
    }

    public void setX_coordinate(String x_coordinate) {
        this.x_coordinate = Double.parseDouble(x_coordinate);
    }

    public Double getY_coordinate() {
        return y_coordinate;
    }

    public void setY_coordinate(String y_coordinate) {
        this.y_coordinate = Double.parseDouble(y_coordinate);
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return  "Distance: " + distance + "meters " + '\n' +
                "Building: " + building_name  + '\n' +
                "Rating: " + accessibility_rating + '\n' +
                "Description: " + accessibility_type_description + '\n' +
                "Address:" + street_address + " ";
    }

    public boolean checkWithSample(BuildingSpot sample){
        return true;
    }

    public boolean checkLng(Double south, Double north){
        if (this.x_coordinate >= south && this.x_coordinate <= north){
            return true;
        }else{
            return false;
        }

    }
}
