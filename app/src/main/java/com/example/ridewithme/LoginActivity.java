package com.example.ridewithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridewithme.Classes.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;

public class LoginActivity extends AppCompatActivity {

    private EditText login_EDT_email;
    private EditText login_EDT_password;
    private MaterialButton login_BTN_login;
    private MaterialButton login_BTN_register;
    private TextView login_LBL_errorMessage;
    private ProgressBar login_PGB_pgb;
    private HashSet<Account> accounts;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        firebaseAuth = FirebaseAuth.getInstance();
       // DatabaseReference myRef = database.getReference("account");
        login_PGB_pgb.setVisibility(View.GONE);

        checkVaildLBl(login_EDT_email,login_EDT_password);
        //***********johny***********//
        login_BTN_login.setOnClickListener(fillAccount);
        login_BTN_register.setOnClickListener(fillAccount);



    }
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    private void checkVaildLBl(EditText email, EditText password) {
        if (isEmpty(password) || password.length() < 4 || password.length() > 10) {
            password.setError("between 4 and 10 alphanumeric characters");
        }

        if (isEmail(email) == false) {
            email.setError("Enter valid email!");
        }
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
            checkUserValid();

        } else if ((view.getTag().toString().equals("register"))) {
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
        }

    }

    private void checkUserValid() {
       login_PGB_pgb.setVisibility(View.VISIBLE);
       String email = login_EDT_email.getText().toString();
       String password = login_EDT_password.getText().toString();
       firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful())
               {
                   Toast.makeText(getApplicationContext(),"Login successfully",Toast.LENGTH_SHORT).show();
                   onLoginSuccess();

               }
               else {
                   onLoginFailed( task);
               }
               
           }
       });

    }

    private void onLoginFailed(Task<AuthResult> task) {
        Toast.makeText(getBaseContext(), "login failed -> " +task.getException() , Toast.LENGTH_LONG).show();
        login_BTN_login.setEnabled(true);
        login_PGB_pgb.setVisibility(View.GONE);

    }

    private void onLoginSuccess() {
        Intent intent = new Intent(this, StartUp.class);
        this.startActivity(intent);
        finish();
    }


    private void findViews() {
        login_EDT_email = findViewById(R.id.login_EDT_email);
        login_EDT_password = findViewById(R.id.login_EDT_password);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_register = findViewById(R.id.login_BTN_register);
        login_LBL_errorMessage = findViewById(R.id.login_LBL_errorMessage);
        login_PGB_pgb  = findViewById(R.id.login_PGB_pgb);
    }


}