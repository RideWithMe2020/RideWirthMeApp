package com.example.ridewithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ridewithme.activities.MapActivity;
import com.google.android.material.button.MaterialButton;

public class StartUp extends AppCompatActivity {

    private MaterialButton Start_BTN_bikeRide;
    private MaterialButton Start_BTN_goal;
    private MaterialButton Start_BTN_history;
    private MaterialButton Start_BTN_statistics;
    private MaterialButton Start_BTN_settings;
    private MaterialButton Start_BTN_friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        findViews();
        Start_BTN_bikeRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                StartUp.this.startActivity(intent);
            }
        });


    }

    private void findViews(){
        Start_BTN_bikeRide = findViewById(R.id.Start_BTN_bikeRide);
        Start_BTN_goal = findViewById(R.id.Start_BTN_goal);
        Start_BTN_history = findViewById(R.id.Start_BTN_history);
        Start_BTN_statistics = findViewById(R.id.Start_BTN_statistics);
        Start_BTN_settings = findViewById(R.id.Start_BTN_settings);
        Start_BTN_friends = findViewById(R.id.Start_BTN_friends);
    }



}