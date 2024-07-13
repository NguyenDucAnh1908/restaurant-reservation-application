package com.restaurant_reservation_application.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends BaseActivity {
    ActivityEditProfileBinding binding;
    private String currentUserId;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        getCurrentUserId();

        // Set click listener for saveBtn
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = binding.fullNameEdt.getText().toString().trim();
                String phoneNumber = binding.phoneNumberEdt.getText().toString().trim();
                String email = binding.emailEdt.getText().toString().trim();

                // Validate inputs
                if (fullName.isEmpty()) {
                    binding.fullNameEdt.setError("Please enter your full name");
                    binding.fullNameEdt.requestFocus();
                    return;
                }

                if (phoneNumber.isEmpty()) {
                    binding.phoneNumberEdt.setError("Please enter your phone number");
                    binding.phoneNumberEdt.requestFocus();
                    return;
                }

                // Update user profile in Firebase
                updateUserProfile(fullName, phoneNumber, email);
            }
        });
    }

    private void getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            loadUserProfile();
        } else {
            // Handle user not logged in case
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserProfile() {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        // Populate EditText fields with existing user data
                        binding.fullNameEdt.setText(user.getName());
                        binding.phoneNumberEdt.setText(user.getPhoneNumber());
                        binding.emailEdt.setText(user.getEmail());
                    } else {
                        Toast.makeText(EditProfileActivity.this, "User data is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "User data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile(String fullName, String phoneNumber, String email) {
        //check valid
        String error = checkValidUserInformation(phoneNumber, email);

        if(error.trim().isEmpty()){
            // Update user object
            Users updatedUser = new Users(currentUserId, email, fullName, "", phoneNumber, 0); // Assuming role here, adjust as per your application

            // Update user profile in Firebase
            usersRef.child(currentUserId).setValue(updatedUser)
                    .addOnSuccessListener(aVoid -> {
                        setResult(RESULT_OK); // Set the result to OK
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish EditProfileActivity after successful update
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        }else{
            Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
        }



    }

    private String checkValidUserInformation(String phoneNumber, String email){
        String message = "";
        String regexPattern = "^(.+)@(\\S+)$";
        if(phoneNumber.charAt(0)!='0' || phoneNumber.length()!=10){
            message = "Please enter valid phone number!";
        }
        if(!email.matches(regexPattern)){
            message = "Please enter valid email!";
        }
        return message;
    }
}
