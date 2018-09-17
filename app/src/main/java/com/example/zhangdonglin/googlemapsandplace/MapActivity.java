package com.example.zhangdonglin.googlemapsandplace;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    //global vars
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final int PLACE_PICKER_REQUEST = 1;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker, mToilet, mParking, mNavigation, mReset, mBuilding, mClearSearch;
    private TextView tInfo;
    private BottomSheetBehavior toiletBottomSheetBehavior, parkingBottomSheetBehavior;
    private View toiletFilterSheet, parkingFilterSheet;
    private Spinner spToiletFilterWheelchair, spToiletFilterFemale, spToiletFilterMale, spToiletFilterDistance;
    private Spinner spParkingFilterDuration, spParkingFilterDisableOnly, spParkingFilterCharge, spParkingFilterDistance, spParkingAvailable;
    private Button btToiletSearch, btParkingSearch;


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Marker mMarker;

    private LatLng currentLatlng, destLatlng, remoteLatlng;
    private String remotePlaceTitle;
    private ParkingManager parkingManager = new ParkingManager();
    private ToiletManager toiletManager = new ToiletManager();
    private Building building = new Building();
    private MetroStationManager metroStationManager = new MetroStationManager();

    private String mode = "Driving";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mToilet = (ImageView) findViewById(R.id.toilet);
        mParking = (ImageView) findViewById(R.id.parking);
        mBuilding=(ImageView) findViewById(R.id.building);
        mClearSearch = (ImageView) findViewById(R.id.clean_search);
        mBuilding=(ImageView) findViewById(R.id.building);
        mNavigation = (ImageView) findViewById(R.id.ic_navigation);
//        mInfo = (ImageView) findViewById(R.id.ic_info);
//        tInfo = (TextView) findViewById(R.id.text_info);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();


        createToiletWidgetsInFilterSheet();
        createParkingWidgetsInFilterSheet();

        getLocationPermission();
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions.");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            mLocationPermissionsGranted = true;
            initMap();
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: intialising the map");
        //Toast.makeText(this, "initMap: intialising the map", Toast.LENGTH_SHORT).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready.", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            //Toast.makeText(this, "going to get current location", Toast.LENGTH_SHORT).show();
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ACCESS_FINE_LOCATION not premised", Toast.LENGTH_SHORT).show();
                return;
            }

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ACCESS_COARSE_LOCATION not premised", Toast.LENGTH_SHORT).show();
                return;
            }

            //mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    destLatlng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    Toast.makeText(MapActivity.this, "Destination set.", Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    showKeyPoint();
                    return true;
                }
            });
            init();
        } else {
            Toast.makeText(this, "Lack of permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed.");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted.");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void init(){

        Log.d(TAG, "init: initiating ");

        registerToiletBottomSheetWidgets();
        registerParkingBottomSheetWidgets();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("AU")
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, autocompleteFilter);
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }

                return false;
            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                cleanMap();
                MarkerOptions options = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                remoteLatlng = latLng;
                mMap.addMarker(options);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(remoteLatlng, mMap.getCameraPosition().zoom));
            }
        });


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mGps: clicked gps icon");
                Toast.makeText(MapActivity.this, "Moving to current location...", Toast.LENGTH_SHORT).show();
                getDeviceLocation();

            }
        });


        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destLatlng != null) {

                    setMetroStationManagerRadius(destLatlng, 2000);
                    metroStationManager.searchByLatRange(MapActivity.this);

                    Uri.Builder directionsBuilder = new Uri.Builder()
                            .scheme("https")
                            .authority("www.google.com")
                            .appendPath("maps")
                            .appendPath("dir")
                            .appendPath("")
                            .appendQueryParameter("api", "1")
                            .appendQueryParameter("destination", destLatlng.latitude + "," + destLatlng.longitude);
                    startActivity(new Intent(Intent.ACTION_VIEW, directionsBuilder.build()));
                }else{
                    Toast.makeText(MapActivity.this, "No Destination is chosen, click on a marker.", Toast.LENGTH_SHORT).show();
                }
//                if (destLatlng != null){
//                    cleanMap();
//                    showKeyPoint();
//                    Toast.makeText(MapActivity.this, "Navigating to destination, mode: " + mode, Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "mNavigation: clicked navigation icon");
//                    String url = getRequestedUrl(currentLatlng, destLatlng);
//                    Log.d(TAG, "mNavigation: clicked navigation icon, show currentLatlng " + currentLatlng.latitude + " || " + currentLatlng.longitude);
//                    Log.d(TAG, "mNavigation: clicked navigation icon, show destLatlng " + destLatlng.latitude + " || " + destLatlng.longitude);
//                    TaskRequestDirections findPath = new TaskRequestDirections();
//                    findPath.execute(url);
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destLatlng,13f));
//                }
//                else{
//                    Toast.makeText(MapActivity.this, "No Destination is Choosen.", Toast.LENGTH_SHORT).show();
//                }

            }
        });

