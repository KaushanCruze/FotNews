package com.example.fotnews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private LinearLayout newsContainer;
    private ImageView profileAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // your main layout XML

        initializeFirebase();
        initializeViews();
        setupClickListeners();
        loadNewsArticles();
    }

    private void initializeFirebase() {
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
    }

    private void initializeViews() {
        newsContainer = findViewById(R.id.newsContainer);
        profileAvatar = findViewById(R.id.profileAvatar);
    }

    private void setupClickListeners() {
        profileAvatar.setOnClickListener(v -> navigateToSettings());
    }

    private void loadNewsArticles() {
        firestore.collection("news")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        showToast("No news found.");
                        return;
                    }
                    // Clear any existing dynamic cards, keeping your static ones intact
                    // Or if you want to clear all:
                    // newsContainer.removeAllViews();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String title = document.getString("title");
                        String date = document.getString("date");
                        String content = document.getString("content");
                        String imageUrl = document.getString("imageUrl");
                        addNewsCard(title, date, content, imageUrl);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading news", e);
                    showToast("Failed to load news");
                });
    }

    // Dynamically create and add news cards matching your XML card style
    private void addNewsCard(String title, String date, String content, String imageUrl) {
        // Create CardView
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12f);
        cardView.setCardElevation(6f);
        cardView.setUseCompatPadding(true);

        // Inner LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(16, 16, 16, 16);
        linearLayout.setBackgroundColor(Color.WHITE);

        // Title TextView
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18f);
        titleView.setTextColor(Color.BLACK);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = 8;
        titleView.setLayoutParams(titleParams);

        // Date TextView
        TextView dateView = new TextView(this);
        dateView.setText(date);
        dateView.setTextSize(12f);
        dateView.setTextColor(Color.parseColor("#666666"));
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dateParams.bottomMargin = 8;
        dateView.setLayoutParams(dateParams);

        // ImageView
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                400); // approx 200dp in pixels ~ use 400px for a crisp image
        imageParams.bottomMargin = 8;
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imageView);
        } else {
            imageView.setVisibility(ImageView.GONE);
        }

        // Content TextView
        TextView contentView = new TextView(this);
        contentView.setText(content);
        contentView.setTextSize(14f);
        contentView.setTextColor(Color.parseColor("#333333"));

        // Add all views to linear layout
        linearLayout.addView(titleView);
        linearLayout.addView(dateView);
        if (imageView.getVisibility() != ImageView.GONE) {
            linearLayout.addView(imageView);
        }
        linearLayout.addView(contentView);

        // Add linear layout to cardView
        cardView.addView(linearLayout);

        // Add cardView to newsContainer
        newsContainer.addView(cardView);
    }

    private void navigateToSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
