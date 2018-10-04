package com.example.zhangdonglin.googlemapsandplace.Controller;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.zhangdonglin.googlemapsandplace.View.MapActivity;
import com.example.zhangdonglin.googlemapsandplace.MyTools;
import com.example.zhangdonglin.googlemapsandplace.Module.MetroStation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MetroStationManager {
    private static final String TAG = "MetroStationManager";
    private Double south;
    private Double north;
    private Double east;
    private Double west;
    public ArrayList<MetroStation> resultList;
    public ArrayList<Marker> markerList;
    private MetroStation sampleStation;
    public int distance;

    //firebase var
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myRef;


    public MetroStationManager(){
        sampleStation = new MetroStation();
        distance = 1500;
        resultList = new ArrayList<MetroStation>();
        markerList = new ArrayList<Marker>();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myFirebaseDatabase.getReference().child("metro_stations");
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

    public MetroStation getSampleStation() {
        return sampleStation;
    }

    public void searchByLatRange(final MapActivity mapActivity) {
        resultList.clear();
        markerList.clear();
        Log.d(TAG, "==============Search by Range Start==========");
        Query q = myRef.orderByChild("lat").startAt(east+"").endAt(west+"");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "===got: " + dataSnapshot.getChildrenCount());
                int count = 0;
                for (DataSnapshot o : dataSnapshot.getChildren()) {
                    MetroStation oneStation = o.getValue(MetroStation.class);
                    Log.d(TAG, "===got: " + oneStation.toString());
                    if (oneStation.getLon() >= south && oneStation.getLon() <= north){
                        LatLng remote = mapActivity.remoteLatlng;
                        Double distance = MyTools.getDistanceFromLatLonInMeter(remote.latitude, remote.longitude, oneStation.getLat(), oneStation.getLon());
                        oneStation.setDistance(MyTools.roundDouble(distance));
                        resultList.add(oneStation);
                        Log.d(TAG, "===got a valid one. ");
                    }else{
                        Log.d(TAG, "===drop one invalid point.");
                    }
                }
                Log.d(TAG, "==========Search after LatRange Query===========got: " + resultList.size());
                filter();
                mapActivity.showMetroStations(resultList);
                Collections.sort(resultList, new Comparator<MetroStation>() {
                    @Override
                    public int compare(MetroStation o1, MetroStation o2) {
                        return o1.getDistance().compareTo(o2.getDistance());
                    }
                });
                if (resultList.size() >= 15){
                    resultList = new ArrayList<MetroStation>(resultList.subList(0,14));
                }

                ArrayList<Object> objectArrayList = new ArrayList<>();
                for (MetroStation oneMetro: resultList){
                    objectArrayList.add((Object) oneMetro);
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
        ArrayList<MetroStation> newList = new ArrayList<MetroStation>();
        for (MetroStation oneStation : resultList) {
            if (oneStation.checkWithSample(sampleStation)) {
                newList.add(oneStation);
            }
        }
        resultList = newList;
        Log.d(TAG, "==============Search after Filter==========got: " + resultList.size());
    }
}
