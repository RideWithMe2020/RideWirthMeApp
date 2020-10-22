package com.example.ridewithme.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ridewithme.Classes.Account;
import com.example.ridewithme.Classes.Tour;
import com.example.ridewithme.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private FirebaseDatabase database;
    private Account account;

    private BarChart Statistics_BARCHART_diagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Statistics_BARCHART_diagram = findViewById(R.id.Statistics_BARCHART_diagram);
        getUserFromFB();
    }


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
                account = dataSnapshot.getValue(Account.class);
                getStatsFB(account);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pttt", "Failed to read value.", error.toException());
            }
        });


    }

    private void getStatsFB(Account account) {
        ArrayList<Tour> tours = account.getTours();
        ArrayList<BarEntry> stats = new ArrayList<>();
        for (int i = 0; i <= tours.size() - 1; i++) {
            float km = Float.parseFloat(tours.get(i).getKm());
            stats.add(new BarEntry((float) tours.get(i).getTime_in_minutes(), km));
        }

        BarDataSet barDataSet = new BarDataSet(stats, "KM");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        Statistics_BARCHART_diagram.setFitBars(true);
        Statistics_BARCHART_diagram.setData(barData);
        Statistics_BARCHART_diagram.getDescription().setText("Stats");
        Statistics_BARCHART_diagram.animateY(2000);
    }
}

