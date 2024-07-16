package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.restaurant_reservation_application.Adapter.ReservationAdapter;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.Tables;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityAdminListTablesBookingBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ListTableBookingAdminActivity extends BaseActivity {
    ActivityAdminListTablesBookingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminListTablesBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#2B2B2B"));
        binding.chipNavigationBar.setItemSelected(R.id.nav_reservation, true);
//        currentUser = mAuth.getCurrentUser();
//
//        // Nhận restaurantId từ Intent
//        restaurantId = getIntent().getStringExtra("restaurantId");

//        initRecyclerView();
//        loadTableBookings(restaurantId);
        SetupBottomNavigationBar();
        DisplayUserInfo();
        Logout();
    }

    private void DisplayUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = databaseReference.child("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Users userInfo = snapshot.getValue(Users.class);
                        if (userInfo != null) {
                            binding.nameTxt.setText(userInfo.getName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ListTableBookingAdminActivity.this, "Failed to fetch user info", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void Logout() {
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(ListTableBookingAdminActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void SetupBottomNavigationBar() {
        binding.chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                binding.chipNavigationBar.setItemSelected(R.id.nav_reservation, true);
                if (id == R.id.nav_reservation) {
                    Intent intent = new Intent(ListTableBookingAdminActivity.this, ListTableBookingAdminActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_history_admin) {
                    // Handle Notifications navigation
                }
            }
        });
    }
}
