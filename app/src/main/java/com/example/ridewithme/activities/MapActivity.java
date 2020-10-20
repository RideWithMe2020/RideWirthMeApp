package com.example.ridewithme.activities;

import androidx.annotation.NonNull;
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
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.ridewithme.Classes.Account;
import com.example.ridewithme.Classes.Tour;
import com.example.ridewithme.Classes.UserListTours;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.android.libraries.places.api.model.Place;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    //////////////////////////variables////////////////
    private final static int AUTOCOMPLETE_REQUEST_CODE = 100;
    private final int LOCATION_PERMISSIONS_REQUEST_CODE = 125;
    private String serverKey = "AIzaSyCI-4RaDocwneRsw2ryTRPMf7NzGV-F1CE"; // Api Key For Google Direction API \\
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private FloatingActionButton map_BTN_directions, map_BTN_gps, map_BTN_start, map_BTN_stop, map_BTN_state_elite, map_BTN_pause;
    private AutocompleteSupportFragment autocompleteFragment;
    private EditText map_EDT_place_autocomplete;
    private TextView map_LBL_distance, map_LBL_time;
    private LocalBroadcastManager localBroadcastManager;
    private ImageView map_IMG_zoomIn, map_IMG_zoomOut;
    public static final String BROADCAST_NEW_LOCATION_DETECTED = "com.example.ridewithme.NEW_LOCATION_DETECTED";
    private LatLng location, destination;
    private Tour myTour;
    private String  sourceLocation;
    private double avg_Speed = 0.0;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private FirebaseDatabase database;
    private Account account;

    //////////////////////////variables////////////////

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_NEW_LOCATION_DETECTED)) {
                String json = intent.getStringExtra("EXTRA_LOCATION");
                try {
                    MyLoc lastLocation = new Gson().fromJson(json, MyLoc.class);
                    location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    sourceLocation = getCompleteAddressString(lastLocation.getLatitude(), lastLocation.getLongitude());
                    newLocation(lastLocation);
                 //  myTour.addAvg_speed(lastLocation.getSpeed());
                } catch (Exception ex) {
                    Log.d("johny", "onReceive: " + ex.toString());
                }
            }
        }
    };



    //
    private void newLocation(final MyLoc lastLocation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("johny", "run: is on + lati = " + lastLocation.getLatitude() + ", longi= " + lastLocation.getLongitude());
                              /*  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(),
                                        lastLocation.getLongitude()),15));*/
              //  place1 = new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("My_Loc");

              //  mMap.addMarker(place1);
                //      mMap.clear();

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
        firebaseAuth= FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        if(account!=null)
            Log.d("johny", "onCreate: " + account.getName() + account.getTours());
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_MAP_google_map);
        mapFragment.getMapAsync(this);
        //addTourToFB();
    }

    private void getUserFromFB() {

        //-------- insert to Cloud FireStore -----------////////////////
        String userID = firebaseAuth.getCurrentUser().getUid();
        Log.d("johny", "getUserFromFB: userID =  " + userID);
            final DocumentReference myRef = fStore.collection("tours").document(userID);
        Map<String, Object> updates = new HashMap<>();
        updates.put(userID, myTour);
       /* myRef.update(updates, FieldValue.arrayUnion(updates)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });*/
        //-------- insert to Cloud FireBase  -----------////////////////

        DatabaseReference myDataRef = database.getReference("users");

        myDataRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //Tour tour = dataSnapshot.getValue(Tour.class);
                account = dataSnapshot.getValue(Account.class);
               // tours.add(myTour);
                account.getTours().add(myTour);
                addTourToFB();
                Log.d("johny", "onDataChange: dest is "  + account.getTours().get(1).getDest());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pttt", "Failed to read value.", error.toException());
            }
        });

       // myRef.update(updates);
        /*        myRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<Tour> tours = document.toObject(UserListTours.class).all_Tours;
                        tours.add(myTour);
                        myRef.set(tours);
                        //Use the the list
                    }
                }
            }
        });*/
       // myRef.update(updates);
        //fStore.collection("users/tours").document(userID).set(updates);
        /*myRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                //ArrayList<Tour> tours = value.toObject(UserListTours.class).all_Tours;
                // tours.add(myTour);
                account = value.toObject(Account.class);
                    account.addToursToList(myTour);
                  //  myRef.set(account);
            }
        });*/
    }

    private void addTourToFB() {
        String userID = firebaseAuth.getCurrentUser().getUid();
        //-------- insert to Cloud Firebase --> the set of every account with unique userID---------------- //
        DatabaseReference myDataRef = database.getReference("users");
        myDataRef.child(userID).setValue(account);

        /*//-------- insert to Cloud FireStore --> the set of every account with unique userID---------------- //
        DocumentReference myRef = fStore.collection("users").document(userID);
        myRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                             @Override
                                             public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                  account = documentSnapshot.toObject(Account.class);
                                                 account.getTours().add(myTour);
                                             }
                                         } );
                myRef.set(account).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("johny", "onSuccess: account update with tour to firestore");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("johny", "onFailure: failed upload to firestore");

                    }
                });*/
    }

    private String getCompleteAddressString(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
              //  Log.d("johny", strReturnedAddress.toString());
            } else {
              //  Log.d("johny", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
         //   Log.d("johny", "Canont get Address!");
        }
        return strAdd;
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
        map_IMG_zoomIn.setOnClickListener(myViewLister);
        map_IMG_zoomOut.setOnClickListener(myViewLister);

    }

    private View.OnClickListener myViewLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonClicked(view);
        }
    };

    private void buttonClicked(View view) {
        if (view.getTag().toString().equals("start")) {
            startService();
        } else if (view.getTag().toString().equals("stop")) {
            stopService();
            validateButtons();

        } else if (view.getTag().toString().equals("pause")) {
            pauseService();
        } else if (view.getTag().toString().equals("direction")) {

            getDestinationInfo(destination);
        } else if (view.getTag().toString().equals("elite")) {
            changeMapType();

        } else if (view.getTag().toString().equals("gps")) {
            validateButtons();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

        } else if (view.getTag().toString().equals("search")) {
            searchPlace();

        } else if (view.getTag().toString().equals("zoom_in")) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());

        } else if (view.getTag().toString().equals("zoom_out")) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());

        }
