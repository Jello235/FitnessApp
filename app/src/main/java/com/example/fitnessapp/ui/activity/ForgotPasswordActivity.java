package com.example.fitnessapp.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.fitnessapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotPasswordEmail;
    private Button retrievePasswordButton;
    private TextView registerText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initializeUI();
        setupListeners();
    }

    private void retrievePasswordLogic() {
        final String email = forgotPasswordEmail.getText().toString();
        Boolean validEmail = isValidEmail(email);

        if (!validEmail) {
            toastMessage("Invalid email");
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    toastMessage("Password reset email sent to " + email);
                    progressBar.setVisibility(View.GONE);
                    onBackPressed();
                    Animatoo.animateSlideLeft(ForgotPasswordActivity.this);
                } else {
                    try { throw task.getException(); }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                        toastMessage("Error: Malformed Email");
                    } catch (FirebaseAuthInvalidUserException invalidEmail) {
                        String errorCode = invalidEmail.getErrorCode();
                        if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                            toastMessage("No matching account found");
                        } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                            toastMessage("User account has been disabled");
                        } else if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                            toastMessage("User account is already in use");
                        } else {
                            toastMessage("Login failed! Error: Incorrect Email");
                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        toastMessage("Error sending password reset email");
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initializeUI() {
        forgotPasswordEmail = findViewById(R.id.forgot_password_email);
        retrievePasswordButton = findViewById(R.id.retrieve_password_btn);
        registerText = findViewById(R.id.registerTxt);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        registerText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void setupListeners() {
        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Animatoo.animateSlideLeft(ForgotPasswordActivity.this);
        });

        retrievePasswordButton.setOnClickListener(view -> retrievePasswordLogic());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(this);
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
