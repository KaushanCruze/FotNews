package com.example.fotnews;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class DevActivity extends AppCompatActivity {

    private LinearLayout backinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        backinfo = findViewById(R.id.back3);

        // Handle back navigation
        backinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to previous activity (MainActivity)
                finish(); // This closes the current activity and goes back
            }
        });
    }


}
