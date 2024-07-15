package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityProfileBinding;

public class ProfileActivity extends BaseActivity {
    ActivityProfileBinding binding;

    private String currentUserId;
    private DatabaseReference usersRef;
    private static final int EDIT_PROFILE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupBottomNavigationBar();
        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        getCurrentUserId();

        // Set click listener for editProfile TextView
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start EditProfileActivity for result
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
                        // Set user details to TextViews
                        binding.nameTxt.setText(user.getName());
                        binding.phoneTxt.setText(user.getPhoneNumber());
                        binding.emailTxt.setText(user.getEmail());
                    } else {
                        Toast.makeText(ProfileActivity.this, "User data is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User data does not exist", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from EditProfileActivity
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload user profile data
            loadUserProfile();
        }
    }
    private void setupBottomNavigationBar() {
        binding.chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.nav_home) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_notifications) {
                    // Handle Notifications navigation
                } else if (id == R.id.nav_history) {
                    Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_more) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (id == R.id.chat) {
                    Intent intent;
                    intent = new Intent(ProfileActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
