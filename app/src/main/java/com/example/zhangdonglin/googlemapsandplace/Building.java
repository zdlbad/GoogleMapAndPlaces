package com.example.zhangdonglin.googlemapsandplace;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Building {
    private HttpURLConnection connection;
    private Double north;
    private Double south;
    private Double east;
    private Double west;
    private static final String BASE_URL_BUILDING = "https://data.melbourne.vic.gov.au/resource/qabw-suvb.json?$where=";



    public Building(){
        connection = null;
    }

    public void setNorth(Double north) {
        this.north = north;
    }

    public void setSouth(Double south) {
        this.south = south;
    }

    public void setEast(Double east) {
        this.east = east;
    }

    public void setWest(Double west) {
        this.west = west;
    }

    private String BuildURL(){
        StringBuilder builder = new StringBuilder(BASE_URL_BUILDING);
        builder.append(" y_coordinate < " + east +" and y_coordinate > "+ west + " and x_coordinate < " + north +" and x_coordinate > "+ south );
        return builder.toString();
    }


    private boolean MakeAPIConnection(){
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

    public JsonArray FindBuildingAccessibility(){
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
