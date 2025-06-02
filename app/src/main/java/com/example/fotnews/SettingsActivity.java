package com.example.fotnews;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backImageView;
    private TextView emailTextView, usernameTextView;
    private ListenerRegistration userInfoListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize Views
        backImageView = findViewById(R.id.back);
        emailTextView = findViewById(R.id.email2);
        usernameTextView = findViewById(R.id.username1);
        View devInfo = findViewById(R.id.dev);
        LinearLayout signOutLayout = findViewById(R.id.signout);
        LinearLayout editInfoLayout = findViewById(R.id.edit_info);

        // Back navigation
        backImageView.setOnClickListener(v -> finish());

        // Navigate to Developer Info
        devInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DevActivity.class);
            startActivity(intent);
        });

        // Sign out - show confirmation dialog instead of immediate sign out
        signOutLayout.setOnClickListener(v -> showSignOutDialog());

        // Edit info
        editInfoLayout.setOnClickListener(v -> showEditInfoDialog());

        // Display current user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName(); // Might be null

            emailTextView.setText("Email: " + (email != null ? email : "-"));
            usernameTextView.setText(name != null ? "Username: " + name : "Username: -");
        } else {
            emailTextView.setText("Not logged in");
            usernameTextView.setText("-");
        }
    }

    // New method to show the sign-out confirmation dialog using your custom XML layout
    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.signout_confirm, null); // <-- your dialog XML filename here
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        LinearLayout btnYes = dialogView.findViewById(R.id.btnYes);
        LinearLayout btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(v -> {
            // Sign out user and go to LoginActivity
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
    }

    private void showEditInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_edit, null);
        builder.setView(dialogView);

        EditText editUsername = dialogView.findViewById(R.id.editUsername);
        LinearLayout btnOk = dialogView.findViewById(R.id.btn_edit_Confirm);
        LinearLayout btnCancel = dialogView.findViewById(R.id.btn_edit_deny);

        AlertDialog dialog = builder.create();

        btnOk.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            updateUser(newUsername);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
    }


    private void updateUser(String username) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
                            Toast.makeText(this, "Failed to listen to user data.", Toast.LENGTH_SHORT).show();
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
