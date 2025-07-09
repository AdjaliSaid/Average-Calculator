package com.example.calculator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    Button btnSignup, btnGoToLogin;
    EditText  etEmail, etPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        dbHelper = new DatabaseHelper(this); // Initialize DB
        btnSignup.setOnClickListener(v -> {
            boolean isValid = true;
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate inputs
            if (email.isEmpty()) {
                etEmail.setBackgroundColor(Color.parseColor("#FFCDD2"));
                isValid = false;
            } else {
                etEmail.setBackgroundColor(Color.TRANSPARENT);
            }

            if (password.isEmpty()) {
                etPassword.setBackgroundColor(Color.parseColor("#FFCDD2"));
                isValid = false;
            } else {
                etPassword.setBackgroundColor(Color.TRANSPARENT);
            }

            if (isValid) {
                if (!dbHelper.userExists(email)) {
                    boolean inserted = dbHelper.insertUser(email, password);
                    dbHelper.displayAllUsers();
                    if (inserted) {
                        Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}
