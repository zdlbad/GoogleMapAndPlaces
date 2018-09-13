package com.example.zhangdonglin.googlemapsandplace;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Parking {
    private static final String BASE_URL_PARKING = "https://data.melbourne.vic.gov.au/resource/dtpv-d4pf.json?$where=";
    private HttpURLConnection connection;
    private Double south;
    private Double north;
    private Double east;
    private Double west;

    public Parking(){
        connection = null;
    }

    public void setNorth(Double north) {
        this.north = north;
    }

    public void setEast(Double east) {
        this.east = east;
    }

    public void setWest(Double west) {
        this.west = west;
    }

    public void setSouth(Double south) {
        this.south = south;
    }

    private String BuildURL(){
        StringBuilder builder = new StringBuilder(BASE_URL_PARKING);
        builder.append("lat < " + east + " and lat > " + west + " and lon < " + north + " and lon > " + south);
        return builder.toString();
    }

    public boolean MakeAPIConnection(){
        int code = 0;
        try {
            URL url = new URL(BuildURL());
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            //set the connection method to GET
            connection.setRequestMethod("GET");
            //add http headers to set your response type to json
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            code = connection.getResponseCode();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        if(code == 200)
            return true;
        else
            return false;
    }

    private void RemoveAPIConnection(){
        connection.disconnect();
    }

    public JsonArray FindParkingSpots(){

        String serverResult = "";
        JsonArray results = null;

        if(MakeAPIConnection()) {
            try {
                //Read the response
                Scanner inStream = new Scanner(connection.getInputStream());
                //read the input stream and store it as string
                while (inStream.hasNextLine()) {
                    serverResult += inStream.nextLine();
                }
                results = new JsonParser().parse(serverResult).getAsJsonArray();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                RemoveAPIConnection();
            }
        }
        return results;
    }
}
