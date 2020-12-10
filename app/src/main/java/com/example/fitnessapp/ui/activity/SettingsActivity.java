package com.example.fitnessapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.fitnessapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class SettingsActivity extends AppCompatActivity {

    private Button supportButton, signOutButton, verifyEmailButton;
    private Toolbar myToolBar;
    private LinearLayout mChangeUsername, mChangePhoneNum, mResetEmail, mDeleteAccount, mUpdatePassword;
    private ImageView profileImage;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = mFirebaseDatabase.getReference();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private TextView userName, userEmail, userPhoneNum;
    private final FirebaseUser user = mAuth.getCurrentUser();
    private ProgressBar progressBar, userDetailsProgessBar;
    private final String userID = user.getUid();

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        setContentView(R.layout.activity_settings);
        initializeUI();
        setupToolbar();
        setupAuthStateListener();
        getProfilePictureFromFirebaseCloudStorage();
        checkIfEmailIsVerified();
        getUserInformationFromFirebase();
        setupListeners();
    }

    public void initializeUI() {
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userPhoneNum = findViewById(R.id.user_phone_num);
        supportButton = findViewById(R.id.support_btn);
        signOutButton = findViewById(R.id.sign_out_btn);
        verifyEmailButton = findViewById(R.id.verify_email_btn);
        myToolBar = findViewById(R.id.settings_toolbar);
        profileImage = findViewById(R.id.profile_picture);
        mChangeUsername = findViewById(R.id.change_username);
        mChangePhoneNum = findViewById(R.id.change_phone_num);
        mUpdatePassword = findViewById(R.id.update_password);
        mResetEmail = findViewById(R.id.reset_email);
        mDeleteAccount = findViewById(R.id.delete_account);
        progressBar = findViewById(R.id.progressBar);
        userDetailsProgessBar = findViewById(R.id.userdetails_progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupAuthStateListener() {
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                showToastMessage("Successfully signed out");
            }
        };
    }

    private void getProfilePictureFromFirebaseCloudStorage() {
        StorageReference profileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri ->
                Picasso.get().load(uri).fit().centerCrop().into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        profileImage.setImageResource(R.mipmap.ic_launcher);
                        progressBar.setVisibility(View.GONE);
                    }
                })
        );
    }

    private void checkIfEmailIsVerified() {
        if (!user.isEmailVerified()) {
            verifyEmailButton.setVisibility(View.VISIBLE);

            verifyEmailButton.setOnClickListener(view -> {
                FirebaseUser user = mAuth.getCurrentUser();
                user.sendEmailVerification().addOnSuccessListener(aVoid -> {
                    showToastMessage("Verification Email has been sent");
                }).addOnFailureListener(e -> {
                    Log.d("SettingsActivity", "onFailure: Email is not sent \n" + e.getMessage());
                });
            });
        }
    }

    private void getUserInformationFromFirebase() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (documentSnapshot.exists()) {
                userName.setText(documentSnapshot.getString("fName"));
                userEmail.setText(documentSnapshot.getString("email"));
                userPhoneNum.setText(documentSnapshot.getString("phone"));
            } else {
                Log.d("SettingsActivity", "onEvent: Document do not exists");
            }
            userDetailsProgessBar.setVisibility(View.GONE);
        });
    }

    private void setupListeners() {
        mUpdatePassword.setOnClickListener(view -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_alert_dialog_editview, null);
            dialogBuilder.setView(dialogView);
            final EditText edt = dialogView.findViewById(R.id.edit1);
            edt.setSingleLine();
            dialogBuilder.setTitle("Update Password");
            dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            });
            dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());
            final AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            dialog.getButton(BUTTON_POSITIVE).setOnClickListener(v12 -> {
                String newPassword = edt.getText().toString();
                if (newPassword.length() == 0) {
                    showToastMessage("Error: Password cannot be empty");
                } else if (newPassword.length() < 8) {
                    showToastMessage("Error: Minimum password length is 8");
                } else {
                    user.updatePassword(newPassword).addOnSuccessListener(aVoid -> {
                        showToastMessage("Password Reset Successfully.");
                        dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        showToastMessage(e.getMessage());
                    });
                }
            });
        });

        mChangeUsername.setOnClickListener(view -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_alert_dialog_editview, null);
            dialogBuilder.setView(dialogView);
            final EditText edt = dialogView.findViewById(R.id.edit1);
            edt.setSingleLine();
            dialogBuilder.setTitle("Update display name");
            dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            });
            dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());
            final AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            dialog.getButton(BUTTON_POSITIVE).setOnClickListener(v12 -> {
                String newName = edt.getText().toString();
                if (newName.length() == 0) {
                    showToastMessage("Error: Name cannot be empty");
                } else if (newName.equals(userName.getText())) {
                    showToastMessage("Error: Current name entered");
                } else {
                    String email = userEmail.getText().toString();
                    user.updateEmail(email).addOnSuccessListener(aVoid -> {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("fName", newName);
                        docRef.update(edited).addOnSuccessListener(aVoid1 -> {
                            showToastMessage("Username Updated");
                            dialog.dismiss();
                        });
                    }).addOnFailureListener(e -> showToastMessage(e.getMessage()));
                }
            });
        });

        mResetEmail.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_alert_dialog_editview, null);
            dialogBuilder.setView(dialogView);
            final EditText edt = dialogView.findViewById(R.id.edit1);
            edt.setSingleLine();
            dialogBuilder.setTitle("Update email address");
            dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            });
            dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());
            final AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            dialog.getButton(BUTTON_POSITIVE).setOnClickListener(v1 -> {
                final String newEmail = edt.getText().toString();
                Boolean validEmail = isValidEmail(newEmail);
                if (!validEmail) {
                    showToastMessage("Error: Invalid Email");
                } else if (newEmail.equals(userEmail.getText())) {
                    showToastMessage("Error: Current email address entered");
                } else {
                    dialog.dismiss();
                    progressBar.setVisibility(View.VISIBLE);
                    user.updateEmail(newEmail).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentReference docRef = fStore.collection("users").document(user.getUid());
                            Map<String, Object> edited = new HashMap<>();
                            edited.put("email", newEmail);
                            docRef.update(edited).addOnSuccessListener(aVoid -> {
                                showToastMessage("Email changed successfully");
                                sendEmailVerification();
                                FirebaseAuth.getInstance().signOut(); // logout the user
                                progressBar.setVisibility(View.GONE);
                                // go to login page
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Animatoo.animateSlideRight(SettingsActivity.this);
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                showToastMessage("Error: Malformed Email");
                                progressBar.setVisibility(View.GONE);
                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                showToastMessage("Error: Email entered already exist");
                                progressBar.setVisibility(View.GONE);
                            } catch (Exception e) {
                                showToastMessage("Email update failed! Please logout and login to " +
                                        "try again, ensure that internet connection is available");
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        });

        mChangePhoneNum.setOnClickListener(view -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_alert_dialog_ccp, null);
            dialogBuilder.setView(dialogView);
            final CountryCodePicker ccp = dialogView.findViewById(R.id.ccp);
            final EditText newPhoneNum = dialogView.findViewById(R.id.phoneText);
            ccp.registerCarrierNumberEditText(newPhoneNum);
            dialogBuilder.setTitle("Update phone number");
            dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> { });
            dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());
            final AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            dialog.getButton(BUTTON_POSITIVE).setOnClickListener(view2 -> {
                if (!ccp.isValidFullNumber()) {
                    showToastMessage("Invalid Phone Number");
                } else if (ccp.getFullNumberWithPlus().equals(userPhoneNum.getText())) {
                    showToastMessage("Error: Current phone number entered");
                } else {
                    DocumentReference docRef = fStore.collection("users").document(user.getUid());
                    Map<String, Object> edited = new HashMap<>();
                    edited.put("phone", ccp.getFullNumberWithPlus());
                    docRef.update(edited).addOnSuccessListener(aVoid -> {
                        showToastMessage("Phone number changed successfully");
                        dialog.dismiss();
                    });
                }
            });
        });

        mDeleteAccount.setOnClickListener(view -> {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Account deleted can never be retrieve back. Are you sure you want to continue?")
                    .setPositiveButton("yes", (dialog, which) -> {
                        progressBar.setVisibility(View.VISIBLE);
                        // Delete information from real-time database
                        myRef.child("User details").child(userID).setValue(null);
                        user.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                showToastMessage("Account Deleted!");
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Animatoo.animateSlideRight(SettingsActivity.this);
                            } else {
                                showToastMessage("Error in deleting account. Please send support email to admin for help");
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    })
                    .setNegativeButton("no", (dialog, which) -> dialog.dismiss())
                    .create().show();
        });

        supportButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, SupportActivity.class);
            startActivity(intent);
            Animatoo.animateSlideLeft(SettingsActivity.this);
        });

        signOutButton.setOnClickListener(view -> logout());

        profileImage.setOnClickListener(view -> {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGalleryIntent,1000);
        });
    }

    private void sendEmailVerification() {
        user.sendEmailVerification().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                showToastMessage("Email verification send to " + user.getEmail() + ". Verify email in order to login");
            } else {
                showToastMessage("Failed to send email verification. Please try again a few minutes later");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                profileImage.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri){
        final StorageReference fileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).fit().centerCrop().into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        profileImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        profileImage.setVisibility(View.VISIBLE);
                    }
                });
                showToastMessage("Successfully updated profile picture");
            });
        }).addOnFailureListener(e -> showToastMessage("Failed to update profile picture"));
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(new Intent(getApplicationContext(), LoginActivity.class));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Animatoo.animateSlideRight(SettingsActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
