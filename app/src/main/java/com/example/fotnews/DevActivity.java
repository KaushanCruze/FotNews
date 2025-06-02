package com.example.fotnews;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DevActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        // Find the Exit button by id
        Button exitButton = findViewById(R.id.exitButton);

        // Set click listener to finish() activity (go back)
        exitButton.setOnClickListener(v -> finish());
    }
}