//
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
                createTour();
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

    private void createTour() {
        Date currentTime = Calendar.getInstance().getTime();
        myTour = new Tour(currentTime,0,sourceLocation,null,0);
    }

    private void addDestPlaceInMap(Intent data) {
        mMap.clear();
        Place place = Autocomplete.getPlaceFromIntent(data);
        Log.d("johny", "Place: " + place.getName() + ", " + place.getId());
        destination = place.getLatLng();
        /*myTour.setDest(getCompleteAddressString(destination.latitude,destination.longitude));
        Log.d("johny", "addDestPlaceInMap: source = " + myTour.getSource() + " dest = " + myTour.getDest());*/
        String dest = getCompleteAddressString(destination.latitude,destination.longitude);
        myTour.setDest(dest);
        Log.d("johny", "addDestPlaceInMap:  dest is " + myTour.getDest());
      //  getUserFromFB();
        map_EDT_place_autocomplete.setText(place.getAddress());
        place2 = new MarkerOptions().position(place.getLatLng()).title("Dest");
        mMap.addMarker(place2);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
    }

    private void validateButtons() {
        if (isMyServiceRunning(LocationService.class)) {
            map_BTN_start.setEnabled(false);
            map_BTN_pause.setEnabled(true);
            map_BTN_stop.setEnabled(true);
            map_BTN_gps.setEnabled(true);
        } else {
            map_BTN_start.setEnabled(true);
            map_BTN_gps.setEnabled(false);
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


        if (counter > 0)
            return true;
        return false;
    }

    private void findViews() {
        map_BTN_directions = findViewById(R.id.map_BTN_directions);
        map_BTN_gps = findViewById(R.id.map_BTN_gps);
        map_BTN_start = findViewById(R.id.map_BTN_start);
        map_BTN_stop = findViewById(R.id.map_BTN_stop);
        map_EDT_place_autocomplete = findViewById(R.id.map_EDT_place_autocomplete);
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_EDT_place_autocomplete);
        map_LBL_distance = findViewById(R.id.map_LBL_distance);
        map_LBL_time = findViewById(R.id.map_LBL_time);
        map_BTN_state_elite = findViewById(R.id.map_BTN_state_elite);
        map_BTN_pause = findViewById(R.id.map_BTN_pause);
        map_IMG_zoomIn = findViewById(R.id.map_IMG_zoomIn);
        map_IMG_zoomOut = findViewById(R.id.map_IMG_zoomOut);
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
                        Toast.makeText(getApplicationContext(), "Error in find routes", Toast.LENGTH_SHORT).show();                    }
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

            mMap.setMyLocationEnabled(true);



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
                        }
                }
        }


}