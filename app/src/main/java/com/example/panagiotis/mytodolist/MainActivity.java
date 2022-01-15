package com.example.panagiotis.mytodolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextBDate;

    private Button buttonSignup;

    private TextView textViewSignin;

    private ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    //initializing firebase ref
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing firebase ref object
        ref = FirebaseDatabase.getInstance().getReference();

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSurname = (EditText) findViewById(R.id.editTextSurname);
        editTextBDate = (EditText) findViewById(R.id.editTextBDate);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){

        //getting user inputs from edit texts
        final String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        final String name  = editTextName.getText().toString().trim();
        final String surname = editTextSurname.getText().toString().trim();
        final String bDate  = editTextBDate.getText().toString().trim();

        //checking if inputs are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter Name",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(surname)){
            Toast.makeText(this,"Please enter Surname",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(bDate)){
            Toast.makeText(this,"Please enter Birth Date",Toast.LENGTH_LONG).show();
            return;
        }

        //if inputs are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //create obj with user information
                            UserInformation userInformation = new UserInformation(email, name, surname, bDate);

                            //add user information to database
                            if(user != null){
                                ref.child("users").child(user.getUid()).setValue(userInformation);
                            }else{
                                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_LONG).show();
                            }

                            finish();
                            startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
                        }else{
                            //display some message here
                            Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }

        if(view == textViewSignin){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}