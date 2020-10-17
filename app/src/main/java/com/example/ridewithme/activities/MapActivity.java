package com.example.ridewithme.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.ridewithme.LocationService;
import com.example.ridewithme.R;

import com.example.ridewithme.utills.MyLoc;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {




    //////////////////////////variables////////////////
    private final static int AUTOCOMPLETE_REQUEST_CODE = 100;
    private final int LOCATION_PERMISSIONS_REQUEST_CODE = 125;
   private String serverKey = "AIzaSyCI-4RaDocwneRsw2ryTRPMf7NzGV-F1CE"; // Api Key For Google Direction API \\
        private GoogleMap mMap;
        private MarkerOptions place1=null, place2;
        private FloatingActionButton map_BTN_directions,map_BTN_gps,map_BTN_start,map_BTN_stop,map_BTN_state_elite,map_BTN_pause;
        private AutocompleteSupportFragment autocompleteFragment ;
        private EditText map_EDT_place_autocomplete;
        private TextView map_LBL_distance,map_LBL_time;
        private LocalBroadcastManager localBroadcastManager;

        public static final String BROADCAST_NEW_LOCATION_DETECTED = "com.example.ridewithme.NEW_LOCATION_DETECTED";
        private LatLng location,destination;
    //////////////////////////variables////////////////

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
                            if(place1==null)
                            {
                                place1 = new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("My_Loc");
                                mMap.addMarker(place1);
                            }
                        }
                });
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.map_activity);
            findViews();
            askLocationPermissions();
            init();

            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_MAP_google_map);
            mapFragment.getMapAsync(this);
        }

    private void init() {
        map_BTN_directions.setOnClickListener(myViewLister);
        map_BTN_gps.setOnClickListener(myViewLister);
        map_BTN_start.setOnClickListener(myViewLister);
        map_BTN_stop.setOnClickListener(myViewLister);
        map_BTN_pause.setOnClickListener(myViewLister);
        map_BTN_state_elite.setOnClickListener(myViewLister);
        Places.initialize(getApplicationContext(), serverKey);
        map_EDT_place_autocomplete.setOnClickListener(myViewLister);
    }

    private View.OnClickListener myViewLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonClicked(view);
        }
    };

    private void buttonClicked(View view) {
        if(view.getTag().toString().equals("start"))
        {
            startService();
        }
        else if(view.getTag().toString().equals("stop"))
        {
            stopService();

        }
        else if(view.getTag().toString().equals("pause"))
        {
            pauseService();
        }
        else if(view.getTag().toString().equals("direction"))
        {
            getDestinationInfo(destination);

        }
        else if(view.getTag().toString().equals("elite"))
        {
            changeMapType();

        }
        else if(view.getTag().toString().equals("gps"))
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

        }
        else if(view.getTag().toString().equals("search"))
        {
            searchPlace();

        }

    }

    private void searchPlace() {
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME,
                Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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
        destination= place.getLatLng();
        map_EDT_place_autocomplete.setText(place.getAddress());
        place2 = new MarkerOptions().position(place.getLatLng()).title("Dest");
        mMap.addMarker(place2);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15));
    }

    private void validateButtons() {
        if (isMyServiceRunning(LocationService.class)) {
            map_BTN_start.setEnabled(false);
            map_BTN_pause.setEnabled(true);
            map_BTN_stop.setEnabled(true);
        } else {
            map_BTN_start.setEnabled(true);
            map_BTN_pause.setEnabled(false);
            map_BTN_stop.setEnabled(false);
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        int counter = 0;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runs = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                counter++;
                //return true;
            }
        }

        Log.d("pttt", "Counter= " + counter);
        if (counter > 0)
            return true;
        return false;
    }
    private void findViews() {
                map_BTN_directions = findViewById(R.id.map_BTN_directions);
                map_BTN_gps =findViewById(R.id.map_BTN_gps);
                map_BTN_start=findViewById(R.id.map_BTN_start);
                map_BTN_stop= findViewById(R.id.map_BTN_stop);
                map_EDT_place_autocomplete = findViewById(R.id.map_EDT_place_autocomplete);
                autocompleteFragment = (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.map_EDT_place_autocomplete);
         map_LBL_distance=findViewById(R.id.map_LBL_distance);
         map_LBL_time    =findViewById(R.id.map_LBL_time);
        map_BTN_state_elite = findViewById(R.id.map_BTN_state_elite);
        map_BTN_pause= findViewById(R.id.map_BTN_pause);
        }
    private void getDestinationInfo(LatLng latLngDestination) {
       // progressDialog();
        final LatLng origin = location;
        final LatLng destination = latLngDestination;
        //-------------Using AK Exorcist Google Direction Library---------------\\
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.BICYCLING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                      //  dismissDialog();
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();

                            //------------Displaying Distance and Time-----------------\\
                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\

                            //--------------Drawing Path-----------------\\
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),
                                        directionPositionList, 5, Color.BLUE);
                            mMap.addPolyline(polylineOptions);
                            //--------------------------------------------\\

                            //-----------Zooming the map according to marker bounds-------------\\
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(origin);
                            builder.include(destination);
                            LatLngBounds bounds = builder.build();

                            int width = getResources().getDisplayMetrics().widthPixels;
                            int height = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                            mMap.animateCamera(cu);
                            //------------------------------------------------------------------\\

                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            Toast.makeText(getApplicationContext(), "No routes exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
        //-------------------------------------------------------------------------------\\

    }
    private void changeMapType() {
        if (mMap != null) {
            int MapType = mMap.getMapType();
            if (MapType == 1) {
                map_BTN_state_elite.setImageResource(R.drawable.ic_satellite_off);
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else {
                map_BTN_state_elite.setImageResource(R.drawable.ic_satellite_on);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }

        }
    }
    private void showingDistanceTime(String distance, String duration) {
                map_LBL_time.setText(duration);
                map_LBL_distance.setText(distance);
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


        }

        private void startService() {
                actionToService(LocationService.START_FOREGROUND_SERVICE);
            validateButtons();
        }

        private void pauseService() {
         actionToService(LocationService.PAUSE_FOREGROUND_SERVICE);
            validateButtons();
    }

        private void stopService() {
                actionToService(LocationService.STOP_FOREGROUND_SERVICE);
            validateButtons();
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