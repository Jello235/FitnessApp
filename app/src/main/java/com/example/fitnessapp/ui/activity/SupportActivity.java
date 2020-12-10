package com.example.fitnessapp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.fitnessapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SupportActivity extends AppCompatActivity {

    private final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = mFirebaseDatabase.getReference();
    private EditText feedback_subject, feedback_message;
    private Button send_feedback_button;
    private Toolbar myToolBar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        initializeUI();
        setupActionBar();
        setupListeners();
    }

    private void initializeUI() {
        feedback_subject = findViewById(R.id.feedback_subject);
        feedback_message = findViewById(R.id.feedback_message);
        send_feedback_button = findViewById(R.id.send_feedback_btn);
        progressBar = findViewById(R.id.progressBar);
        myToolBar = findViewById(R.id.support_toolbar);
    }

    private void setupActionBar() {
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupListeners() {
        send_feedback_button.setOnClickListener(view -> {
            String subjectInput = feedback_subject.getText().toString();
            Boolean validSubject = isValidSubject(subjectInput);
            String messageInput = feedback_message.getText().toString();
            Boolean validMessage = isValidMessage(messageInput);

            if (!validSubject) {
                toastMessage("Subject is empty!");
            } else if (!validMessage) {
                toastMessage("Message is empty!");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String currentTime = sdf.format(new Date());
                String timestamp = currentTime;
                String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ref.child("Feedback Messages").child(user_uid).child(timestamp).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                ref.child("Feedback Messages").child(user_uid).child(timestamp).child("subject").setValue(subjectInput);
                ref.child("Feedback Messages").child(user_uid).child(timestamp).child("message").setValue(messageInput);
                toastMessage("Support Message Sent");
                progressBar.setVisibility(View.GONE);
                onBackPressed();
                Animatoo.animateSlideRight(SupportActivity.this);
            }
        });
    }

    private static boolean isValidSubject(CharSequence target) {
        return !TextUtils.isEmpty(target);
    }

    private static boolean isValidMessage(CharSequence target) {
        return !TextUtils.isEmpty(target);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
