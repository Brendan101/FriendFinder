package com.brenardo9956gmail.friendfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "FirebaseAuth: ";
    public static final String NEW_USER = "new";
    public static final String RETURN_USER = "return";

    EditText nameEdit, emailEdit, passwordEdit;
    Button login, register;

    private String name, email, password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEdit = (EditText) findViewById(R.id.name);
        emailEdit = (EditText) findViewById(R.id.email);
        passwordEdit = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when this activity leaves focus, sign the user out
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        login.setOnClickListener(null);
        register.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {

        boolean infoEntered = true;

        name = nameEdit.getText().toString();
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();

        //Minor validation on email and password (both required)
        if(email.equals("")){
            infoEntered = false;
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        if(password.equals("")){
            infoEntered = false;
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
        }

        if(infoEntered) {

            switch (v.getId()) {

                case R.id.login:
                    signInUser(email, password);
                    break;

                case R.id.register:
                    //Minor validation on name field
                    if (!name.equals("")) {
                        registerUser(name, email, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }

        }

    }

    private void registerUser(String username, String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration failed",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            onRegister();
                        }

                    }
                });

    }

    private void signInUser(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            onLogIn();
                        }

                    }
                });

    }

    private void onRegister(){

        //user is logged in
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {

            //send this over to the maps activity
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("userType", NEW_USER);
            mapIntent.putExtra("uid", mAuth.getCurrentUser().getUid());
            mapIntent.putExtra("name", name);
            mapIntent.putExtra("email", email);
            mapIntent.putExtra("fList", email);
            startActivity(mapIntent);

        }

    }

    private void onLogIn(){

        //user is logged in
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){

            //send this over to the maps activity
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("userType", RETURN_USER);
            mapIntent.putExtra("uid", mAuth.getCurrentUser().getUid());
            startActivity(mapIntent);

        }

    }
}