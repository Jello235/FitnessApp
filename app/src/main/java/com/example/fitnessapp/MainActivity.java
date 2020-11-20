package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";

    private EditText editTextheight;
    private EditText editTextweight;
    private TextView result;
    private TextView waterRequired;
    private Button dietButton;
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordbookRef = db.collection("Records");
    private DocumentReference recordRef = db.document("Records/User records");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextheight = (EditText) findViewById(R.id.height);
        editTextweight = (EditText) findViewById(R.id.weight);
        textViewData = (TextView) findViewById(R.id.text_view_data);
        result = (TextView) findViewById(R.id.result);
        waterRequired = (TextView) findViewById(R.id.water);
        dietButton = (Button) findViewById(R.id.diet);
        dietButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDietAdvisor();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recordbookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Record record = documentSnapshot.toObject(Record.class);
                    record.setDocumentId(documentSnapshot.getId());

                    String documentId = record.getDocumentId();
                    String weight = record.getWeight();
                    String height = record.getHeight();
                    Date date = record.getDate();

                    data +="Weight:" + weight + "\nHeight:" + height + "\nDate:" + date + "\n\n";
                }

                textViewData.setText(data);

            }
        });
    }

    public void addRecord(View v) {
        String weight = editTextweight.getText().toString();
        String height = editTextheight.getText().toString();
        long timestamp = Timestamp.now().getSeconds();
        Date date = new Date (timestamp * 1000);

        Record record = new Record(weight, height, timestamp, date);

        recordbookRef.add(record);
    }

    public void loadRecord(View v) {
        recordbookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Record record = documentSnapshot.toObject(Record.class);
                            record.setDocumentId(documentSnapshot.getId());

                            String documentId = record.getDocumentId();
                            String weight = record.getWeight();
                            String height = record.getHeight();
                            Date date = record.getDate();

                            data += "Weight: " + weight + "\nHeight: " + height + "\nDate:" + date + "\n\n";
                        }
                        textViewData.setText(data);
                    }
                });
    }



    public void openDietAdvisor(){
        Intent intent = new Intent(MainActivity.this, DietAdvisor.class);
        startActivity(intent);
    }

    public void calculateWater(View v){
        String weightStr2 = editTextweight.getText().toString();
        if (weightStr2 != null){
            float weightValue2 = Float.parseFloat(weightStr2);

            double water = (weightValue2 * 0.033);

            displayWater(water);
        }
    }

    public void displayWater(double water){
        String waterR = Double.toString(water);
        waterRequired.setText("Drink " + waterR + " litres of water daily.");
    }

    public void calculateBMI(View v) {
        String heightStr = editTextheight.getText().toString();
        String weightStr = editTextweight.getText().toString();

        if (heightStr != null && !"".equals(heightStr)
                && weightStr != null && !"".equals(weightStr)) {
            float heightValue = Float.parseFloat(heightStr) / 100;
            float weightValue = Float.parseFloat(weightStr);

            float bmi = weightValue / (heightValue * heightValue);

            displayBMI(bmi);
        }
    }

    private void displayBMI(float bmi) {
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

        bmiLabel = bmi + "\n\n" + bmiLabel;
        result.setText(bmiLabel);
    }
}