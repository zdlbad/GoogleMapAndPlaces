package com.example.zhangdonglin.googlemapsandplace.Controller;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.zhangdonglin.googlemapsandplace.View.MapActivity;
import com.example.zhangdonglin.googlemapsandplace.MyTools;
import com.example.zhangdonglin.googlemapsandplace.Module.ParkingStatus;
import com.example.zhangdonglin.googlemapsandplace.Module.ParkingSpot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class ParkingSpotManager {
    private static final String BASE_URL_PARKING_STATUS = "https://data.melbourne.vic.gov.au/resource/dtpv-d4pf.json?$where=";
    private static final String BASE_URL_PARKING_RESTRICTION = "https://data.melbourne.vic.gov.au/resource/rzb8-bz3y.json?$where=";
    private static final String TAG = "ParkingSpotManager";

    private HttpURLConnection connection;
    private Double south;
    private Double north;
    private Double east;
    private Double west;
    public ArrayList<ParkingSpot> searchingResult;
    public ArrayList<Marker> markerList;
    private ParkingSpot sampleParkingSpot;
    private int distance;

    //firebase var
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;

    public ParkingSpotManager(){
        connection = null;
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference().child("parking_restriction");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        searchingResult = new ArrayList<ParkingSpot>();
        markerList = new ArrayList<Marker>();
        sampleParkingSpot = new ParkingSpot();
        sampleParkingSpot.setStatus("Unoccupied");
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

    public void setSampleParkingSpot(ParkingSpot sampleParkingSpot) {
        this.sampleParkingSpot = sampleParkingSpot;
    }

    public ParkingSpot getSampleParkingSpot() {
        return sampleParkingSpot;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    private String BuildURLForLocation(){
        StringBuilder builder = new StringBuilder(BASE_URL_PARKING_STATUS);
        builder.append("lat < " + east + " and lat > " + west + " and lon < " + north + " and lon > " + south);
        return builder.toString();
    }

    public boolean MakeAPIConnectionForLocation() {
        int code = 0;
        try {
            URL url = new URL(BuildURLForLocation());
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            //set the connection method to GET
            connection.setRequestMethod("GET");
            //add http headers to set your response type to json
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            code = connection.getResponseCode();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (code == 200)
            return true;
        else
            return false;
    }

    private void RemoveAPIConnection(){
        connection.disconnect();
    }

    public void searchParkingAndFilterAndShow(final MapActivity mapActivity){
        searchingResult.clear();
        markerList.clear();
        new AsyncTask<Void, Void, JsonArray>(){
            @Override
            protected JsonArray doInBackground(Void... voids) {
                return searchParkingByRadius();
            }
            @Override
            protected void onPostExecute(JsonArray spots) {

                if (spots.size() != 0) {
                    Log.d(TAG, "------- geting result from first level search. count: " + spots.size()
                    );
                    Log.d(TAG, "------- Sample spot:" + sampleParkingSpot.toString());

                    ArrayList<ParkingStatus> firstResult = sortAndSelect(spots, mapActivity);

                    for (final ParkingStatus oneStatus: firstResult) {
                        Query q = myRef.orderByChild("BayID").equalTo(oneStatus.getBayId() + "");
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot o : dataSnapshot.getChildren()) {
                                        ParkingSpot oneParking = o.getValue(ParkingSpot.class);
                                        oneParking.setLat(oneStatus.getLat());
                                        oneParking.setLon(oneStatus.getLon());
                                        oneParking.setStatus(oneStatus.getStatus());
                                        oneParking.checkSpot();
                                        Log.d(TAG, "------- one spot:" + oneParking.toString());
                                        if (oneParking.chechWithSampleSpot(sampleParkingSpot)) {
                                            Log.d(TAG, "------- one spot valid");
                                            LatLng remote = mapActivity.remoteLatlng;
                                            Double distance = MyTools.getDistanceFromLatLonInMeter(remote.latitude, remote.longitude, oneParking.getLat(), oneParking.getLon());
                                            oneParking.setDistance(MyTools.roundDouble(distance));
                                            searchingResult.add(oneParking);
                                            mapActivity.showParkingSpot(oneParking);
                                        } else {
                                            Log.d(TAG, "!!!!!!! one spot invalid");
                                        }
                                    }
                                    ArrayList<Object> objectArrayList = new ArrayList<>();
                                    for (ParkingSpot oneParkingSpot : searchingResult) {
                                        objectArrayList.add((Object) oneParkingSpot);
                                    }
                                    mapActivity.showSearchingResultList(objectArrayList);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                mapActivity.cleanMap();
                            }
                        });
                    }
                    Log.d(TAG, "==========Search after LatRange Query===========got: " + searchingResult.size());
                    if (searchingResult.size() == 0){
                        Toast.makeText(mapActivity, "No valid parking places found...", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        }.execute();
    }

    private JsonArray searchParkingByRadius() {
        String serverResult = "";
        JsonArray response = null;
        if (MakeAPIConnectionForLocation()) {
            try {
                //Read the response
                Scanner inStream = new Scanner(connection.getInputStream());
                //read the input stream and store it as string
                while (inStream.hasNextLine()) {
                    serverResult += inStream.nextLine();
                }
                response = new JsonParser().parse(serverResult).getAsJsonArray();
            } catch(Exception e){
                e.printStackTrace();
            } finally{
                RemoveAPIConnection();
            }
        }
        return response;
    }

    public ArrayList<ParkingStatus> sortAndSelect(JsonArray rawData, final MapActivity mapActivity){
        ArrayList<ParkingStatus> result = new ArrayList<ParkingStatus>();
        for (int i = 0; i < rawData.size(); i++) {
            final JsonObject oneSpot = rawData.get(i).getAsJsonObject();
            int bayId = oneSpot.get("bay_id").getAsInt();
            final Double lat = oneSpot.get("lat").getAsDouble();
            final Double lon = oneSpot.get("lon").getAsDouble();
            final String status = oneSpot.get("status").getAsString();
            LatLng remote = mapActivity.remoteLatlng;
            Double distance = MyTools.getDistanceFromLatLonInMeter(remote.latitude, remote.longitude, lat, lon);
            ParkingStatus oneStatus = new ParkingStatus(bayId, lat, lon, status,MyTools.roundDouble(distance));
            if (oneStatus.getStatus().equals("Unoccupied")){
                result.add(oneStatus);
            }
        }

        Collections.sort(result, new Comparator<ParkingStatus>() {
            @Override
            public int compare(ParkingStatus o1, ParkingStatus o2) {
                return o1.getDistance().compareTo(o2.getDistance());
            }
        });

        if (result.size() >= 15){
            return new ArrayList<ParkingStatus>(result.subList(0,14));
        }else {
            return result;
        }
    }



//    public boolean MakeAPIConnectionForRestriction(String id){
//        int code = 0;
//        try {
//            URL url = new URL(BuildURLForRestriction(id));
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setReadTimeout(10000);
//            connection.setConnectTimeout(15000);
//            //set the connection method to GET
//            connection.setRequestMethod("GET");
//            //add http headers to set your response type to json
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("Accept", "application/json");
//            code = connection.getResponseCode();
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//        if(code == 200)
//            return true;
//        else
//            return false;
//    }


//
//    private String BuildURLForRestriction(String id){
//        StringBuilder builder = new StringBuilder(BASE_URL_PARKING_RESTRICTION);
//        builder.append("bayid = " + id);
//        return builder.toString();
//    }
//         ======== one parking spot in json format========
//            {
//              ":@computed_region_evbi_jbp8":"1",
//              "bay_id":"6227",
//              "lat":"-37.816737889952776",
//              "location":{ "type":"Point",
//                           "coordinates":[144.945818509531,-37.816737889953]
//                          },
//              "lon":"144.94581850953125",
//              "st_marker_id":"13332E",
//              "status":"Unoccupied"
//             }


//    public JsonArray FindParkingSpots(){
//
//        String serverResult = "";
//        JsonArray results = null;
//
//        if(MakeAPIConnection()) {
//            try {
//                //Read the response
//                Scanner inStream = new Scanner(connection.getInputStream());
//                //read the input stream and store it as string
//                while (inStream.hasNextLine()) {
//                    serverResult += inStream.nextLine();
//                }
//                results = new JsonParser().parse(serverResult).getAsJsonArray();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                RemoveAPIConnection();
//            }
//        }
//        return results;
//    }

//    private void filter() {
//        Log.d(TAG, "==============Filter==========now:" + searchingResult.size());
//        ArrayList<ParkingSpot> newList = new ArrayList<ParkingSpot>();
//        for (ParkingSpot oneParkingSpot : searchingResult) {
//            if (oneParkingSpot.chechWithSampleSpot(sampleParkingSpot)) {
//                newList.add(oneParkingSpot);
//            }
//        }
//        searchingResult = newList;
//        Log.d(TAG, "==============after==========got: " + searchingResult.size());
//    }

//    public void searchParkingPlacesWithFilter(final MapActivity mapActivity){
//
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                generateParkingSpotFrom(searchParkingByRadius());
//                filter();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void v) {
//                mapActivity.showParkingSpots(searchingResult);
//            }
//        }.execute();
//
//    }


}
