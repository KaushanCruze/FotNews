package com.example.fotnews;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//
        // Check if user is already logged in
        if (currentUser == null) {
            // Not logged in → go to Login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // prevent returning to MainActivity
        } else {
            // Already logged in → go to Home activity (or main content)
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }
}

