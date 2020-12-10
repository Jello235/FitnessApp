package com.example.fitnessapp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.fitnessapp.R;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private CardView workoutPlannerCardView;
    private CardView fitnessLevelCardView;
    private CardView dietAdvisorCardView;
    private CardView settingsCardView;
    private Animation fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        setContentView(R.layout.activity_main);
        checkIfDeviceTokenIsEmpty();
        initiateUI();
        setupListeners();
    }

    private void checkIfDeviceTokenIsEmpty() {
        String deviceToken = getSharedPreferences("_", MODE_PRIVATE).getString("deviceToken", "empty");

        if (deviceToken.equals("empty")) {
            updateDeviceToken();
        }
    }

    private void updateDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String token = instanceIdResult.getToken();
            getSharedPreferences("_", MODE_PRIVATE).edit().putString("deviceToken", token).apply();
        }).addOnFailureListener(error -> {
            updateDeviceToken();  // retry if failed
        });
    }

    private void initiateUI() {
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        workoutPlannerCardView = findViewById(R.id.workout_planner_cardview);
        fitnessLevelCardView = findViewById(R.id.fitness_level_cardview);
        dietAdvisorCardView = findViewById(R.id.diet_advisor_cardview);
        settingsCardView = findViewById(R.id.settings_cardview);

        workoutPlannerCardView.startAnimation(fromBottom);
        fitnessLevelCardView.startAnimation(fromBottom);
        dietAdvisorCardView.startAnimation(fromBottom);
        settingsCardView.startAnimation(fromBottom);
    }

    private void setupListeners() {
        workoutPlannerCardView.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), WorkoutPlannerActivity.class)));

        fitnessLevelCardView.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), FitnessLevelActivity.class)));

        dietAdvisorCardView.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), DietAdvisorActivity.class)));

        settingsCardView.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));
    }
}