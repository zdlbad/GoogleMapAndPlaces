package com.example.zhangdonglin.googlemapsandplace;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.JsonWriter;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ToiletManager {
    private static final String BASE_URL_TOILET = "https://data.melbourne.vic.gov.au/resource/dsec-5y6t.json?$where=";
    private static final String TAG = "ToiletManager";
    private HttpURLConnection connection;
    private Double south;
    private Double north;
    private Double east;
    private Double west;

    //firebase var
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;



    public ToiletManager(){
        connection = null;
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference().child("toilet");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        StringBuilder builder = new StringBuilder(BASE_URL_TOILET);
        builder.append("lat > \"" + east + "\" and lat < \"" + west + "\" and lon < \"" + north + "\" and lon > \"" + south + "\"");
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

    public JsonArray FindNearbyToilets(){

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

    public void searchByLatRange(){
        Query q = myRef.orderByChild("lat").startAt(east+"").endAt(west+"");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot o : dataSnapshot.getChildren()){
                    HashMap<String,Object> child = (HashMap<String,Object>) o.getValue();
                    List<Object> f = (List)child.values();
                    JsonObject k = (JsonObject) f.get(0);
                    Log.d(TAG, "get lat: " + k.toString());

//                    String g = k.replaceAll("([a-zA-Z0-9-. \\)\\(_]+)", "\"$0\"");
//                    Log.d(TAG, "get lat: " + g);
//                    JsonObject l = new JsonParser().parse(g).getAsJsonObject();
//                    Log.d(TAG, "!!!!!!!!!" + l.toString());
                    count += 1;
                }
                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
