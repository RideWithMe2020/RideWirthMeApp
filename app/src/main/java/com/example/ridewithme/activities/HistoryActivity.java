package com.example.ridewithme.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridewithme.Classes.Account;
import com.example.ridewithme.Classes.Adapter_Exercise;
import com.example.ridewithme.Classes.Tour;
import com.example.ridewithme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private FirebaseDatabase database;
    private Account account;
    private TextView history_LBL_history;


    private RecyclerView list_LST_exercises;
    private TextView exercise_LBL_date;
    private TextView exercise_LBL_time;
    private TextView exercise_LBL_source;
    private TextView exercise_LBL_dest;
    private TextView exercise_LBL_speed;
    private TextView exercise_LBL_km;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        findViews();
        getUserFromFB();

    }

    private void findViews() {
        list_LST_exercises = findViewById(R.id.list_LST_exercises);
        exercise_LBL_date = findViewById(R.id.exercise_LBL_date);
        exercise_LBL_time = findViewById(R.id.exercise_LBL_time);
        exercise_LBL_source = findViewById(R.id.exercise_LBL_source);
        exercise_LBL_dest = findViewById(R.id.exercise_LBL_dest);
        exercise_LBL_speed = findViewById(R.id.exercise_LBL_speed);
        exercise_LBL_km = findViewById(R.id.exercise_LBL_km);
    }


//        Log.d("johny", "before getFB");
//        getUserFromFB();
//        Log.d("johny", "after getFB");


    private void getUserFromFB() {
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        Log.d("johny", "in fire base");
        DatabaseReference myDataRef = database.getReference("users");
        String userID = firebaseAuth.getCurrentUser().getUid();
        myDataRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("johny", "onDataChange ,method");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //Tour tour = dataSnapshot.getValue(Tour.class);
                account = dataSnapshot.getValue(Account.class);
                // tours.add(myTour);
                Log.d("johny", "onDataChange: account is " + account.getName() + " " + account.getEmail());
                showHistory(account);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pttt", "Failed to read value.", error.toException());
            }
        });
    }


    private void showHistory(Account thisAccount) {
//        LinearLayout history_LAY_linealLay = (LinearLayout) findViewById(R.id.history_LAY_linealLay);
//        Log.d("stas", "tour is " + thisAccount.getTours().get(0));
//        for (int i = 0; i <= thisAccount.getTours().size()-1; i++) {
//            Tour tour = thisAccount.getTours().get(i);
//            history_LBL_history = new TextView(HistoryActivity.this);
//            history_LBL_history.setText("date: " + tour.getDate() + " source: " + tour.getSource()  + " dest: " + tour.getDest() +
//                    " avg speed: " + tour.getAvg_speed() + " time: " + tour.getTime_in_minutes());
//            history_LBL_history.setTextSize(20);
//            history_LAY_linealLay.addView(history_LBL_history);
        ArrayList<Tour> allTours = thisAccount.getTours();
        Adapter_Exercise adapter_exercise = new Adapter_Exercise(this, allTours);
        list_LST_exercises.setLayoutManager(new LinearLayoutManager(this));
        list_LST_exercises.setAdapter(adapter_exercise);
    }


}