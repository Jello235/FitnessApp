package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DietAdvisor extends AppCompatActivity  {
    private Button weightLossButton;
    private Button bodyBuildingButton;
    private Button normalDietButton;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        weightLossButton = (Button)findViewById(R.id.weight_losing);
        bodyBuildingButton = (Button)findViewById(R.id.body_building);
        normalDietButton = (Button)findViewById(R.id.normal_diet);
        weightLossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeightLossMenu();
            }
        });

        normalDietButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNormalDietMenu();
            }
        });

        bodyBuildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBodyBuildingMenu();
            }
        });
    }

    public void openWeightLossMenu(){
        Intent intent1 = new Intent(this, WeightLossMenu.class);
        startActivity(intent1);
    }

    public void openNormalDietMenu(){
        Intent intent2 = new Intent(this, NormalDietMenu.class);
        startActivity(intent2);
    }

    public void openBodyBuildingMenu(){
        Intent intent3 = new Intent(this, BodyBuildingMenu.class);
        startActivity(intent3);
    }
}
