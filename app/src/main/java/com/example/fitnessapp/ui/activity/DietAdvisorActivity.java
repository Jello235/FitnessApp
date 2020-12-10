package com.example.fitnessapp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.fitnessapp.R;

public class DietAdvisorActivity extends AppCompatActivity {

    private Button weightLossButton;
    private Button bodyBuildingButton;
    private Button normalDietButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        setContentView(R.layout.activity_diet_advisor);
        initializeUI();
        setupToolbar();
        setupListeners();
    }

    private void initializeUI() {
        weightLossButton = findViewById(R.id.weight_losing);
        bodyBuildingButton = findViewById(R.id.body_building);
        normalDietButton = findViewById(R.id.normal_diet);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupListeners() {
        weightLossButton.setOnClickListener(view ->
                startActivity(new Intent(this, WeightLossMenuActivity.class)));

        normalDietButton.setOnClickListener(view ->
                startActivity(new Intent(this, NormalDietMenuActivity.class)));

        bodyBuildingButton.setOnClickListener(view ->
                startActivity(new Intent(this, BodyBuildingMenuActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
