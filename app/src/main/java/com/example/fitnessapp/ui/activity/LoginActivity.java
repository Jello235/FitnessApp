package com.example.fitnessapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.fitnessapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLoginBtn;
    private TextView mCreateBtn;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private TextView forgotTextLink;

    @Override
    public void onStart() {
        super.onStart();
        checkIfUserAlreadyLoginBefore();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUI();
        setupListeners();
    }

    private void checkIfUserAlreadyLoginBefore() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            showToastMessage("Login successful!");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Animatoo.animateFade(LoginActivity.this);
        }
    }

    private void initializeUI() {
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginButton);
        mCreateBtn = findViewById(R.id.login);
        forgotTextLink = findViewById(R.id.forgotPassword);
    }

    private void setupListeners() {
        mLoginBtn.setOnClickListener(view -> {
            hideKeyboard(view);
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is required.");
            } else if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is required.");
            } else if (password.length() < 8) {
                mPassword.setError("Password Must be >= 8 Characters");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                signInWithEmailAndPassword(email, password);
            }
        });

        mCreateBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        forgotTextLink.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            Animatoo.animateSlideRight(LoginActivity.this);
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                showToastMessage("Error! " + task.getException().getMessage());
            }
        });
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}