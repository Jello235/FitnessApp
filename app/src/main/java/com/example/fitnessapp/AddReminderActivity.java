package com.example.fitnessapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnessapp.fcm.MyFirebaseMessagingService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM;

public class AddReminderActivity extends AppCompatActivity {

    private MaterialButton doneButton;
    private MaterialButton cancelButton;
    private Date chosenDeadlineDate;
    private EditText reminderDeadlineDate;
    private TextInputEditText reminderTitle;
    private TextInputEditText reminderMessage;
    private TextInputLayout reminderTitleTextInputLayout;
    private TextInputLayout reminderMessageTextInputLayout;
    private TextInputLayout reminderDeadlineDateTextInputLayout;
    private ProgressBar progressBar;
    private boolean allowBackPress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        initializeUI();
        setupListener();
    }

    private void initializeUI() {
        reminderDeadlineDate = findViewById(R.id.reminder_deadline_date_editText);
        reminderTitle = findViewById(R.id.reminder_title_textInputEditText);
        reminderMessage = findViewById(R.id.reminder_message_textInputEditText);
        reminderTitleTextInputLayout = findViewById(R.id.reminder_title_textInputLayout);
        reminderMessageTextInputLayout = findViewById(R.id.reminder_message_textInputLayout);
        reminderDeadlineDateTextInputLayout = findViewById(R.id.reminder_deadline_date_textInputLayout);
        progressBar = findViewById(R.id.progressBar);
        doneButton = findViewById(R.id.done_button);
        cancelButton = findViewById(R.id.cancel_button);
    }

    private void setupListener() {
        reminderDeadlineDate.setOnClickListener(view -> showDatePickerDialog());

        doneButton.setOnClickListener(view -> {
            hideKeyboard(this, view);

            if (isReminderTitleValid() && isReminderDeadlineDateValid() && isReminderMessageValid()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                if (currentUser != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    disableUIInteraction();

                    Map<String, Object> reminderData = new HashMap<>();
                    reminderData.put("title", reminderTitle.getText().toString());
                    reminderData.put("message", reminderMessage.getText().toString());
                    reminderData.put("createdDate", Timestamp.now());
                    reminderData.put("reminderDate", chosenDeadlineDate.getTime());
//                    reminderData.put("senderUID", currentUser.getUid());
                    reminderData.put("senderDeviceToken", MyFirebaseMessagingService.getToken(this));

                    addNotificationToFirestore(reminderData);
//                } else {
//                    Toast.makeText(this, "Please login to set reminder", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        cancelButton.setOnClickListener(view ->
                startActivity(new Intent(this, MainActivity.class)));
    }

    public void addNotificationToFirestore(Map<String, Object> data) {
        CollectionReference ref = FirebaseFirestore.getInstance().collection("notifications");
        String documentID = ref.document().getId();
        data.put("reminderID", documentID);

        ref.document(documentID).set(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Successfully added reminder", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                task.getException().printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            enableUIInteraction();
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, chosenYear, chosenMonth, chosenDay) -> {
                    // Get value and use it for comparison in 'doneButton' onClickListener method
                    Calendar chosenDateInCalendar = Calendar.getInstance();
                    chosenDateInCalendar.set(chosenYear, chosenMonth, chosenDay, 0, 0);
                    chosenDeadlineDate = chosenDateInCalendar.getTime();
                    // Set the value for data binding (UI)
                    String date = chosenDay + "/" + (chosenMonth + 1) + "/" + chosenYear;
                    reminderDeadlineDate.setText(date);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        calendar.add(Calendar.DAY_OF_YEAR, 1); // Set min date to one day from today
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private boolean isReminderTitleValid() {
        if (TextUtils.isEmpty(reminderTitle.getText().toString().trim())) {
            reminderTitleTextInputLayout.setError("Title cannot be empty");
            return false;
        } else {
            reminderTitleTextInputLayout.setError(null);
            reminderTitleTextInputLayout.setErrorEnabled(false);
            reminderTitleTextInputLayout.setEndIconMode(END_ICON_CLEAR_TEXT);
            return true;
        }
    }

    private boolean isReminderDeadlineDateValid() {
        if (TextUtils.isEmpty(reminderDeadlineDate.getText().toString())) {
            reminderDeadlineDateTextInputLayout.setError("Deadline Date cannot be empty");
            reminderDeadlineDateTextInputLayout.setEndIconMode(END_ICON_CUSTOM);
            return false;
        } else {
            reminderDeadlineDateTextInputLayout.setError(null);
            reminderDeadlineDateTextInputLayout.setErrorEnabled(false);
            reminderDeadlineDateTextInputLayout.setEndIconMode(END_ICON_CLEAR_TEXT);
            return true;
        }
    }

    private boolean isReminderMessageValid() {
        if (TextUtils.isEmpty(reminderMessage.getText().toString().trim())) {
            reminderMessageTextInputLayout.setError("Message cannot be empty");
            return false;
        } else {
            reminderMessageTextInputLayout.setError(null);
            reminderMessageTextInputLayout.setErrorEnabled(false);
            reminderMessageTextInputLayout.setEndIconMode(END_ICON_CLEAR_TEXT);
            return true;
        }
    }

    public void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void disableUIInteraction() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        allowBackPress = false;
    }

    public void enableUIInteraction() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        allowBackPress = true;
    }

    @Override
    public void onBackPressed() {
        if (allowBackPress) {
            super.onBackPressed();
        }
    }
}