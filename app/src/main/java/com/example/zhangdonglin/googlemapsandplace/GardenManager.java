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


public class GardenManager {
    private static final String TAG = "GardenManager";
    private Double south;
    private Double north;
    private Double east;
    private Double west;
    public ArrayList<Garden> resultList;
    public ArrayList<Marker> markerList;
    private Garden sampleGarden;
    public int distance;

    //firebase var
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;


    public GardenManager(){
        sampleGarden = new Garden();
        resultList = new ArrayList<Garden>();
        markerList = new ArrayList<Marker>();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference().child("garden");
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

    public Garden getSampleGarden() {
        return sampleGarden;
    }

    public void searchByLatRange(final MapActivity mapActivity) {
        resultList.clear();
        markerList.clear();
        Log.d(TAG, "==============Search by Range First==========");
        Query q = myRef.orderByChild("lat").startAt(east + "").endAt(west + "");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot o : dataSnapshot.getChildren()) {
                    Garden oneGarden = o.getValue(Garden.class);
                    LatLng remote = mapActivity.remoteLatlng;
                    Double distance = MyTools.getDistanceFromLatLonInMeter(remote.latitude, remote.longitude, oneGarden.getLat(), oneGarden.getLon());
                    oneGarden.setDistance(MyTools.roundDouble(distance));
                    resultList.add(oneGarden);
                }
                Log.d(TAG, "==========Search after LatRange Query===========got: " + resultList.size());
                filter();
                mapActivity.showGardenSpots(resultList);

                ArrayList<Object> objectArrayList = new ArrayList<>();
                for (Garden oneGarden: resultList){
                    objectArrayList.add((Object) oneGarden);
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
        ArrayList<Garden> newList = new ArrayList<Garden>();
        for (Garden oneGarden : resultList) {
            if (oneGarden.checkLng(south, north) && oneGarden.checkWithSample(sampleGarden)) {
                newList.add(oneGarden);
            }
        }
        resultList = newList;
        Log.d(TAG, "==============Search after Filter==========got: " + resultList.size());
    }

}
