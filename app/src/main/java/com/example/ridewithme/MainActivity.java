package com.example.ridewithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends AppCompatActivity {
   final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openActivity();
            }
        }, 1200);
    }

    private void openActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        this.startActivity(intent);
        finish();
    }
}