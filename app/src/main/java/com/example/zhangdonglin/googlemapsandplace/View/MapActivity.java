package com.example.zhangdonglin.googlemapsandplace.View;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangdonglin.googlemapsandplace.MyTools;
import com.example.zhangdonglin.googlemapsandplace.R;
import com.example.zhangdonglin.googlemapsandplace.Controller.BuildingSpotManager;
import com.example.zhangdonglin.googlemapsandplace.Controller.GardenManager;
import com.example.zhangdonglin.googlemapsandplace.Controller.MetroStationManager;
import com.example.zhangdonglin.googlemapsandplace.Controller.ParkingSpotManager;
import com.example.zhangdonglin.googlemapsandplace.Controller.ToiletManager;
import com.example.zhangdonglin.googlemapsandplace.Module.BuildingSpot;
import com.example.zhangdonglin.googlemapsandplace.Module.Garden;
import com.example.zhangdonglin.googlemapsandplace.Module.MetroStation;
import com.example.zhangdonglin.googlemapsandplace.Module.ParkingSpot;
import com.example.zhangdonglin.googlemapsandplace.Module.Toilet;
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
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    //global vars
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-38, 144), new LatLng(-37, 145));
    //    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final int PLACE_PICKER_REQUEST = 1;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker, mToilet, mParking, mNavigation, mReset, mBuilding, mClearSearch, mMetro, mGarden;
    private TextView tInfo;
    private BottomSheetBehavior toiletBottomSheetBehavior, buildingBottomSheetBehavior, gardenBottomSheetBehavior, parkingBottomSheetBehavior, metroBottomSheetBehavior, bottomSheetBehavior;
    private View toiletFilterSheet, buildingFilterSheet, gardenFilterSheet, parkingFilterSheet, metroFilterSheet, bottomSheet;
    private View toiletFilterHandle, buildingFilterHandle, gardenFilterHandle, parkingFilterHandle, metroFilterHandle, bottomSheetHandle;
    private Spinner spToiletFilterWheelchair, spToiletFilterFemale, spToiletFilterMale, spToiletFilterDistance;
    private Spinner spParkingFilterDuration, spParkingFilterDisableOnly, spParkingFilterCharge, spParkingFilterDistance, spParkingAvailable;
    private Spinner spMetroLoop, spMetroLift, spMetroPid, spMetroDistance;
    private Spinner spGardenFilterDistance;
    private Spinner spBuildingFilterDistance;
    private Button btToiletSearch, btParkingSearch, btMetroSearch, btGardenSearch, btBuildingSearch;
    private CheckBox cbToiletWheelchair, cbMetroLift, cbMetroRamp;

    //buildingDialog
    public Dialog buildingReportDialog, buildingInfoDialog, buildingRatingInfoDialog;
    public CheckBox cbFeature1, cbFeature2, cbFeature3, cbFeature4;
    public RatingBar starRatingBar;
    public Button btReportSubmit, btInfoMakeReport, btRatingInfoClose;
    public TextView tvFeature1Count, tvFeature2Count, tvFeature3Count, tvFeature4Count, tvAverageRating;

    //redirectDialog
    public Dialog redirectConfirmDialog;
    public Button btRedirectCancel, btRedirectConfirm;

    //result list
    public ListView resultList;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    public GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    public LatLng currentLatlng, destLatlng, remoteLatlng;
    public ParkingSpotManager parkingManager = new ParkingSpotManager();
    public ToiletManager toiletManager = new ToiletManager();
    public MetroStationManager metroStationManager = new MetroStationManager();
    public GardenManager gardenManager = new GardenManager();
    public BuildingSpotManager buildingManager = new BuildingSpotManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mToilet = (ImageView) findViewById(R.id.toilet);
        mParking = (ImageView) findViewById(R.id.parking);
        mBuilding =(ImageView) findViewById(R.id.building);
        mClearSearch = (ImageView) findViewById(R.id.clean_search);
        mBuilding = (ImageView) findViewById(R.id.building);
        mGarden = (ImageView) findViewById(R.id.garden);
        mNavigation = (ImageView) findViewById(R.id.ic_navigation);
