package com.example.zhangdonglin.googlemapsandplace;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MetroStationManager {
    private static final String TAG = "MetroStationManager";
    private Double south;
    private Double north;
    private Double east;
    private Double west;
    public ArrayList<MetroStation> resultList;
    private Toilet sampleStation;
    public int distance;

    //firebase var
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;


    public MetroStationManager(){
        sampleStation = new Toilet();
        //connection = null;
        resultList = new ArrayList<MetroStation>();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference().child("metro_stations").child("properties");
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

    public Toilet getSampleStation() {
        return sampleStation;
    }

    public void searchByLatRange(final MapActivity mapActivity) {
        resultList.clear();
        Log.d(TAG, "==============Search by Range First==========");
        //Query q = myRef.child("properties").child("lift").orderByChild("1").startAt(east + "").endAt(west + "");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "===got: " + dataSnapshot.getChildrenCount());
                int count = 0;
                for (DataSnapshot o : dataSnapshot.getChildren()) {
                    MetroStation oneStation = o.getValue(MetroStation.class);
                    Log.d(TAG, "===got: " + oneStation.toString());

                    resultList.add(oneStation);
                }
                Log.d(TAG, "==========Search after LatRange Query===========got: " + resultList.size());
                //mapActivity.showToiletSpots(resultList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
