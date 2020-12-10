package com.example.fitnessapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.fitnessapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText mFullName, mEmail, mPassword, mPhone, mConfirmPass;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore fStore;
    private CountryCodePicker ccp;
    private Button mShowHidePasswordBtn, mShowHideConfirmPasswordBtn;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(mPhone);
        mRegisterBtn = findViewById(R.id.loginButton);
        mLoginBtn = findViewById(R.id.login);
        mLoginBtn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        mShowHidePasswordBtn = findViewById(R.id.showHidePasswordBtn);
        mShowHideConfirmPasswordBtn = findViewById(R.id.showHideConfirmPasswordBtn);
        mConfirmPass = findViewById(R.id.confirmPass);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        mRegisterBtn.setOnClickListener(view -> {
            hideKeyboard(view);
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String fullName = mFullName.getText().toString();
            String phone = ccp.getFullNumberWithPlus();
            String confirmPass = mConfirmPass.getText().toString().trim();

            if (TextUtils.isEmpty(fullName)) {
                mFullName.setError("Username is required");
            } else if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is required.");
            } else if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is required.");
            } else if (TextUtils.isEmpty(confirmPass)) {
                mPassword.setError("Confirmed password is required.");
            } else if (!password.equals(confirmPass)) {
                mConfirmPass.setError("Your passwords do not match");
            } else if (password.length() < 8) {
                mPassword.setError("Password Must be >= 8 Characters");
            } else if (!ccp.isValidFullNumber()) {
                mPhone.setError("Phone number is not valid");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                createUserWithEmailAndPassword(email, password, fullName, phone);
            }
        });

        mShowHidePasswordBtn.setOnClickListener(view -> {
            if (mShowHidePasswordBtn.getText().toString().equals("Show")) {
                mPassword.setTransformationMethod(null);
                mShowHidePasswordBtn.setText("Hide");
            } else {
                mPassword.setTransformationMethod(new PasswordTransformationMethod());
                mShowHidePasswordBtn.setText("Show");
            }
        });

        mShowHideConfirmPasswordBtn.setOnClickListener(view -> {
            if (mShowHideConfirmPasswordBtn.getText().toString().equals("Show")) {
                mConfirmPass.setTransformationMethod(null);
                mShowHideConfirmPasswordBtn.setText("Hide");
            } else {
                mConfirmPass.setTransformationMethod(new PasswordTransformationMethod());
                mShowHideConfirmPasswordBtn.setText("Show");
            }
        });

        mLoginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Animatoo.animateSlideRight(RegisterActivity.this);
        });
    }

    private void createUserWithEmailAndPassword(String email, String password, String fullName, String phone) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Send Email Verification Link
                FirebaseUser fUser = fAuth.getCurrentUser();
                fUser.sendEmailVerification().addOnSuccessListener(aVoid -> {
                    showToastMessage("Verification Email has been sent");
                }).addOnFailureListener(e -> {
                    showToastMessage(e.getMessage());
                });

                showToastMessage("User Created");
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fStore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("fName", fullName);
                user.put("email", email);
                user.put("phone", phone);
                documentReference.set(user).addOnSuccessListener(aVoid -> {
                    Log.d("RegisterActivity", "onSuccess: User profile is created for " + userID);
                }).addOnFailureListener(e -> {
                    Log.d("RegisterActivity", e.getMessage());
                });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Animatoo.animateSlideRight(RegisterActivity.this);
    }
}