//        mInfo = (ImageView) findViewById(R.id.ic_info);
//        tInfo = (TextView) findViewById(R.id.text_info);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetHandle = findViewById(R.id.bottom_sheet_handle);

        createToiletWidgets();
        createParkingWidgetst();
        createMetroWidgets();
        createGardenWidgets();
        createBuildingSpotWidgets();

        //creat redirect dialog
        redirectConfirmDialog = new Dialog(MapActivity.this);
        redirectConfirmDialog.setContentView(R.layout.dialog_redirect_to_google);
        redirectConfirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btRedirectCancel = (Button) redirectConfirmDialog.findViewById(R.id.bt_redirect_to_google_cancel);
        btRedirectConfirm = (Button) redirectConfirmDialog.findViewById(R.id.bt_redirect_to_google_confirm);

        getLocationPermission();
    }

    private void init(){

        Log.d(TAG, "init: initiating ");

        registerToiletBottomSheetWidgets();
        registerParkingBottomSheetWidgets();
        registerMetroBottomSheetWidgets();
        registerGardenBottomSheetWidgets();
        registerBuildingSpotBottomSheetWidgets();

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                closeBottomSheet();
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
                navigationToGoogle();
            }
        });

        mClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
                cleanMap();
            }
        });

        bottomSheetHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        btRedirectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectConfirmDialog.dismiss();
            }
        });
        btRedirectConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToGoogle();
            }
        });

        Log.d(TAG, "init: initiating finished");
    }

    private void initMap() {
        Log.d(TAG, "initMap: intialising the map");
        //Toast.makeText(this, "initMap: intialising the map", Toast.LENGTH_SHORT).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
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
            mMap.setInfoWindowAdapter(new SpotInfoWindowAdapter(MapActivity.this));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                destLatlng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                Toast.makeText(MapActivity.this, "Navigation By Clicking Top Right Button", Toast.LENGTH_SHORT).show();
                //mMap.clear();
                marker.showInfoWindow();
                closeBottomSheet();
                //showKeyPoint();
                return true;
                }
            });

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                cleanMap();
                MarkerOptions options = new MarkerOptions()
                        .title("Target Location")
                        .snippet("You are here")
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                remoteLatlng = latLng;
                Log.d(TAG, "current location: lat: " + latLng.latitude + ", " + latLng.longitude);

                mMap.addMarker(options);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(remoteLatlng, mMap.getCameraPosition().zoom));
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    destLatlng = marker.getPosition();
                    navigationToGoogle();
                }
            });

            init();
        } else {
            Toast.makeText(this, "Lack of permission", Toast.LENGTH_SHORT).show();
        }
    }

    //South Longitude is negative，North Longitude is positive;
    //East Latitude is positive，West Latitude is negative.
    // ======================== Toilet related code here =========================
    private void createToiletWidgets(){
        toiletFilterSheet = findViewById(R.id.toilet_filter_bottomsheet);
        toiletBottomSheetBehavior = BottomSheetBehavior.from(toiletFilterSheet);
        toiletFilterHandle = findViewById(R.id.toilet_filter_handle);
        //spToiletFilterWheelchair = (Spinner) findViewById(R.id.spin_toilet_wheelchair);
//        spToiletFilterFemale = (Spinner) findViewById(R.id.spin_toilet_female);
//        spToiletFilterMale = (Spinner) findViewById(R.id.spin_toilet_male);
        spToiletFilterDistance = (Spinner) findViewById(R.id.spin_toilet_distance);
        btToiletSearch = (Button) findViewById(R.id.button_toilet_search);
        cbToiletWheelchair = (CheckBox) findViewById(R.id.cb_toilet_wheelchair);
    }

    public void registerToiletBottomSheetWidgets(){
//        spToiletFilterWheelchair.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if (spToiletFilterWheelchair.getSelectedItem().toString().toLowerCase().equals("yes")) {
//                    toiletManager.getSampleToilet().setWheelchair("yes");
//                }else if (spToiletFilterWheelchair.getSelectedItem().toString().toLowerCase().equals("no")) {
//                    toiletManager.getSampleToilet().setWheelchair("no");
//                }else{
//                    toiletManager.getSampleToilet().setWheelchair("");
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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
            toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (cbToiletWheelchair.isChecked()) {
                toiletManager.getSampleToilet().setWheelchair("yes");
            }else{
                toiletManager.getSampleToilet().setWheelchair("");
            }
            cleanMap();
            resetBackgroundForIcons();
            mToilet.setBackgroundResource(R.drawable.background_circle_warning);
            destLatlng = null;
            checkRemoteLocation();
            setToiletManagerRadius(remoteLatlng,toiletManager.getDistance());
            Toast.makeText(MapActivity.this, "Searching for nearby public toilets...", Toast.LENGTH_SHORT).show();
            toiletManager.searchByLatRange(MapActivity.this);
            }
        });

        mToilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet();
                toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        toiletFilterHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void setToiletManagerRadius(LatLng latLng, double radiusinMeters){

        Log.d(TAG, "method: setToiletManagerRadius called ");
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +

        toiletManager.setSouth(southwestCorner.longitude);
        toiletManager.setNorth(northeastCorner.longitude);
        toiletManager.setEast(northeastCorner.latitude);
        toiletManager.setWest(southwestCorner.latitude);
    }

    public void showToiletSpots(ArrayList<Toilet> toilets) {
        Log.d(TAG, "method: showSpots called ");
        showRemotePoint();
        if (toilets.size() != 0) {
            for (int i = 0; i < toilets.size(); i++) {
                Toilet oneToilet = toilets.get(i);
                Double lat = oneToilet.getLat();
                Double lon = oneToilet.getLon();
                LatLng latlng = new LatLng(lat, lon);
                Double distance = oneToilet.getDistance();
                String distanceString = "";
                if (distance >= 1000){
                    distanceString = "Distance:  " + MyTools.roundDouble(distance/1000) + " km ";
                }else{
                    distanceString = "Distance:  " +  MyTools.roundDoubleWithOutDecimal(distance) + " m ";
                }
                MarkerOptions options = new MarkerOptions()
                        .position(latlng).title("Public Toilet")
                        .snippet(distanceString)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_toilet));
                toiletManager.markerList.add(mMap.addMarker(options));
            }
        }else{
            Log.d(TAG, "method: showSpots : no spot found ");
            Toast.makeText(MapActivity.this, "No Nearby Toilets found", Toast.LENGTH_SHORT).show();
        }
    }

    // ======================== Garden related code here =========================
    private void createGardenWidgets(){
        gardenFilterSheet = findViewById(R.id.garden_filter_bottomsheet);
        gardenBottomSheetBehavior = BottomSheetBehavior.from(gardenFilterSheet);
        gardenFilterHandle = findViewById(R.id.garden_filter_handle);
//        spToiletFilterFemale = (Spinner) findViewById(R.id.spin_toilet_female);
//        spToiletFilterMale = (Spinner) findViewById(R.id.spin_toilet_male);
        spGardenFilterDistance = (Spinner) findViewById(R.id.spin_garden_distance);
        btGardenSearch = (Button) findViewById(R.id.button_garden_search);
    }

    public void registerGardenBottomSheetWidgets(){
        spGardenFilterDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spGardenFilterDistance.getSelectedItem().toString().toLowerCase().equals("1000m")) {
                    gardenManager.setDistance(1000);
                }else if (spGardenFilterDistance.getSelectedItem().toString().toLowerCase().equals("600m")) {
                    gardenManager.setDistance(600);
                }else{
                    gardenManager.setDistance(300);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btGardenSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gardenBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    gardenBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    gardenBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                cleanMap();
                resetBackgroundForIcons();
                mGarden.setBackgroundResource(R.drawable.background_circle_warning);
                destLatlng = null;
                checkRemoteLocation();
                setGardenManagerRadius(remoteLatlng,gardenManager.getDistance());
                Toast.makeText(MapActivity.this, "Searching for nearby public parks...", Toast.LENGTH_SHORT).show();
                gardenManager.searchByLatRange(MapActivity.this);
            }
        });

        mGarden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet();
                gardenBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        gardenFilterHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gardenBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void setGardenManagerRadius(LatLng latLng, double radiusinMeters){

        Log.d(TAG, "method: setGardenManagerRadius called ");
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +

        gardenManager.setSouth(southwestCorner.longitude);
        gardenManager.setNorth(northeastCorner.longitude);
        gardenManager.setEast(northeastCorner.latitude);
        gardenManager.setWest(southwestCorner.latitude);
    }

    public void showGardenSpots(ArrayList<Garden> gardens) {
        Log.d(TAG, "method: showSpots called ");
        showRemotePoint();
        if (gardens.size() != 0) {
            for (int i = 0; i < gardens.size(); i++) {
                Garden oneGarden = gardens.get(i);
                Double lat = oneGarden.getLat();
                Double lon = oneGarden.getLon();
                LatLng latlng = new LatLng(lat, lon);
                Double distance = oneGarden.getDistance();
                String distanceString = "";
                if (distance >= 1000){
                    distanceString = "Distance:  " + MyTools.roundDouble(distance/1000) + " km ";
                }else{
                    distanceString = "Distance:  " +  MyTools.roundDoubleWithOutDecimal(distance) + " m ";
                }
                MarkerOptions options = new MarkerOptions()
                        .position(latlng).title(oneGarden.getName())
                        .snippet(distanceString)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_garden));
                gardenManager.markerList.add(mMap.addMarker(options));
            }
        }else{
            Log.d(TAG, "method: showSpots : no spot found ");
            Toast.makeText(MapActivity.this, "No Nearby Parks found", Toast.LENGTH_SHORT).show();
        }
    }

    // ======================== Parking related code here ==========================
    private void createParkingWidgetst(){
        parkingFilterSheet = findViewById(R.id.parking_filter_bottomsheet);
        parkingBottomSheetBehavior = BottomSheetBehavior.from(parkingFilterSheet);
        spParkingFilterDuration = (Spinner) findViewById(R.id.spin_parking_duration);
        spParkingFilterDistance = (Spinner) findViewById(R.id.spin_parking_distance);
        //spParkingFilterCharge = (Spinner) findViewById(R.id.spin_parking_pay);
        //spParkingAvailable = (Spinner) findViewById(R.id.spin_parking_available);
        //spParkingFilterDisableOnly = (Spinner) findViewById(R.id.spin_parking_disableOnly);
        btParkingSearch = (Button) findViewById(R.id.button_parking_search);
        parkingFilterHandle = findViewById(R.id.parking_filter_handle);
    }

    private void registerParkingBottomSheetWidgets(){
//        spParkingFilterCharge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if (spParkingFilterCharge.getSelectedItem().toString().toLowerCase().equals("yes")) {
//                    parkingManager.getSampleParkingSpot().setNeedToPay("yes");
//                }else if (spParkingFilterCharge.getSelectedItem().toString().toLowerCase().equals("no")) {
//                    parkingManager.getSampleParkingSpot().setNeedToPay("no");
//                }else{
//                    parkingManager.getSampleParkingSpot().setNeedToPay("");
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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
                resetBackgroundForIcons();
                mParking.setBackgroundResource(R.drawable.background_circle_warning);
                destLatlng = null;
                checkRemoteLocation();
                Toast.makeText(MapActivity.this, "Searching for nearby parking places...", Toast.LENGTH_SHORT).show();
                setParkingManagerRadius(remoteLatlng,parkingManager.getDistance());
                parkingManager.searchParkingAndFilterAndShow(MapActivity.this);
            }
        });

        mParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet();
                parkingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        parkingFilterHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parkingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

    public void showParkingSpot(ParkingSpot oneParkingSpot){
        Log.d(TAG, "method: showSpots called ");
        showRemotePoint();
        String bayId = oneParkingSpot.getBayID();
        Double lat = oneParkingSpot.getLat();
        Double lon = oneParkingSpot.getLon();
        String status = oneParkingSpot.getStatus();
        LatLng latlng = new LatLng(lat, lon);
        Log.d(TAG, "method: showSpots : " + oneParkingSpot.toString());
        MarkerOptions options = null;
        Double distance = oneParkingSpot.getDistance();
        String distanceString = "";
        if (distance >= 1000){
            distanceString = "Distance:  " + MyTools.roundDouble(distance/1000) + " km ";
        }else{
            distanceString = "Distance:  " +  MyTools.roundDoubleWithOutDecimal(distance) + " m ";
        }
        if (status.equals("Unoccupied")){
            options = new MarkerOptions().title("Parking Spot")
                    .snippet(distanceString)
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_icon));
        }else{
            options = new MarkerOptions().title("Parking Spot")
                    .snippet(distanceString)
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_redicon));;
        }
        parkingManager.markerList.add(mMap.addMarker(options));
    }

    // ======================== Metro related code here ==========================
    public void createMetroWidgets(){
        metroFilterSheet = findViewById(R.id.metro_filter_bottomsheet);
        metroBottomSheetBehavior = BottomSheetBehavior.from(metroFilterSheet);
        spMetroDistance = (Spinner) findViewById(R.id.spin_metro_distance);
        //spMetroPid = (Spinner) findViewById(R.id.spin_metro_pid);
//        spMetroLift = (Spinner) findViewById(R.id.spin_metro_lift);
//        spMetroLoop = (Spinner) findViewById(R.id.spin_metro_loop);
        btMetroSearch = (Button) findViewById(R.id.button_metro_search);
        mMetro = (ImageView) findViewById(R.id.ic_metro_station);
        metroFilterHandle = findViewById(R.id.metro_filter_handle);
        cbMetroLift = (CheckBox) findViewById(R.id.cb_metro_lift);
        cbMetroRamp = (CheckBox) findViewById(R.id.cb_metro_ramp);
    }

    public void registerMetroBottomSheetWidgets(){
//        spMetroLoop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if (spMetroLoop.getSelectedItem().toString().toLowerCase().equals("yes")) {
//                    metroStationManager.getSampleStation().setHe_loop("Yes");
//                }else if (spMetroLoop.getSelectedItem().toString().toLowerCase().equals("no")) {
//                    metroStationManager.getSampleStation().setHe_loop("No");
//                }else{
//                    metroStationManager.getSampleStation().setHe_loop("");
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        spMetroLift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if (spMetroLift.getSelectedItem().toString().toLowerCase().equals("yes")) {
//                    metroStationManager.getSampleStation().setLift("Yes");
//                }else if (spMetroLift.getSelectedItem().toString().toLowerCase().equals("no")) {
//                    metroStationManager.getSampleStation().setLift("No");
//                }else {
//                    metroStationManager.getSampleStation().setLift("");
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spMetroPid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if (spMetroPid.getSelectedItem().toString().equals("--")) {
//                    metroStationManager.getSampleStation().setPids("");
//                }else{
//                    metroStationManager.getSampleStation().setPids(spMetroPid.getSelectedItem().toString());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        spMetroDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spMetroDistance.getSelectedItem().toString().toLowerCase().equals("1000m")) {
                    metroStationManager.setDistance(1000);
                }else if (spMetroDistance.getSelectedItem().toString().toLowerCase().equals("500m")) {
                    metroStationManager.setDistance(500);
                }else{
                    metroStationManager.setDistance(2000);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btMetroSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metroBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (cbMetroRamp.isChecked()) {
                    metroStationManager.getSampleStation().setHe_loop("Yes");
                }else{
                    metroStationManager.getSampleStation().setHe_loop("");
                }

                if (cbMetroLift.isChecked()) {
                    metroStationManager.getSampleStation().setLift("Yes");
                }else{
                    metroStationManager.getSampleStation().setLift("");
                }

                cleanMap();
                showRemotePoint();
                resetBackgroundForIcons();
                mMetro.setBackgroundResource(R.drawable.background_circle_warning);
                destLatlng = null;
                checkRemoteLocation();
                setMetroStationManagerRadius(remoteLatlng,metroStationManager.getDistance());
                Toast.makeText(MapActivity.this, "Searching for nearby metro stations...", Toast.LENGTH_SHORT).show();
                metroStationManager.searchByLatRange(MapActivity.this);
            }
        });

        mMetro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet();
                metroBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        metroFilterHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metroBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

    }

    private void setMetroStationManagerRadius(LatLng latLng, double radiusinMeters){

        Log.d(TAG, "method: setMetroStationManagerRadius called ");
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +

        metroStationManager.setSouth(southwestCorner.longitude);
        metroStationManager.setNorth(northeastCorner.longitude);
        metroStationManager.setEast(northeastCorner.latitude);
        metroStationManager.setWest(southwestCorner.latitude);
    }

    public void showMetroStations(ArrayList<MetroStation> resultList) {
        Log.d(TAG, "method: showMetroStations called ");
        showRemotePoint();
        if (resultList.size() != 0) {
            for (int i = 0; i < resultList.size(); i++) {
                MetroStation oneStation = resultList.get(i);
                Double lat = oneStation.getLat();
                Double lon = oneStation.getLon();
                LatLng latlng = new LatLng(lat, lon);
                Double distance = oneStation.getDistance();
                String distanceString = "";
                if (distance >= 1000){
                    distanceString = "Distance:  " + MyTools.roundDouble(distance/1000) + " km ";
                }else{
                    distanceString = "Distance:  " +  MyTools.roundDoubleWithOutDecimal(distance) + " m ";
                }
                MarkerOptions options = new MarkerOptions()
                        .position(latlng).title(oneStation.getStation())
                        .snippet(distanceString)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker_train));
                metroStationManager.markerList.add(mMap.addMarker(options));
            }
        }else{
            Log.d(TAG, "method: showSpots : no spot found ");
            Toast.makeText(MapActivity.this, "No Nearby Metro Stations found", Toast.LENGTH_SHORT).show();
        }
    }

    // ======================== Building related code here ==========================
    private void createBuildingSpotWidgets(){
        buildingFilterSheet = findViewById(R.id.building_filter_bottomsheet);
        buildingBottomSheetBehavior = BottomSheetBehavior.from(buildingFilterSheet);
        buildingFilterHandle = findViewById(R.id.building_filter_handle);
        spBuildingFilterDistance = (Spinner) findViewById(R.id.spin_building_distance);
        btBuildingSearch = (Button) findViewById(R.id.button_building_search);

        buildingReportDialog = new Dialog(MapActivity.this);
        buildingReportDialog.setContentView(R.layout.dialog_building_report);
        buildingReportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        cbFeature1 = (CheckBox) buildingReportDialog.findViewById(R.id.report_cb_feature1);
        cbFeature2 = (CheckBox) buildingReportDialog.findViewById(R.id.report_cb_feature2);
        cbFeature3 = (CheckBox) buildingReportDialog.findViewById(R.id.report_cb_feature3);
        cbFeature4 = (CheckBox) buildingReportDialog.findViewById(R.id.report_cb_feature4);
        btReportSubmit = (Button) buildingReportDialog.findViewById(R.id.bt_report_submit);
        starRatingBar = (RatingBar) buildingReportDialog.findViewById(R.id.report_ratingBar);

        buildingInfoDialog = new Dialog(MapActivity.this);
        buildingInfoDialog.setContentView(R.layout.dialog_building_info);
        buildingInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        tvFeature1Count = (TextView) buildingInfoDialog.findViewById(R.id.info_count_feature1);
        tvFeature2Count = (TextView) buildingInfoDialog.findViewById(R.id.info_count_feature2);
        tvFeature3Count = (TextView) buildingInfoDialog.findViewById(R.id.info_count_feature3);
        tvFeature4Count = (TextView) buildingInfoDialog.findViewById(R.id.info_count_feature4);
        tvAverageRating = (TextView) buildingInfoDialog.findViewById(R.id.info_average_rating);
        btInfoMakeReport = (Button) buildingInfoDialog.findViewById(R.id.bt_info_make_report);

        buildingRatingInfoDialog = new Dialog(MapActivity.this);
        buildingRatingInfoDialog.setContentView(R.layout.dialog_building_rating_info);
        buildingRatingInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btRatingInfoClose = (Button) buildingRatingInfoDialog.findViewById(R.id.bt_building_rating_info_close);
    }

    public void registerBuildingSpotBottomSheetWidgets(){
        spBuildingFilterDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spBuildingFilterDistance.getSelectedItem().toString().toLowerCase().equals("1000m")) {
                    buildingManager.setDistance(1000);
                }else if (spBuildingFilterDistance.getSelectedItem().toString().toLowerCase().equals("600m")) {
                    buildingManager.setDistance(600);
                }else{
                    buildingManager.setDistance(300);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btInfoMakeReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingInfoDialog.dismiss();
                buildingReportDialog.show();
            }
        });

        btReportSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collectSelection();
                if (cbFeature1.isChecked()){
                    buildingManager.reportBuildingSpot.setFeature1Count(
                            buildingManager.reportBuildingSpot.getFeature1Count() + 1);
                }
                if (cbFeature2.isChecked()){
                    buildingManager.reportBuildingSpot.setFeature2Count(
                            buildingManager.reportBuildingSpot.getFeature2Count() + 1);
                }
                if (cbFeature3.isChecked()){
                    buildingManager.reportBuildingSpot.setFeature3Count(
                            buildingManager.reportBuildingSpot.getFeature3Count() + 1);
                }
                if (cbFeature4.isChecked()){
                    buildingManager.reportBuildingSpot.setFeature4Count(
                            buildingManager.reportBuildingSpot.getFeature4Count() + 1);
                }

                buildingManager.reportBuildingSpot.setReportCount(buildingManager.reportBuildingSpot
                                                                                .getReportCount() + 1);
                buildingManager.reportBuildingSpot.setRatingTotal(buildingManager.reportBuildingSpot.getRatingTotal()
                                                                    + Math.round(starRatingBar.getRating()));
                //makeReport();
                buildingManager.updateBuildingSpot();
                btBuildingSearch.callOnClick();
                buildingReportDialog.dismiss();
                buildingRatingInfoDialog.dismiss();
            }
        });

        btBuildingSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                cleanMap();
                resetBackgroundForIcons();
                mBuilding.setBackgroundResource(R.drawable.background_circle_warning);
                destLatlng = null;
                checkRemoteLocation();
                setBuildingSpotManagerRadius(remoteLatlng,buildingManager.getDistance());
                Toast.makeText(MapActivity.this, "Searching for nearby buildings...", Toast.LENGTH_SHORT).show();
                buildingManager.searchByLatRange(MapActivity.this);
                buildingRatingInfoDialog.show();
            }
        });

        mBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            closeBottomSheet();
            buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        buildingFilterHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        btRatingInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingRatingInfoDialog.dismiss();
            }
        });
    }

    private void setBuildingSpotManagerRadius(LatLng latLng, double radiusinMeters){

        Log.d(TAG, "method: setBuildingManagerRadius called ");
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +

        buildingManager.setSouth(southwestCorner.longitude);
        buildingManager.setNorth(northeastCorner.longitude);
        buildingManager.setEast(northeastCorner.latitude);
        buildingManager.setWest(southwestCorner.latitude);
    }

    public void showBuildingSpots(ArrayList<BuildingSpot> buildingSpots) {
        Log.d(TAG, "method: showSpots called ");
        showRemotePoint();
        if (buildingSpots.size() != 0) {
            for (int i = 0; i < buildingSpots.size(); i++) {
                BuildingSpot oneBuilding = buildingSpots.get(i);
                Double lat = oneBuilding.getY_coordinate();
                Double lon = oneBuilding.getX_coordinate();
                LatLng latlng = new LatLng(lat, lon);
                int rating = oneBuilding.getAccessibility_rating();
                Double distance = oneBuilding.getDistance();
                String distanceString = "";
                if (distance >= 1000){
                    distanceString = "Distance:  " + MyTools.roundDouble(distance/1000) + " km ";
                }else{
                    distanceString = "Distance:  " +  MyTools.roundDoubleWithOutDecimal(distance) + " m ";
                }
                MarkerOptions options = new MarkerOptions()
                        .position(latlng).title(oneBuilding.getBuilding_name())
                        .snippet(distanceString);
                if (rating == 0){
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_red));
                }else if(rating == 3){
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_green));
                }else if(rating == 1){
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_orange));
                }else if(rating == 2){
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_orange));
                }
                Marker building = mMap.addMarker(options);
                buildingManager.markerList.add(building);
            }
        }else{
            Log.d(TAG, "method: showSpots : no spot found ");
            Toast.makeText(MapActivity.this, "No Nearby Buildings Information found", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateInfoDialog() {
        Log.d(TAG, "method: updateInfoDialog called: -- " + buildingManager.reportBuildingSpot.getFeature1Count() + "");

        tvFeature1Count.setText(buildingManager.reportBuildingSpot.getFeature1Count() + "");
        tvFeature2Count.setText(buildingManager.reportBuildingSpot.getFeature2Count() + "");
        tvFeature3Count.setText(buildingManager.reportBuildingSpot.getFeature3Count() + "");
        tvFeature4Count.setText(buildingManager.reportBuildingSpot.getFeature4Count() + "");
        if (buildingManager.reportBuildingSpot.getReportCount() == 0) {
            tvAverageRating.setText("0");
        } else {
            tvAverageRating.setText(MyTools.roundDouble(1.0 * buildingManager.reportBuildingSpot.getRatingTotal()
                    / buildingManager.reportBuildingSpot.getReportCount()) + "");
        }
    }

//    private void showBuildingAccessibility(JsonArray accessibility){
//        Log.d(TAG, "method: showSpots called ");
//        if (accessibility.size() != 0) {
//            for (int i = 0; i < accessibility.size(); i++) {
//                JsonObject oneSpot = accessibility.get(i).getAsJsonObject();
//                Double lat = oneSpot.get("y_coordinate").getAsDouble();
//                Double lon = oneSpot.get("x_coordinate").getAsDouble();
//                JsonElement name = oneSpot.get("building_name");
//                String buildingName = "Target Building";
//                if (name != null){
//                    buildingName = oneSpot.get("building_name").getAsString();
//                }
//                String rating = oneSpot.get("accessibility_rating").toString();
//                String level = oneSpot.get("accessibility_type").toString();
//                String snippet =  " Accessibility Rating: " + rating +  '\n' +
//                        " Accessibility Type: " + level ;
//                LatLng latlng = new LatLng(lat, lon);
//                MarkerOptions options = null;
//                if (rating.equals("\"0\"")){
//                    options = new MarkerOptions().position(latlng).title(buildingName).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_red));
//                }else if(rating.equals("\"3\"")){
//                    options = new MarkerOptions().position(latlng).title(buildingName).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_green));
//                }else if(rating.equals("\"1\"")){
//                    options = new MarkerOptions().position(latlng).title(buildingName).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_orange));
//                }else if(rating.equals("\"2\"")){
//                    options = new MarkerOptions().position(latlng).title(buildingName).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building_lightgreen));
//                }
//                cleanMap();
//                mMap.addMarker(options);
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(remoteLatlng, 16f));
//            }
//        }else{
//            Log.d(TAG, "method: showSpots : no data found ");
//            Toast.makeText(MapActivity.this, "No information on the building", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private JsonArray findBuildingAccessibility(LatLng latLng, double radiusinMeters){
//        Log.d(TAG, "nethod: findBuildingAccessibility called ");
//        JsonArray results = null;
//        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
//        LatLng southwestCorner = latLngBounds.southwest; // -
//        LatLng northeastCorner = latLngBounds.northeast; // +
//        building.setSouth(southwestCorner.longitude);
//        building.setNorth(northeastCorner.longitude);
//        building.setEast(northeastCorner.latitude);
//        building.setWest(southwestCorner.latitude);
//        return building.FindBuildingAccessibility();
//    }

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

    private void closeBottomSheet() {
        toiletBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        parkingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        metroBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        buildingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void showRemotePoint(){
        Log.d(TAG, "showKeyPoint: method called");

        if (remoteLatlng!= null){
            Log.d(TAG, "showKeyPoint: add remote point");
            MarkerOptions markerOptions = new MarkerOptions()
                    .title("Target Location")
                    .snippet("You are here")
                    .position(remoteLatlng);
            mMap.addMarker(markerOptions);
        }
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
//        remoteLatlng = latlng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));


        if (!title.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latlng)
                    .title(title)
                    .snippet("Your are here");
            result = mMap.addMarker(markerOptions);
            result.showInfoWindow();
        }
        hideSoftKeyboard();
        return result;
    }

    public void moveToMarker(Marker marker){
        float zoom = mMap.getCameraPosition().zoom;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),zoom));
        marker.showInfoWindow();
        closeBottomSheet();
    }

    private void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSearchingResultList(ArrayList<Object> objects){
        ListAdapter listAdapter = new ListAdapter(MapActivity.this, objects);
        resultList = (ListView)findViewById(R.id.search_result_list);
        resultList.setAdapter(listAdapter);
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(MapActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
            }
        });
        if (objects.size() != 0){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    };

    public void navigationToGoogle(){
        if (destLatlng != null) {
            redirectConfirmDialog.show();
        }else{
            Toast.makeText(MapActivity.this, "No Destination is chosen, click on a marker.", Toast.LENGTH_SHORT).show();
        }
    }

    public void redirectToGoogle(){
        if (destLatlng != null) {
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
    }

    public void setDestLatlng(Double lat, Double lon){
        destLatlng = new LatLng(lat,lon);
    }

    public void resetBackgroundForIcons(){
        mToilet.setBackgroundResource(R.drawable.background_circle_white);
        mBuilding.setBackgroundResource(R.drawable.background_circle_white);
        mParking.setBackgroundResource(R.drawable.background_circle_white);
        mMetro.setBackgroundResource(R.drawable.background_circle_white);
        mGarden.setBackgroundResource(R.drawable.background_circle_white);

    }

    public void resetReportDialog(){
        cbFeature1.setChecked(false);
        cbFeature2.setChecked(false);
        cbFeature3.setChecked(false);
        cbFeature4.setChecked(false);
        starRatingBar.setRating((float)0.0);
    }

    public void checkRemoteLocation(){
        if (remoteLatlng == null){
            if (currentLatlng != null){
                remoteLatlng = currentLatlng;
            }else{
                remoteLatlng = new LatLng(-37.87681761019685, 145.04447914659977);
            }
        }
    }

    // ======================== permission code here =========================
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
                    initMap();
                }
            }
        }
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

