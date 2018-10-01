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


public class ToiletManager {
    //private static final String BASE_URL_TOILET = "https://data.melbourne.vic.gov.au/resource/dsec-5y6t.json?$where=";
    private static final String TAG = "ToiletManager";
    //private HttpURLConnection connection;
    private Double south;
    private Double north;
    private Double east;
    private Double west;
    public ArrayList<Toilet> resultList;
    public ArrayList<Marker> markerList;
    private Toilet sampleToilet;
    public int distance;

    //firebase var
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;


    public ToiletManager(){
        sampleToilet = new Toilet();
        //connection = null;
        resultList = new ArrayList<Toilet>();
        markerList = new ArrayList<Marker>();
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

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Toilet getSampleToilet() {
        return sampleToilet;
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
                    Toilet oneToilet = o.getValue(Toilet.class);
                    LatLng remote = mapActivity.remoteLatlng;
                    Double distance = MyTools.getDistanceFromLatLonInMeter(remote.latitude, remote.longitude, oneToilet.getLat(), oneToilet.getLon());
                    oneToilet.setDistance(MyTools.roundDouble(distance));
                    resultList.add(oneToilet);
                }
                Log.d(TAG, "==========Search after LatRange Query===========got: " + resultList.size());
                filter();
                mapActivity.showToiletSpots(resultList);

                ArrayList<Object> objectArrayList = new ArrayList<>();
                for (Toilet oneToilet: resultList){
                    objectArrayList.add((Object) oneToilet);
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
        ArrayList<Toilet> newList = new ArrayList<Toilet>();
        for (Toilet oneToilet : resultList) {
            if (oneToilet.checkLng(south, north) && oneToilet.checkWithSample(sampleToilet)) {
                newList.add(oneToilet);
            }
        }
        resultList = newList;
        Log.d(TAG, "==============Search after Filter==========got: " + resultList.size());
    }

}
