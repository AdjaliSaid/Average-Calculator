package com.example.calculator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnGoToSignup;
    EditText etEmail, etPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToSignup = findViewById(R.id.btnGoToSignup);

        dbHelper = new DatabaseHelper(this);
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean isValid = true;

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
                if (dbHelper.checkUser(email, password)) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "This account doan't exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGoToSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }
}
