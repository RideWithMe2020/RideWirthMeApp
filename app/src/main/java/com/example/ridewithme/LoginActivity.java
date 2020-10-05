package com.example.ridewithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
    private TextView login_LBL_errorMessage;
    private HashSet<Account> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("account");

        //***********johny***********//
       login_BTN_login.setOnClickListener(fillAccount);
       login_BTN_register.setOnClickListener(fillAccount);


    }


    private View.OnClickListener fillAccount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonClicked(view);
        }
    };

    private void buttonClicked(View view) {

        Log.d("johny", "buttonClicked: clicked " + view.getTag().toString());

        if (view.getTag().toString().equals("login")) {
            if (checkUserValid()) {
                Intent intent = new Intent(this, StartUp.class);
                this.startActivity(intent);
                finish();
            } else {
                login_LBL_errorMessage.setText("Username or password are invalid");
                Log.d("stas","error message");
            }

        } else if ((view.getTag().toString().equals("register"))) {
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
        }

    }

    private boolean checkUserValid() {
        /*for (Account account: accounts) {
            if((login_EDT_username.getText().toString().equals(account.getEmail())) && (login_EDT_password.getText().toString().equals(account.getPassword()))){
                return true;
            }
        }
        return false;*/
        return  true;
    }


    private void findViews() {
        login_EDT_username = findViewById(R.id.login_EDT_username);
        login_EDT_password = findViewById(R.id.login_EDT_password);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_register = findViewById(R.id.login_BTN_register);
        login_LBL_errorMessage = findViewById(R.id.login_LBL_errorMessage);

    }


}