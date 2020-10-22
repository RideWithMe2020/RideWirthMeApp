package com.example.ridewithme.activities;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ridewithme.Classes.Account;
import com.example.ridewithme.Classes.Tour;
import com.example.ridewithme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity
{
    final Handler handler = new Handler(Looper.getMainLooper());
    private EditText register_EDT_name;
    private EditText register_EDT_email;
    private EditText register_EDT_password;
    private MaterialButton register_BTN_create;
    private TextView       register_TXT_login;
    private ProgressBar register_PGB_pgb;
    private Account account;
    private Set<Account> set=null;
    Map<String,Object> list = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private FirebaseDatabase database;
    private String userID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        register_PGB_pgb.setVisibility(View.GONE);

        firebaseAuth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        fStore = FirebaseFirestore.getInstance();
        register_BTN_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signup();
            }
        });
        register_TXT_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private void findViews() {
        register_EDT_name=findViewById(R.id.register_EDT_name);
        register_EDT_email=findViewById(R.id.register_EDT_email);
        register_EDT_password= findViewById(R.id.register_EDT_password);
        register_BTN_create=findViewById(R.id.register_BTN_create);
        register_TXT_login =findViewById(R.id.register_TXT_login);
        register_PGB_pgb = findViewById(R.id.register_PGB_pgb);
    }


    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private Account createAccount() {

        String name=register_EDT_name.getText().toString();
        String email= register_EDT_email.getText().toString();
        String password= register_EDT_password.getText().toString();


        Account user = new Account(name,email,password,new ArrayList<Tour>());

        Log.d("johny", "createAccount: create acounts success " + user.getName());
        return  user;
    }


    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        register_BTN_create.setEnabled(false);
            register_PGB_pgb.setVisibility(View.VISIBLE);
        account = createAccount();
        Log.d("johny", "signup:  account tours is" + account.getTours());
        firebaseAuth.createUserWithEmailAndPassword(account.getEmail(),account.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful())
            {
                Toast.makeText(getApplicationContext(),"User created in FB",Toast.LENGTH_SHORT).show();
                userID = firebaseAuth.getCurrentUser().getUid();
                //-------- insert to Cloud FireBase --> the set of every account with unique userID---------------- //
                DatabaseReference myDataRef = database.getReference("users");
                myDataRef.child(userID).setValue(account);

                onSignupSuccess();
            }
            else {
                onSignupFailed();
            }

            }
        });

    }



    public void onSignupSuccess() {
        register_BTN_create.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        register_BTN_create.setEnabled(true);
        register_PGB_pgb.setVisibility(View.GONE);
    }

    public boolean validate() {
        boolean valid = true;

        String name = register_EDT_name.getText().toString();
        String email = register_EDT_email.getText().toString();
        String password = register_EDT_password.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            register_EDT_name.setError("at least 3 characters");
            valid = false;
        } else {
            register_EDT_name.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            register_EDT_email.setError("enter a valid email address");
            valid = false;
        } else {
            register_EDT_email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            register_EDT_password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            register_EDT_password.setError(null);
        }

        return valid;
    }

}