//        mReset.setVisibility(View.VISIBLE);
//        mReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /*Intent intent = getIntent();
//                finish();
//                startActivity(intent);*/
//                mSearchText.clearListSelection();
//                mSearchText.setText("");
//                remoteLatlng = currentLatlng;
//                moveCamera(remoteLatlng, DEFAULT_ZOOM, "My Location");
//                cleanMap();
//            }
//        });

        mBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanMap();
                destLatlng = null;
                showKeyPoint();
                Toast.makeText(MapActivity.this, "Searching for building accessibility info...", Toast.LENGTH_SHORT).show();
                new AsyncTask<Void, Void, JsonArray>() {

                    @Override
                    protected JsonArray doInBackground(Void... voids) {
                        return findBuildingAccessibility(remoteLatlng, 50);
                    }

                    @Override
                    protected void onPostExecute(JsonArray doubles) {
                        showBuildingAccessibility(doubles);
                    }
                }.execute();
            }
        });

        mClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
                cleanMap();
            }
        });

//        tInfo.setVisibility(View.INVISIBLE);
//        mInfo.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if (tInfo.getVisibility() == View.INVISIBLE)
//                    tInfo.setVisibility(View.VISIBLE);
//                else
//                    tInfo.setVisibility(View.INVISIBLE);
//            }
//        });

        mBuilding.setVisibility(View.VISIBLE);
        mBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBuilding.setVisibility(View.VISIBLE);
                cleanMap();
                destLatlng = null;
                showKeyPoint();
                new AsyncTask<Void, Void, JsonArray>() {

                    @Override
                    protected JsonArray doInBackground(Void... voids) {
                        return findBuildingAccessibility(remoteLatlng, 50);
                    }

                    @Override
                    protected void onPostExecute(JsonArray doubles) {
                        showBuildingAccessibility(doubles);
                    }
                }.execute();
            }
        });

        Log.d(TAG, "init: initiating finished");
    }

    // ======================== functional code here =========================
    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    public void cleanMap(){
        mMap.clear();
    }

    private void showKeyPoint(){
        Log.d(TAG, "showKeyPoint: method called");
        if (currentLatlng != null){
            Log.d(TAG, "showKeyPoint: add current");
            mMap.addMarker(new MarkerOptions().position(currentLatlng));
        }

        if (remoteLatlng!= null && destLatlng == null){
            Log.d(TAG, "showKeyPoint: add remotemelbou");
            MarkerOptions markerOptions = new MarkerOptions().position(remoteLatlng).title(remotePlaceTitle);
            mMap.addMarker(markerOptions).showInfoWindow();
        }

        if (destLatlng != null){
            Log.d(TAG, "showKeyPoint: add destination");
            MarkerOptions markerOptions = new MarkerOptions().position(destLatlng).title("Destination");
            mMap.addMarker(markerOptions).showInfoWindow();
        }
    }


    // ======================== Toilet related code here =========================
    public void showToiletSpots(ArrayList<Toilet> toilets) {
        Log.d(TAG, "method: showSpots called ");

        if (toilets.size() != 0) {
            for (int i = 0; i < toilets.size(); i++) {
                Toilet oneToilet = toilets.get(i);
                Double lat = oneToilet.getLat();
                Double lon = oneToilet.getLon();
                LatLng latlng = new LatLng(lat, lon);
                MarkerOptions options = new MarkerOptions()
                        .position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_toilet_icon));
                mMap.addMarker(options);
            }
        }else{
            Log.d(TAG, "method: showSpots : no spot found ");
            Toast.makeText(MapActivity.this, "No Nearby Toilets found", Toast.LENGTH_SHORT).show();
        }
    }

    private void createToiletWidgetsInFilterSheet(){
        toiletFilterSheet = findViewById(R.id.toilet_filter_bottomsheet);
        toiletBottomSheetBehavior = BottomSheetBehavior.from(toiletFilterSheet);
        spToiletFilterWheelchair = (Spinner) findViewById(R.id.spin_toilet_wheelchair);
        spToiletFilterFemale = (Spinner) findViewById(R.id.spin_toilet_female);
        spToiletFilterMale = (Spinner) findViewById(R.id.spin_toilet_male);
        spToiletFilterDistance = (Spinner) findViewById(R.id.spin_toilet_distance);
        btToiletSearch = (Button) findViewById(R.id.button_toilet_search);
    }

    public void registerToiletBottomSheetWidgets(){
        spToiletFilterWheelchair.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spToiletFilterWheelchair.getSelectedItem().toString().toLowerCase().equals("yes")) {
                    toiletManager.getSampleToilet().setWheelchair("yes");
                }else if (spToiletFilterWheelchair.getSelectedItem().toString().toLowerCase().equals("no")) {
                    toiletManager.getSampleToilet().setWheelchair("no");
                }else{
                    toiletManager.getSampleToilet().setWheelchair("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spToiletFilterFemale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spToiletFilterFemale.getSelectedItem().toString().toLowerCase().equals("yes")) {
                    toiletManager.getSampleToilet().setFemale("yes");
                }else if (spToiletFilterFemale.getSelectedItem().toString().toLowerCase().equals("no")) {
                    toiletManager.getSampleToilet().setFemale("no");
                }else {
                    toiletManager.getSampleToilet().setFemale("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spToiletFilterMale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spToiletFilterMale.getSelectedItem().toString().toLowerCase().equals("yes")) {
                    toiletManager.getSampleToilet().setMale("yes");
                }else if (spToiletFilterMale.getSelectedItem().toString().toLowerCase().equals("no")) {
                    toiletManager.getSampleToilet().setMale("no");
                }else{
                    toiletManager.getSampleToilet().setMale("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spToiletFilterDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spToiletFilterDistance.getSelectedItem().toString().toLowerCase().equals("1000m")) {
                    toiletManager.setDistance(1000);
                }else if (spToiletFilterDistance.getSelectedItem().toString().toLowerCase().equals("600m")) {
                    toiletManager.setDistance(600);
                }else{
                    toiletManager.setDistance(300);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btToiletSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toiletBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                cleanMap();
                destLatlng = null;
                showKeyPoint();
                setToiletManagerRadius(remoteLatlng,toiletManager.getDistance());
                Toast.makeText(MapActivity.this, "Searching for nearby public toilets...", Toast.LENGTH_SHORT).show();
                toiletManager.searchByLatRange(MapActivity.this);
            }
        });

        mToilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toiletBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

    }

    private void setToiletManagerRadius(LatLng latLng, double radiusinMeters){

        Log.d(TAG, "method: setToiletManagerRadius called ");
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters*0.7);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +

        toiletManager.setSouth(southwestCorner.longitude);
        toiletManager.setNorth(northeastCorner.longitude);
        toiletManager.setEast(northeastCorner.latitude);
        toiletManager.setWest(southwestCorner.latitude);
    }


    // ======================== Parking related code here ==========================

    public void showParkingSpot(ParkingSpot oneSpot){
        Log.d(TAG, "method: showSpots called ");

        String bayId = oneSpot.getBayID();
        Double lat = oneSpot.getLat();
        Double lon = oneSpot.getLon();
        String status = oneSpot.getStatus();
        LatLng latlng = new LatLng(lat, lon);
        Log.d(TAG, "method: showSpots : " + oneSpot.toString());
        MarkerOptions options = null;
        if (status.equals("Unoccupied")){
            options = new MarkerOptions().title(bayId).position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_icon));
        }else{
            options = new MarkerOptions().title(bayId).position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_redicon));;
        }
        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(remoteLatlng, 16f));
    }

    private void createParkingWidgetsInFilterSheet(){
        parkingFilterSheet = findViewById(R.id.parking_filter_bottomsheet);
        parkingBottomSheetBehavior = BottomSheetBehavior.from(parkingFilterSheet);
        spParkingFilterDuration = (Spinner) findViewById(R.id.spin_parking_duration);
        spParkingFilterDistance = (Spinner) findViewById(R.id.spin_parking_distance);
        spParkingFilterCharge = (Spinner) findViewById(R.id.spin_parking_pay);
        spParkingAvailable = (Spinner) findViewById(R.id.spin_parking_available);
        spParkingFilterDisableOnly = (Spinner) findViewById(R.id.spin_parking_disableOnly);
        btParkingSearch = (Button) findViewById(R.id.button_parking_search);
    }

    private void registerParkingBottomSheetWidgets(){
        spParkingFilterCharge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spParkingFilterCharge.getSelectedItem().toString().toLowerCase().equals("yes")) {
                    parkingManager.getSampleParkingSpot().setNeedToPay("yes");
                }else if (spParkingFilterCharge.getSelectedItem().toString().toLowerCase().equals("no")) {
                    parkingManager.getSampleParkingSpot().setNeedToPay("no");
                }else{
                    parkingManager.getSampleParkingSpot().setNeedToPay("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spParkingFilterDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spParkingFilterDistance.getSelectedItem().toString().toLowerCase().equals("1000m")) {
                    parkingManager.setDistance(1000);
                }else if (spParkingFilterDistance.getSelectedItem().toString().toLowerCase().equals("600m")) {
                    parkingManager.setDistance(600);
                }else {
                    parkingManager.setDistance(300);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spParkingFilterDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spParkingFilterDuration.getSelectedItem().toString().toLowerCase().equals(">120 mins")) {
                    parkingManager.getSampleParkingSpot().setNormalDuration(120);
                }else if (spParkingFilterDuration.getSelectedItem().toString().toLowerCase().equals(">60 mins")) {
                    parkingManager.getSampleParkingSpot().setNormalDuration(60);
                }else{
                    parkingManager.getSampleParkingSpot().setNormalDuration(30);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spParkingFilterDisableOnly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spParkingFilterDisableOnly.getSelectedItem().toString().toLowerCase().equals("yes")) {
                    parkingManager.getSampleParkingSpot().setDisableOnly("yes");
                }else if (spParkingFilterDisableOnly.getSelectedItem().toString().toLowerCase().equals("no")) {
                    parkingManager.getSampleParkingSpot().setDisableOnly("no");
                }else{
                    parkingManager.getSampleParkingSpot().setDisableOnly("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spParkingAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spParkingAvailable.getSelectedItem().toString().toLowerCase().equals("yes")) {
                    parkingManager.getSampleParkingSpot().setStatus("Unoccupied");
                }else if (spParkingAvailable.getSelectedItem().toString().toLowerCase().equals("no")) {
                    parkingManager.getSampleParkingSpot().setStatus("Present");
                }else{
                    parkingManager.getSampleParkingSpot().setDisableOnly("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btParkingSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parkingBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    parkingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    parkingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                cleanMap();
                destLatlng = null;
                showKeyPoint();
                Toast.makeText(MapActivity.this, "Searching for nearby parking places...", Toast.LENGTH_SHORT).show();
                setParkingManagerRadius(remoteLatlng,parkingManager.getDistance());
                parkingManager.searchParkingAndFilterAndShow(MapActivity.this);
            }
        });

        mParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parkingBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    parkingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    parkingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void setParkingManagerRadius( LatLng latLng, double radiusinMeters){

        Log.d(TAG, "nethod: setParkingManagerRadius called ");
        JsonArray results = null;
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +
        parkingManager.setSouth(southwestCorner.longitude);
        parkingManager.setNorth(northeastCorner.longitude);
        parkingManager.setEast(northeastCorner.latitude);
        parkingManager.setWest(southwestCorner.latitude);

    }


    // ======================== Metro related code here ==========================

    private void setMetroStationManagerRadius(LatLng latLng, double radiusinMeters){

        Log.d(TAG, "method: setMetroStationManagerRadius called ");
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters*0.7);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +

        metroStationManager.setSouth(southwestCorner.longitude);
        metroStationManager.setNorth(northeastCorner.longitude);
        metroStationManager.setEast(northeastCorner.latitude);
        metroStationManager.setWest(southwestCorner.latitude);
    }












    // ======================== Building related code here ==========================

    private void showBuildingAccessibility(JsonArray accessibility){
        Log.d(TAG, "method: showSpots called ");
        if (accessibility.size() != 0) {
            for (int i = 0; i < accessibility.size(); i++) {
                JsonObject oneSpot = accessibility.get(i).getAsJsonObject();
                Double lat = oneSpot.get("y_coordinate").getAsDouble();
                Double lon = oneSpot.get("x_coordinate").getAsDouble();
                String rating = oneSpot.get("accessibility_rating").toString();
                String level = oneSpot.get("accessibility_type").toString();
                LatLng latlng = new LatLng(lat, lon);
                MarkerOptions options = null;
                if (rating.equals("\"0\"")){
                    options = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_red));
                }else if(rating.equals("\"3\"")){
                    options = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_green));
                }else if(rating.equals("\"1\"")){
                    options = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_orange));
                }else if(rating.equals("\"2\"")){
                    options = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_lightgreen));
                }
                cleanMap();
                mMap.addMarker(options);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(remoteLatlng, 16f));
            }
        }else{
            Log.d(TAG, "method: showSpots : no data found ");
            Toast.makeText(MapActivity.this, "No information on the building", Toast.LENGTH_SHORT).show();
        }
    }

    //South Longitude is negative，North Longitude is positive; East Latitude is positive，West Latitude is negative.

    private JsonArray findBuildingAccessibility(LatLng latLng, double radiusinMeters){
        Log.d(TAG, "nethod: findBuildingAccessibility called ");
        JsonArray results = null;
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +
        building.setSouth(southwestCorner.longitude);
        building.setNorth(northeastCorner.longitude);
        building.setEast(northeastCorner.latitude);
        building.setWest(southwestCorner.latitude);
        return building.FindBuildingAccessibility();
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed google api client connect", Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (requestCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            }
        }
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geoLocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException " + e.getMessage());
        }

        if (list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "getLocate: Found a location." + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device's current locatioin");
        //Toast.makeText(MapActivity.this, "getDeviceLocation: getting the device's current locatioin", Toast.LENGTH_SHORT).show();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        try{
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            if (currentLocation == null) {
                                Log.d(TAG, "onComplete: task result not found.");
                                Toast.makeText(MapActivity.this, "unable to find current location", Toast.LENGTH_SHORT).show();
                                currentLatlng = new LatLng(-37.8179,144.959);
                            }else{
                                currentLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                Log.d(TAG, "onComplete: currentLatlng set!");
                            }
                            moveCamera(currentLatlng, DEFAULT_ZOOM, "My Location");
                            remotePlaceTitle = "";
                        }else {
                            Log.d(TAG, "onComplete: current location is null.");
                            Toast.makeText(MapActivity.this, "unable to find current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private Marker moveCamera(LatLng latlng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latlng.latitude + ", lng:" + latlng.longitude);

        Marker result = null;
        remoteLatlng = latlng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        if (!title.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions().position(latlng).title(title).snippet("");
            result = mMap.addMarker(markerOptions);
            result.showInfoWindow();
        }
        hideSoftKeyboard();
        return result;
    }

    private void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String getRequestedUrl(LatLng from, LatLng to) {
        Log.d(TAG, "method: getRequestedUrl is called:");
        String string_origin = "origin=" + from.latitude + "," + from.longitude;
        String string_destination = "destination=" + to.latitude + "," + to.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = string_origin+"&"+string_destination+"&"+sensor+"&"+mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param; // + "&key=AIzaSyCcLw8zgIjhCqC1QeCBXv7NVGAUFuChKq8";
        return url;
    }

    private String requesDirection(String reqUrl) throws IOException {
        Log.d(TAG, "method: requesDirection is called: " + reqUrl);
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStream.close();

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        Log.d(TAG, "method: requesDirection get: " + responseString);
        return responseString;
    }

    public List<List<LatLng>> getLatlng(JSONObject jObject) {
        List<List<LatLng>> routes = new ArrayList<List<LatLng>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        try {
            jRoutes = jObject.getJSONArray("routes");

            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<LatLng>();

                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        path = PolyUtil.decode(polyline);
                        routes.add(path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return routes;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "TaskRequsetDirections: method start");
            String responseString = "";
            try{
                responseString = requesDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<LatLng>>> {

        @Override
        protected List<List<LatLng>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<LatLng>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                routes = getLatlng(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<LatLng>> lists) {
            Log.d(TAG, "Start Drawing Path: method start");

            PolylineOptions polylineOptions = null;
            for (List<LatLng> path : lists){
                polylineOptions = new PolylineOptions();
                polylineOptions.addAll(path);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
                if (polylineOptions != null) {
                    mMap.addPolyline(polylineOptions);
                    Log.d(TAG, "Drawing Path: path found! ");
                }else {
                    Toast.makeText(MapActivity.this, "Direction not found!", Toast.LENGTH_SHORT).show();
                }
            }


            Log.d(TAG, "Finish Drawing Path: method finished");
        }
    }


    /*
        -------- google palces API actocomplete suggestions --------
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully.");
                places.release();
                return;
            }
            final Place place = places.get(0);
            Log.d(TAG, "onResult: Place details:" + place.getAttributions());

            cleanMap();
            remotePlaceTitle = new String(place.getName().toString());
            remoteLatlng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            moveCamera(place.getLatLng(), DEFAULT_ZOOM, remotePlaceTitle);
            places.release();
        }
    };

}
