package com.example.ridewithme.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ridewithme.R;

public class DataActivity extends AppCompatActivity {

    private EditText data_EDT_name;
    private EditText data_EDT_password;
    private EditText data_EDT_email;
    private Button data_BTN_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        findViews();
    }

    private void findViews() {
        data_EDT_name.findViewById(R.id.data_EDT_name);
        data_EDT_password.findViewById(R.id.data_EDT_password);
        data_EDT_email.findViewById(R.id.data_EDT_email);
        data_BTN_save.findViewById(R.id.data_BTN_save);

    }
}