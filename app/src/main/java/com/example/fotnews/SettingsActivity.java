package com.example.fotnews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backImageView;
    private ListenerRegistration userInfoListener;

    private TextView emailTextView,usernameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize back image
        backImageView = findViewById(R.id.back);

        // Handle back navigation
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to previous activity (MainActivity)
                finish(); // This closes the current activity and goes back
            }
        });
        emailTextView = findViewById(R.id.email2);
        usernameTextView = findViewById(R.id.username1);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName(); // May be null if not set

            emailTextView.setText("Email: "+email);
            usernameTextView.setText(name != null ? "Username: "+name : "No username");
        } else {
            emailTextView.setText("Not logged in");
            usernameTextView.setText("-");
        }
        View devInfo = findViewById(R.id.dev);


        devInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, DevActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout signOutLayout = findViewById(R.id.signout);
        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUser();
            }
        });

        LinearLayout editInfo = findViewById(R.id.edit_info);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditInfoDialog();
            }
        });


    }


// Inside your activity class:

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        // After sign out, redirect user to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish();
    }
    private void showEditInfoDialog() {
        // Inflate your dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.activity_edit, null);

        // Find views inside the dialog if you want to pre-fill or listen
        EditText editUsername = dialogView.findViewById(R.id.editUsername);


        // Optional: pre-fill username/email from your data source

        // Build AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)

                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle saving updated info here
                        String newUsername = editUsername.getText().toString().trim();


                        // TODO: Validate and update your user info here
                        updateUser(newUsername);
//
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();


        dialog.show();
    }

    private void updateUser(String username) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            String msg = "User not logged in";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//            Log.d("UserUpdate", msg);
            return;
        }

        String uid = user.getUid();
        Map<String, Object> updates = new HashMap<>();

        if (!username.isEmpty()) {
            updates.put("username", username);
        }

        if (updates.isEmpty()) {
            String msg = "No changes to update";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//            Log.d("UserUpdate", msg);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    String msg = "Username updated successfully";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//                    Log.d("UserUpdate", msg);
                })
                .addOnFailureListener(e -> {
                    String msg = "Failed to update username: " + e.getMessage();
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//                    Log.e("UserUpdate", msg);
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userInfoListener = db.collection("users")
                    .document(user.getUid())
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Listen failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String email = user.getEmail();
                            String username = documentSnapshot.getString("username");

                            emailTextView.setText("Email: " + (email != null ? email : "-"));
                            usernameTextView.setText("Username: " + (username != null ? username : "-"));
                        }
                    });
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (userInfoListener != null) {
            userInfoListener.remove();
        }
    }

}


