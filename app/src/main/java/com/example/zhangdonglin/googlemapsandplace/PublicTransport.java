package com.example.zhangdonglin.googlemapsandplace;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class PublicTransport {
    HttpURLConnection connection;
    Double south;
    Double north;
    Double east;
    Double west;


    public PublicTransport(){
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

    private String BuildURL(String baseUrl){
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("lat < " + east + " and lat > " + west + " and lon < " + north + " and lon > " + south);
        return builder.toString();
    }

    private boolean MakeAPIConnection(String baseUrl){
        int code = 0;
        try {
            URL url = new URL(BuildURL(baseUrl));
            connection = (HttpURLConnection) url.openConnection();
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

    public JsonArray FindMetroStations(String baseUrl){

        String serverResult = "";
        JsonArray results = null;

        if(MakeAPIConnection(baseUrl)) {
            try {
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                //set the connection method to GET
                connection.setRequestMethod("GET");
                //add http headers to set your response type to json
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
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
        } else{

        }
        return results;
    }
}
