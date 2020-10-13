package com.example.ridewithme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ridewithme.LocationService;
import com.example.ridewithme.R;
import com.example.ridewithme.directionhelpers.FetchURL;
import com.example.ridewithme.directionhelpers.TaskLoadedCallback;
import com.example.ridewithme.utills.MyLoc;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.android.libraries.places.api.model.Place;

import android.util.Log;

import java.util.Arrays;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
        /////////////////-------stuf2-----------//////////////////

        protected LatLng start = null;
        protected LatLng end = null;

        //to get location permissions.
        private final static int LOCATION_REQUEST_CODE = 23;
        boolean locationPermission = false;

        //to get Autocomplete
        private final static int AUTOCOMPLETE_REQUEST_CODE = 100;
        //polyline object
        private List<Polyline> polylines = null;


        //////////////////////////stuf1////////////////
        private final int LOCATION_PERMISSIONS_REQUEST_CODE = 125;

        private GoogleMap mMap;
        private MarkerOptions place1, place2,myPlace;
        private FloatingActionButton map_BTN_directions,map_BTN_gps,map_BTN_start,map_BTN_stop;
        private AutocompleteSupportFragment autocompleteFragment ;
        private EditText map_EDT_place_autocomplete;
    //android:name="com.google.android.libraries.places.widget.AutocompleteSupportFragment"
        private Polyline currentPolyline;
        private LocalBroadcastManager localBroadcastManager;

        public static final String BROADCAST_NEW_LOCATION_DETECTED = "com.example.ridewithme.NEW_LOCATION_DETECTED";
        double[] valuesLatLon = new double[2];
        private LatLng location;
        private BroadcastReceiver myReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals(BROADCAST_NEW_LOCATION_DETECTED)) {
                                String json = intent.getStringExtra("EXTRA_LOCATION");
                                try {
                                        MyLoc lastLocation = new Gson().fromJson(json, MyLoc.class);
                                        location= new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                                        newLocation(lastLocation);
                                } catch(Exception ex) { }
                        }
                }
        };

        private void newLocation(final MyLoc lastLocation) {
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                Log.d("johny", "run: is on + lati = " +lastLocation.getLatitude() + ", longi= " + lastLocation.getLongitude() );
                              /*  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(),
                                        lastLocation.getLongitude()),15));*/

                                place1 = new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("Home");
                           //     place2 = new MarkerOptions().position(new LatLng(32.088882, 34.848297)).title("stas_house");
                                mMap.addMarker(place1);
                          //      mMap.addMarker(place2);
                        }
                });
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.map_activity);
                findViews();
                askLocationPermissions();
                map_BTN_directions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(),
                                        "bicycle"), "bicycle");
                        }
                });
                map_BTN_gps.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                                //myPlace = new MarkerOptions().position(new LatLng())
                        }
                });
                map_BTN_start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                startService();

                        }
                });
                map_BTN_stop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                stopService();
                        }
                });
                Places.initialize(getApplicationContext(),"AIzaSyCI-4RaDocwneRsw2ryTRPMf7NzGV-F1CE");
                map_EDT_place_autocomplete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.NAME,
                                        Place.Field.LAT_LNG);
                                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(MapActivity.this);
                                startActivityForResult(intent,AUTOCOMPLETE_REQUEST_CODE);


                        }
                });

               /* autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
                autocompleteFragment.setCountry("IL");

                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
                // Set up a PlaceSelectionListener to handle the response.
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(@NonNull Place place) {
                                Log.d("johny", "Place: " + place.getName() + ", " + place.getId());

                        }

                        @Override
                        public void onError(Status status) {
                                Log.d("johny", "onError: " + status.getStatus());
                        }
                });*/

                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.mapNearBy);
                mapFragment.getMapAsync(this);
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                        if (resultCode == RESULT_OK) {
                            addDestPlaceInMap(data);

                        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                                // TODO: Handle the error.
                                Status status = Autocomplete.getStatusFromIntent(data);
                                Log.d("johny", status.getStatusMessage());
                        } else if (resultCode == RESULT_CANCELED) {
                                // The user canceled the operation.
                        }
                        return;
                }
                super.onActivityResult(requestCode, resultCode, data);
        }

    private void addDestPlaceInMap(Intent data) {
        Place place = Autocomplete.getPlaceFromIntent(data);
        Log.d("johny", "Place: " + place.getName() + ", " + place.getId());
        map_EDT_place_autocomplete.setText(place.getAddress());
        place2 = new MarkerOptions().position(place.getLatLng()).title("Dest");
        mMap.addMarker(place2);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15));
    }

    private void findViews() {
                map_BTN_directions = findViewById(R.id.map_BTN_directions);
                map_BTN_gps =findViewById(R.id.map_BTN_gps);
                map_BTN_start=findViewById(R.id.map_BTN_start);
                map_BTN_stop= findViewById(R.id.map_BTN_stop);
                map_EDT_place_autocomplete = findViewById(R.id.map_EDT_place_autocomplete);
                autocompleteFragment = (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.map_EDT_place_autocomplete);

        }

        private void requestPermision() {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                LOCATION_REQUEST_CODE);
                } else {
                        locationPermission = true;
                }
        }


        @Override
        protected void onPause() {
                super.onPause();
                localBroadcastManager.unregisterReceiver(myReceiver);
        }
        @Override
        protected void onResume() {
                super.onResume();
                IntentFilter intentFilter = new IntentFilter(BROADCAST_NEW_LOCATION_DETECTED);
                localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.registerReceiver(myReceiver, intentFilter);
        }
        @Override
        public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
               /* LatLng ltlng=new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f);
                mMap.animateCamera(cameraUpdate);*/

                Log.d("mylog", "Added Markers");

        }

        private String getUrl(LatLng origin, LatLng dest, String directionMode) {
                // Origin of route
                String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
                // Destination of route
                String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
                // Mode
                String mode = "mode=" + directionMode;
                // Building the parameters to the web service
                String parameters = str_origin + "&" + str_dest + "&" + mode;
                // Output format
                String output = "json";
                // Building the url to the web service
                String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
                return url;
        }

        @Override
        public void onTaskDone(Object... values) {
                if (currentPolyline != null)
                        currentPolyline.remove();
                currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        }


        private void startService() {
                actionToService(LocationService.START_FOREGROUND_SERVICE);
        }

        private void pauseService() {
                actionToService(LocationService.PAUSE_FOREGROUND_SERVICE);
        }

        private void stopService() {
                actionToService(LocationService.STOP_FOREGROUND_SERVICE);
        }

        private void actionToService(String action) {
                Intent startIntent = new Intent(MapActivity.this, LocationService.class);
                startIntent.setAction(action);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(startIntent);
                        // or
                        //ContextCompat.startForegroundService(this, startIntent);
                } else {
                        startService(startIntent);
                }
        }

        // // // // // // // // // // // // // // // // Permissions  // // // // // // // // // // // // // // //

        private void askLocationPermissions() {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION
                                ,Manifest.permission.ACCESS_FINE_LOCATION
                                ,Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ,Manifest.permission.FOREGROUND_SERVICE
                        },
                        LOCATION_PERMISSIONS_REQUEST_CODE);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
                switch (requestCode) {
                        case LOCATION_PERMISSIONS_REQUEST_CODE: {
                                // If request is cancelled, the result arrays are empty.
                                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                        Toast.makeText(MapActivity.this, "Result code = " + grantResults[0], Toast.LENGTH_SHORT).show();

                                        // permission was granted, yay! Do the
                                        // contacts-related task you need to do.
                                } else {

                                        // permission denied, boo! Disable the
                                        // functionality that depends on this permission.
                                        Toast.makeText(MapActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                                }
                                return;
                        }
                }
        }


}