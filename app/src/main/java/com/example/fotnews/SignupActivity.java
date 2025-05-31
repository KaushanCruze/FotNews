package com.example.fotnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private LinearLayout signupButton;
    private TextView signInRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password1);
        confirmPasswordEditText = findViewById(R.id.password2);
        signupButton = findViewById(R.id.back3);
        signInRedirect = findViewById(R.id.signin);

        signupButton.setOnClickListener(v -> registerUser());

        signInRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Input validation
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Minimum password length is 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Show loading indicator
        signupButton.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            // Update user profile with username
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            saveUserToFirestore(user, username);
                                        } else {
                                            handleError("Failed to update profile: " +
                                                    profileTask.getException().getMessage());
                                        }
                                    });
                        }
                    } else {
                        handleError("Registration failed: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user, String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("email", user.getEmail());
        userData.put("username", username);
        userData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    handleError("Failed to save user data: " + e.getMessage());
                });
    }

    private void handleError(String message) {
        signupButton.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e("SignupActivity", message);
    }
}