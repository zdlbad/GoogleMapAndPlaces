package com.example.zhangdonglin.googlemapsandplace;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.gson.JsonParser;
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
import java.util.Scanner;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    //global vars
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String BASE_URL_TOILET = "https://data.melbourne.vic.gov.au/resource/dsec-5y6t.json?$where=";
    private static final String BASE_URL_PARKING = "https://data.melbourne.vic.gov.au/resource/dtpv-d4pf.json?$where=";

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker, mToilet, mParking, mNavigation;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Marker mMarker;
    private LatLng currentLatlng, destLatlng, remoteLatlng;
    private String remotePlaceTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreate: called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        //mInfo = (ImageView) findViewById(R.id.place_info);
        //mPlacePicker = (ImageView) findViewById(R.id.place_picker);
        mToilet = (ImageView) findViewById(R.id.toilet);
        mParking = (ImageView) findViewById(R.id.parking);
        mNavigation = (ImageView) findViewById(R.id.ic_navigation);
        mNavigation.setVisibility(View.INVISIBLE);
        getLocationPermission();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions.");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready.", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    destLatlng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    Toast.makeText(MapActivity.this, "Destination set.", Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    showKeyPoint();
                    mNavigation.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            init();
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
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
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

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mGps: clicked gps icon");
                getDeviceLocation();
            }
        });

        mToilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMap();
                destLatlng = null;
                showKeyPoint();
                new AsyncTask<Void, Void, ArrayList<Double[]>>() {

                    @Override
                    protected ArrayList<Double[]> doInBackground(Void... voids) {
                        return findNearbyToilets(BASE_URL_TOILET, remoteLatlng,800);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Double[]> doubles) {
                        showToiletSpots(doubles);
                    }
                }.execute();
            }
        });

        mParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMap();
                destLatlng = null;
                showKeyPoint();
                new AsyncTask<Void, Void, JsonArray>() {

                    @Override
                    protected JsonArray doInBackground(Void... voids) {
                        return findNearbyParkings(BASE_URL_PARKING, remoteLatlng,800);
                    }

                    @Override
                    protected void onPostExecute(JsonArray doubles) {
                        showParkingSpots(doubles);
                    }
                }.execute();
            }
        });

        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMap();
                showKeyPoint();
                Log.d(TAG, "mNavigation: clicked navigation icon");
                String url = getRequestedUrl(currentLatlng, destLatlng);
                Log.d(TAG, "mNavigation: clicked navigation icon, show currentLatlng " + currentLatlng.latitude + " || " + currentLatlng.longitude);
                Log.d(TAG, "mNavigation: clicked navigation icon, show destLatlng " + destLatlng.latitude + " || " + destLatlng.longitude);
                TaskRequestDirections findPath = new TaskRequestDirections();
                findPath.execute(url);
                mNavigation.setVisibility(View.INVISIBLE);
            }
        });

        Log.d(TAG, "init: initiating finished");
        hideSoftKeyboard();
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    public void clearMap(){
        mMap.clear();
    }

    //South Longitude is negative，North Longitude is positive; East Latitude is positive，West Latitude is negative.
    private ArrayList<Double[]> findNearbyToilets(String baseUrl, LatLng latLng, double radiusinMeters){

        Log.d(TAG, "nethod: findNearbyToilets called ");
        ArrayList<Double[]> results = new ArrayList<Double[]>();
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +
        Double south = southwestCorner.longitude; // -
        Double north = northeastCorner.longitude; // +
        Double east = northeastCorner.latitude; // +
        Double west = southwestCorner.latitude; // -

        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("lat > \"" + east + "\" and lat < \"" + west + "\" and lon < \"" + north + "\" and lon > \"" + south + "\"");
        String urlString = builder.toString();
        Log.d(TAG, "url: URL:  " + urlString);

        //String fakeUrl =  "https://data.melbourne.vic.gov.au/resource/dsec-5y6t.json?$where=lat > \"-37.459502430111115\"";

        URL url = null;
        HttpURLConnection conn = null;
        boolean checkResult = false;
        String serverResult = "";
        //Making HTTP request
        try {
            url = new URL(urlString);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()){
                serverResult += inStream.nextLine();
            }
            JsonArray rs = new JsonParser().parse(serverResult).getAsJsonArray();
            for(int i = 0; i < rs.size(); i++)
            {
                JsonObject r = rs.get(i).getAsJsonObject();
                Double[] result = new Double[2];
                result[0] = r.get("lat").getAsDouble();
                result[1] = r.get("lon").getAsDouble();
                results.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return results;
    }

    private JsonArray findNearbyParkings(String baseUrl, LatLng latLng, double radiusinMeters){

        Log.d(TAG, "nethod: findNearbyParkings called ");
        JsonArray results = null;
        LatLngBounds latLngBounds = toBounds(latLng, radiusinMeters);
        LatLng southwestCorner = latLngBounds.southwest; // -
        LatLng northeastCorner = latLngBounds.northeast; // +
        Double south = southwestCorner.longitude; // -
        Double north = northeastCorner.longitude; // +
        Double east = northeastCorner.latitude; // +
        Double west = southwestCorner.latitude; // -

        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("lat < " + east + " and lat > " + west + " and lon < " + north + " and lon > " + south);
        String urlString = builder.toString();
        Log.d(TAG, "url: URL:  " + urlString);

        URL url = null;
        HttpURLConnection conn = null;
        boolean checkResult = false;
        String serverResult = "";
        //Making HTTP request
        try {
            url = new URL(urlString);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()){
                serverResult += inStream.nextLine();
            }
            results = new JsonParser().parse(serverResult).getAsJsonArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return results;
    }

    private void showKeyPoint(){
        Log.d(TAG, "showKeyPoint: method called");
        if (currentLatlng != null){
            Log.d(TAG, "showKeyPoint: add current");
            mMap.addMarker(new MarkerOptions().position(currentLatlng));
        }

        if (remoteLatlng!= null){
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

    private void showToiletSpots(ArrayList<Double[]> spotList) {
        Log.d(TAG, "method: showSpots called ");

        if (spotList.size() != 0){
            for (Double[] oneSpot : spotList ){
                LatLng latlng = new LatLng(oneSpot[0], oneSpot[1]);
                MarkerOptions options = new MarkerOptions()
                        .position(latlng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).alpha(0.8f);
                mMap.addMarker(options);
            }
        }else{
            Log.d(TAG, "method: showSpots : no spot found ");
            Toast.makeText(MapActivity.this, "No Nearby Toilets found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showParkingSpots(JsonArray spotArray){
        Log.d(TAG, "method: showSpots called ");

        if (spotArray.size() != 0) {
            for (int i = 0; i < spotArray.size(); i++) {
                JsonObject oneSpot = spotArray.get(i).getAsJsonObject();
                Double lat = oneSpot.get("lat").getAsDouble();
                Double lon = oneSpot.get("lon").getAsDouble();
                String status = oneSpot.get("status").toString();
                LatLng latlng = new LatLng(lat, lon);
                MarkerOptions options = null;
                if (status.equals("\"Present\"")){
                    options = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).alpha(0.8f);
                }else{
                    options = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(0f)).alpha(0.5f);
                }
                mMap.addMarker(options);
            }
        }else{
            Log.d(TAG, "method: showSpots : no spot found ");
            Toast.makeText(MapActivity.this, "No Nearby Parking Place found", Toast.LENGTH_SHORT).show();
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: intialising the map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            currentLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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

            clearMap();
            remotePlaceTitle = new String(place.getName().toString());
            remoteLatlng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            moveCamera(place.getLatLng(), DEFAULT_ZOOM, remotePlaceTitle);
            places.release();
        }
    };

}
