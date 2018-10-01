package com.example.zhangdonglin.googlemapsandplace;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BuildingSpotManager {
    //private static final String BASE_URL_BuildingSpot = "https://data.melbourne.vic.gov.au/resource/dsec-5y6t.json?$where=";
    private static final String TAG = "BuildingSpotManager";
    //private HttpURLConnection connection;
    private Double south;
    private Double north;
    private Double east;
    private Double west;
    public ArrayList<BuildingSpot> resultList;
    public ArrayList<Marker> markerList;
    private BuildingSpot sampleBuildingSpot;
    public int distance;

    //firebase var
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;


    public BuildingSpotManager(){
        sampleBuildingSpot = new BuildingSpot();
        //connection = null;
        resultList = new ArrayList<BuildingSpot>();
        markerList = new ArrayList<Marker>();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference().child("building_info");
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

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public BuildingSpot getSampleBuildingSpot() {
        return sampleBuildingSpot;
    }

    public void searchByLatRange(final MapActivity mapActivity) {
        resultList.clear();
        markerList.clear();
        Log.d(TAG, "==============Search by Range First==========");
        Query q = myRef.orderByChild("y_coordinate").startAt(east + "").endAt(west + "");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot o : dataSnapshot.getChildren()) {
                    BuildingSpot oneBuildingSpot = o.getValue(BuildingSpot.class);
                    LatLng remote = mapActivity.remoteLatlng;
                    Log.d(TAG, "point Lat: " + oneBuildingSpot.getY_coordinate());
                    Double distance = MyTools.getDistanceFromLatLonInMeter(remote.latitude, remote.longitude, oneBuildingSpot.getY_coordinate(), oneBuildingSpot.getX_coordinate());
                    oneBuildingSpot.setDistance(MyTools.roundDouble(distance));
                    resultList.add(oneBuildingSpot);
                }
                Log.d(TAG, "==========Search after LatRange Query===========got: " + resultList.size());
                filter();
                mapActivity.showBuildingSpots(resultList);

                ArrayList<Object> objectArrayList = new ArrayList<>();
                for (BuildingSpot oneBuildingSpot: resultList){
                    objectArrayList.add((Object) oneBuildingSpot);
                }
                mapActivity.showSearchingResultList(objectArrayList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void filter() {
        Log.d(TAG, "==============Search by Filter then==========now:" + resultList.size());
        ArrayList<BuildingSpot> newList = new ArrayList<BuildingSpot>();
        for (BuildingSpot oneBuildingSpot : resultList) {
            if (oneBuildingSpot.checkLng(south, north) && oneBuildingSpot.checkWithSample(sampleBuildingSpot)) {
                newList.add(oneBuildingSpot);
            }else{
                Log.d(TAG, "point not valid: " + oneBuildingSpot.getX_coordinate());
            }
        }
        resultList = newList;
        Log.d(TAG, "==============Search after Filter==========got: " + resultList.size());
    }

}
