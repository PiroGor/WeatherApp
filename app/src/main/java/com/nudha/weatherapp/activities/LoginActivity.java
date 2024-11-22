package com.nudha.weatherapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nudha.weatherapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, emailEditText;
    private TextView registerTextView;
    private Button loginButton;
    private boolean isRegistering = false;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registerTextView = findViewById(R.id.registerTextView);
        loginButton = findViewById(R.id.loginButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Processing a click on the "Register" text
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRegistering) {
                    usernameEditText.setVisibility(View.VISIBLE);
                    registerTextView.setVisibility(View.GONE);
                    loginButton.setText("Register");
                    isRegistering = true;
                }
            }
        });

        // Processing a click on the "Log in" button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                // Compliance check
                if (email.isEmpty() || password.isEmpty() || (isRegistering && username.isEmpty())) {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!isEmailValid(email)) {
                    Toast.makeText(LoginActivity.this, "Incorrect email format", Toast.LENGTH_SHORT).show();
                } else if (!isPasswordValid(password)) {
                    Toast.makeText(LoginActivity.this, "Incorrect password format: minimum 8 characters, 1 digit and 1 special. symbol", Toast.LENGTH_SHORT).show();
                } else if (isRegistering && !isUsernameValid(username)) {
                    Toast.makeText(LoginActivity.this, "Incorrect name format", Toast.LENGTH_SHORT).show();
                } else if (isRegistering) {
                    // User registration
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        }
                    });

                }else{
                    // User verification at login
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Incorrect login or password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                navigateToMainActivity();
                            }
                        }
                    });
                }
            }
        });
    }

    // Method for switching to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Checking login validity
    private boolean isUsernameValid(String username) {
        return username.length() >= 4 && username.length() <= 16;
    }

    // Check password validity
    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[!@#$%^&*()_+=<>?])[a-zA-Z0-9!@#$%^&*()_+=<>?]{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // Check email validity
    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
