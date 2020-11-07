package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private Button addReminderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        setupListener();
        checkIfDeviceTokenIsEmpty();
    }

    private void checkIfDeviceTokenIsEmpty() {
        String deviceToken = getSharedPreferences("_", MODE_PRIVATE).getString("deviceToken", "empty");

        if (deviceToken.equals("empty")) { updateDeviceToken(); }
    }

    private void updateDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String token = instanceIdResult.getToken();
            getSharedPreferences("_", MODE_PRIVATE).edit().putString("deviceToken", token).apply();
        }).addOnFailureListener(error -> {
            // retry if failed
            updateDeviceToken();
        });
    }

    private void initializeUI() {
        addReminderButton = findViewById(R.id.add_reminder_button);
    }

    private void setupListener() {
        addReminderButton.setOnClickListener(view ->
                startActivity(new Intent(this, AddReminderActivity.class)));
    }
}