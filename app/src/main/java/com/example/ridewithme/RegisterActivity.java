package com.example.ridewithme;


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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedHashSet;
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
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private String userID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        checkValueOfEDT(register_EDT_name,register_EDT_password,register_EDT_email);
        firebaseAuth= FirebaseAuth.getInstance();
        register_BTN_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signup();
            }
        });

        /*if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),StartUp.class));
                finish();
        }*/


        // addAccountsToFB();
//        register_BTN_create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signup();
//            }
//
//
//        });

     /*   register_TXT_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });*/
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    private Set<Account> getSetFromFB() {
        // import set from FB if not exsist create new LinkedHashSet
        //  DatabaseReference myRef = database.getReference("account");
        // set = myref.getSet();
        if(set==null)
        {
            set = new LinkedHashSet<Account>();
        }
        Log.d("johny", "getSetFromFB: create set");
        return  set;
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


        Account user = new Account(name,email,password,null);

        Log.d("johny", "createAccount: create acounts success " + user.getName());
        return  user;
    }

    public void checkValueOfEDT(EditText name, EditText password, EditText email) {
        if (isEmpty(password) || password.length() < 4 || password.length() > 10) {
            password.setError("between 4 and 10 alphanumeric characters");
        }
        if (isEmpty(name) || name.length() < 3) {
            register_EDT_name.setError("at least 3 characters");
        }
        if (isEmail(email) == false) {
            email.setError("Enter valid email!");
        }
   
    }

    private void addAccountToSet(Set<Account> set, Account inputAccount) {
        set.add(inputAccount);
        Log.d("johny", "addAccountToSet: add acount to the set");
    }

    private void addAccountsToFB() {
    }


    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        register_BTN_create.setEnabled(false);
/*
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();*/
            register_PGB_pgb.setVisibility(View.VISIBLE);

/*        String name = register_EDT_name.getText().toString();
        String email = register_EDT_email.getText().toString();
        String password = register_EDT_password.getText().toString();*/

        // TODO: Implement your own signup logic here.
        account = createAccount();
        Log.d("johny", "signup:  account name is" + account.getName());

        firebaseAuth.createUserWithEmailAndPassword(account.getEmail(),account.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful())
            {
                Toast.makeText(getApplicationContext(),"User created in FB",Toast.LENGTH_SHORT).show();
                userID = firebaseAuth.getCurrentUser().getUid();
                 DocumentReference myRef = fStore.collection("users").document(userID);
                onSignupSuccess();
            }
            else {
                onSignupFailed();
            }

            }
        });

   /*     set = getSetFromFB();
        addAccountToSet(set,account);
        // addAccountsToFB();
        if(set!=null)
        {
            for (Account user:set)
            {
                Log.d("johny", "signup:  name is" + user.getName());
            }
        }
*/

     /* handler.postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }


    public void onSignupSuccess() {
        register_BTN_create.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        register_BTN_create.setEnabled(true);
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
