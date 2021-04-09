package com.sih.evdoctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.sih.evdoctor.entities.VehicleOwner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditProfileActivity extends AppCompatActivity {

    public static final int EDIT_MODE = 0;
    public static final int REGISTER_MODE = 1;
    public static final String MODE_KEY = "mode_key";

    //    private Customer customer;
    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private VehicleOwner vehicleOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        vehicleOwner = new VehicleOwner();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    finish();
                vehicleOwner.setUid(firebaseAuth.getCurrentUser().getUid());
            }
        });
    }

    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                String firstName = "";
                String lastName = "";
                if (firstNameEditText.getText() != null)
                    firstName = firstNameEditText.getText().toString();
                if (firstName.length() < 2) {
                    Toast.makeText(this, "Enter a valid name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lastNameEditText.getText() != null) {
                    lastName = lastNameEditText.getText().toString();
                }

                if (lastName.length() == 1) {
                    Toast.makeText(this, "Enter a valid name", Toast.LENGTH_SHORT).show();
                    return;
                }

                vehicleOwner.setFirstName(firstName);
                vehicleOwner.setLastName(lastName);
                vehicleOwner.setFullName(firstName + " " + lastName);
                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(vehicleOwner.getFullName())
                        .build();
                Toast.makeText(this, "Updating profile...", Toast.LENGTH_SHORT).show();
                view.setEnabled(false);
                FirebaseFirestore.getInstance().document("vehicle_owners/" + vehicleOwner.getUid()).set(
                        vehicleOwner, SetOptions.merge()
                );
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(EditProfileActivity.this, "Profile Updated. Cheers!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                break;
        }
    }
}
