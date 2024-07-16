package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.restaurant_reservation_application.Adapter.ReservationAdapter;
import com.restaurant_reservation_application.Adapter.RestaurentsListAdapter;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.Restaurents;
import com.restaurant_reservation_application.Model.Tables;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityAdminListTablesBookingBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListTableBookingAdminActivity extends BaseActivity {
    ActivityAdminListTablesBookingBinding binding;
    private ReservationAdapter reservationAdapter;
    private List<Reservation> reservationList;
    private RecyclerView.Adapter adapterReservation;
    private int restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminListTablesBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#2B2B2B"));
        binding.chipNavigationBar.setItemSelected(R.id.nav_reservation, true);


        // Receive restaurantId from Intent
        restaurantId = Integer.parseInt(getIntent().getStringExtra("restaurantId"));
        Log.d(TAG, "Received restaurantId: " + restaurantId);

        // Initialize RecyclerView and load bookings
//        initRecyclerView();
//        loadTableBookings(restaurantId);
        setupBottomNavigationBar();
        displayUserInfo();
        setupLogoutButton();
        initList(restaurantId);
    }
    private void initList(int restaurantId) {
        DatabaseReference tablesRef = database.getReference("Tables");
        DatabaseReference myRef = database.getReference("Reservation");
        binding.progressBarTable.setVisibility(View.VISIBLE);
        ArrayList<Reservation> list = new ArrayList<>();
        List<Integer> tableIds = new ArrayList<>();
        String todayDate = getCurrentDate();
        // Log restaurantId received
        Log.d("ListTableBookingAdminActivity", "Received restaurantId: " + restaurantId);

        tablesRef.orderByChild("RestaurantId").equalTo(restaurantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot tablesSnapshot) {
                if (tablesSnapshot.exists()) {
                    for (DataSnapshot tableSnapshot : tablesSnapshot.getChildren()) {
                        Tables table = tableSnapshot.getValue(Tables.class);
                        if (table != null) {
                            tableIds.add(table.getId());
                            // Log each table ID
                            Log.d("ListTableBookingAdminActivity", "Table found: " + table.getId());
                        }
                    }

                    if (tableIds.isEmpty()) {
                        Log.d("ListTableBookingAdminActivity", "No tables found for restaurantId: " + restaurantId);
                        binding.progressBarTable.setVisibility(View.GONE);
                        return;
                    }

                    // Lấy các đặt chỗ có tableId thuộc danh sách tableIds
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot reservationsSnapshot) {
                            if (reservationsSnapshot.exists()) {
                                for (DataSnapshot reservationSnapshot : reservationsSnapshot.getChildren()) {
                                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);
                                    if (reservation != null && tableIds.contains(reservation.getTableId()) && todayDate.equals(reservation.getDate())) {
                                        list.add(reservation);
                                        // Log each reservation
                                        Log.d("ListTableBookingAdminActivity", "Reservation found: " + reservation.getId());
                                    }
                                }
                                if (!list.isEmpty()) {
                                    binding.recyclerViewTable.setLayoutManager(new LinearLayoutManager(ListTableBookingAdminActivity.this));
                                    adapterReservation = new ReservationAdapter(list);
                                    binding.recyclerViewTable.setAdapter(adapterReservation);
                                } else {
                                    Log.d("ListTableBookingAdminActivity", "No reservations found for tables in restaurantId: " + restaurantId);
                                }
                            } else {
                                Log.d("ListTableBookingAdminActivity", "No reservations exist in database");
                            }
                            binding.progressBarTable.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            binding.progressBarTable.setVisibility(View.GONE);
                            Log.e("ListTableBookingAdminActivity", "Failed to load reservations: ", error.toException());
                        }
                    });
                } else {
                    binding.progressBarTable.setVisibility(View.GONE);
                    Log.e("ListTableBookingAdminActivity", "No tables found for restaurantId: " + restaurantId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarTable.setVisibility(View.GONE);
                Log.e("ListTableBookingAdminActivity", "Failed to load tables: ", error.toException());
            }
        });
    }

    private void displayUserInfo() {
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

    private void setupLogoutButton() {
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

    private void setupBottomNavigationBar() {
        binding.chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.nav_reservation) {
                    // Already in Reservation screen, do nothing
                } else if (id == R.id.nav_history_admin) {
                    Intent adminIntent = new Intent(ListTableBookingAdminActivity.this, HistoryActivityAdmin.class);
                    adminIntent.putExtra("restaurantId", restaurantId);
                    startActivity(adminIntent);
                }
            }
        });
    }
    // Hàm để lấy ngày hôm nay
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
