package com.example.fitnessapp.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fitnessapp.R;
import com.example.fitnessapp.model.Record;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;

public class FitnessLevelActivity extends AppCompatActivity {

    private EditText editTextHeight;
    private EditText editTextWeight;
    private TextView textViewData;
    private Button calculateBMIAndWaterRequired;
    private Toolbar toolbar;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference recordBookRef = db.collection("Records");

    @Override
    protected void onStart() {
        super.onStart();
        recordBookRef.orderBy("date", DESCENDING).addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) { return; }

            String data = "";
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Record record = documentSnapshot.toObject(Record.class);
                record.setDocumentId(documentSnapshot.getId());

                String weight = record.getWeight();
                String height = record.getHeight();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
                String date = simpleDateFormat.format(record.getDate());
                data += "Weight: " + weight + "\nHeight: " + height + "\nSaved Date: " + date + "\n\n";
            }
            textViewData.setText(data);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_level);
        initializeUI();
        setupToolbar();
        setupListeners();
    }

    private void initializeUI() {
        editTextHeight = findViewById(R.id.height);
        editTextWeight = findViewById(R.id.weight);
        calculateBMIAndWaterRequired = findViewById(R.id.calculateBMIAndWaterRequired);
        textViewData = findViewById(R.id.text_view_data);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupListeners() {
        calculateBMIAndWaterRequired.setOnClickListener(view -> calculateBMIAndWaterRequired(view));
    }

    public void addRecord(View view) {
        String weight = editTextWeight.getText().toString();
        String height = editTextHeight.getText().toString();
        long timestamp = Timestamp.now().getSeconds();
        Date date = new Date(timestamp * 1000);

        Record record = new Record(weight, height, timestamp, date);
        recordBookRef.add(record);
    }

    public String calculateWater() {
        String weightStr2 = editTextWeight.getText().toString();
        if (weightStr2 != null && !weightStr2.isEmpty()) {
            float weightValue2 = Float.parseFloat(weightStr2);
            double water = (weightValue2 * 0.033);
            return String.format("%.2f", water) + " litres of water daily.";
        }
        return null;
    }

    public String calculateBMI() {
        String heightStr = editTextHeight.getText().toString();
        String weightStr = editTextWeight.getText().toString();

        if (heightStr != null && !heightStr.isEmpty() && weightStr != null && !weightStr.isEmpty()) {
            float heightValue = Float.parseFloat(heightStr) / 100;
            float weightValue = Float.parseFloat(weightStr);

            float bmi = weightValue / (heightValue * heightValue);

            String bmiLabel = "";

            if (Float.compare(bmi, 18.5f) <= 0) {
                bmiLabel = getString(R.string.underweight);
            } else if (Float.compare(bmi, 18.5f) > 0 && Float.compare(bmi, 25f) <= 0) {
                bmiLabel = getString(R.string.normal);
            } else if (Float.compare(bmi, 25f) > 0 && Float.compare(bmi, 30f) <= 0) {
                bmiLabel = getString(R.string.overweight);
            } else {
                bmiLabel = getString(R.string.obese_class);
            }
            return bmi + "(" + bmiLabel + ")";
        }
        return null;
    }

    private void calculateBMIAndWaterRequired(View view) {
        String bmi = calculateBMI();
        String water_required = calculateWater();

        if (bmi != null && water_required != null) {
            new AlertDialog.Builder(this)
                    .setMessage("BMI: " + bmi + "\n\nWater Required: " + water_required)
                    .setPositiveButton("Close", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        hideKeyboard(view);
                    })
                    .create().show();
        } else {
            Toast.makeText(this, "Make sure weight and height is not empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}