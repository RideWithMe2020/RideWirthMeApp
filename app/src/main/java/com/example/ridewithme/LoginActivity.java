package com.example.ridewithme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.ridewithme.Classes.Account;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;

public class LoginActivity extends AppCompatActivity {

    private EditText login_EDT_username;
    private EditText login_EDT_password;
    private MaterialButton login_BTN_login;
    private MaterialButton login_BTN_register;
    private HashSet<Account> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("account");


//        login_EDT_username.setOnClickListener(fillAccount);
//        login_EDT_password.setOnClickListener(fillAccount);


    }


    private View.OnClickListener fillAccount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonClicked(view);
        }
    };

    private void buttonClicked(View view) {


        if(view.getTag().equals("login")){


        }else if (view.getTag().equals("register")){

        }

    }

    private void getAccountsFromSP() {


    }


    private void findViews() {
        login_EDT_username = findViewById(R.id.login_EDT_username);
        login_EDT_password = findViewById(R.id.login_EDT_password);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_register = findViewById(R.id.login_BTN_register);


    }


}