//    private String getRequestedUrl(LatLng from, LatLng to) {
//        Log.d(TAG, "method: getRequestedUrl is called:");
//        String string_origin = "origin=" + from.latitude + "," + from.longitude;
//        String string_destination = "destination=" + to.latitude + "," + to.longitude;
//        String sensor = "sensor=false";
//        String mode = "mode=driving";
//        String param = string_origin+"&"+string_destination+"&"+sensor+"&"+mode;
//        String output = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param; // + "&key=AIzaSyCcLw8zgIjhCqC1QeCBXv7NVGAUFuChKq8";
//        return url;
//    }

//    private String requesDirection(String reqUrl) throws IOException {
//        Log.d(TAG, "method: requesDirection is called: " + reqUrl);
//        String responseString = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpURLConnection = null;
//        try{
//            URL url = new URL(reqUrl);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.connect();
//
//            inputStream = httpURLConnection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//
//            responseString = stringBuffer.toString();
//            bufferedReader.close();
//            inputStream.close();
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            if (inputStream != null) {
//                inputStream.close();
//            }
//            httpURLConnection.disconnect();
//        }
//        Log.d(TAG, "method: requesDirection get: " + responseString);
//        return responseString;
//    }


//    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            Log.d(TAG, "TaskRequsetDirections: method start");
//            String responseString = "";
//            try{
//                responseString = requesDirection(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            TaskParser taskParser = new TaskParser();
//            taskParser.execute(s);
//        }
//    }

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

    // -------- google palces API actocomplete suggestions --------
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
            String remotePlaceTitle = new String(place.getName().toString());
            remoteLatlng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            moveCamera(place.getLatLng(), DEFAULT_ZOOM, remotePlaceTitle);
            places.release();
        }
    };



}
