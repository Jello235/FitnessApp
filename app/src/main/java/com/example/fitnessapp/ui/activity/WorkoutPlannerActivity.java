package com.example.fitnessapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitnessapp.R;
import com.example.fitnessapp.model.FitnessChoice;
import com.example.fitnessapp.ui.adapter.FitnessListAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class WorkoutPlannerActivity extends AppCompatActivity {

    private final String TAG = "WorkoutActivity";
    private Spinner partSpinner;
    private Spinner intensitySpinner;
    private TextView workoutResult;
    private Button search;
    private String part;
    private String intensity;
    private FloatingActionButton addReminderButton;
    private Toolbar toolbar;
    private RecyclerView fitnessChoiceRecycleView;
    private final FirebaseFirestore fireStoreRef = FirebaseFirestore.getInstance();
    private FitnessListAdapter listAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        listAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listAdapter != null){
            listAdapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        setContentView(R.layout.activity_workout_planner);
        setupBodyPartSpinner();
        setupIntensitySpinner();
        setupSearchButton();
        setUpRecyclerView();
        setupToolbar();
        setupAddReminderButton();
        workoutResult = findViewById(R.id.result);
    }

    private void setupBodyPartSpinner() {
        partSpinner = findViewById(R.id.part_spinner);
        ArrayAdapter<CharSequence> partAdapter = ArrayAdapter.createFromResource(this, R.array.part, android.R.layout.simple_spinner_item);
        partAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        partSpinner.setAdapter(partAdapter);
    }

    private void setupIntensitySpinner() {
        intensitySpinner = findViewById(R.id.intensity_spinner);
        ArrayAdapter<CharSequence> intensityAdapter = ArrayAdapter.createFromResource(this, R.array.intensity, android.R.layout.simple_spinner_item);
        intensityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intensitySpinner.setAdapter(intensityAdapter);
    }

    private void setupSearchButton() {
        search = findViewById(R.id.search_button);
        search.setOnClickListener(view -> {
            part = partSpinner.getSelectedItem().toString();
            intensity = intensitySpinner.getSelectedItem().toString();
            search(intensity,part);
        });
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupAddReminderButton() {
        addReminderButton = findViewById(R.id.add_reminder_button);
        addReminderButton.setOnClickListener(view ->
                startActivity(new Intent(this, AddReminderActivity.class)));
    }

    private void setUpRecyclerView(){
        Query query = fireStoreRef.collection("Exercise Choice").orderBy("description",Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "no error");
            }  else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        FirestoreRecyclerOptions<FitnessChoice> options = new FirestoreRecyclerOptions
                .Builder<FitnessChoice>()
                .setQuery(query, FitnessChoice.class)
                .build();

        listAdapter = new FitnessListAdapter(options);
        listAdapter.updateOptions(options);

        fitnessChoiceRecycleView = findViewById(R.id.exercise_list_recycler_view);
        fitnessChoiceRecycleView.setLayoutManager(new LinearLayoutManager(this));
        fitnessChoiceRecycleView.setAdapter(listAdapter);
    }

    private void search(String intensity, String part){
        Query query = fireStoreRef.collection("Exercise Choice")
                .whereEqualTo("intensity",intensity).whereEqualTo("part",part);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "no error");
            }  else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        FirestoreRecyclerOptions<FitnessChoice> options = new FirestoreRecyclerOptions
                .Builder<FitnessChoice>()
                .setQuery(query, FitnessChoice.class)
                .build();

        workoutResult.setVisibility(View.VISIBLE);
        listAdapter.updateOptions(options);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}