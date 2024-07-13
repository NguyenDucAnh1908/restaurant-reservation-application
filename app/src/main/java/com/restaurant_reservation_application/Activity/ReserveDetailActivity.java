package com.restaurant_reservation_application.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restaurant_reservation_application.Adapter.FoodListOrderAdapter;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.Restaurents;
import com.restaurant_reservation_application.Model.Tables;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityReserveDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReserveDetailActivity extends BaseActivity {
    ActivityReserveDetailBinding binding;
    Reservation currentReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReserveDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariables();

        // Lấy reservationId từ Intent
        int reservationId = getIntent().getIntExtra("reservationId", -1);
        if (reservationId != -1) {
            loadReservationData(reservationId);
        } else {
            Log.e("ReserveDetailActivity", "No reservationId provided in Intent");
        }

        binding.cartBtn.setOnClickListener(v -> {
            if (currentReservation != null) {
                // Create intent to open CartActivity
                Intent intent = new Intent(ReserveDetailActivity.this, CartActivity.class);

                // Get tableId and reservationId from reservation
                int tableId = currentReservation.getTableId();
                int reservationIntentId = currentReservation.getId();
                intent.putExtra("tableId", tableId);
                intent.putExtra("reservationId", reservationId);

                // Start CartActivity
                startActivity(intent);
            } else {
                Log.e("ReserveDetailActivity", "Reservation is null");
            }
        });



    }

    private void setVariables() {

        binding.selectFoodTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuFoodDialog();
            }
        });

    }

    private void loadReservationData(int reservationId) {
        DatabaseReference reservationRef = FirebaseDatabase.getInstance().getReference("Reservation").child(String.valueOf(reservationId));
        reservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentReservation = snapshot.getValue(Reservation.class);
                    if (currentReservation != null) {
                        // Hiển thị chi tiết Reservation lên giao diện
                        binding.dateTxt.setText("Date and Time: " + currentReservation.getDate());
                        binding.timeTxt.setText(currentReservation.getStartTime() + " - " + currentReservation.getEndTime());
                        binding.personTxt.setText("People: " + currentReservation.getPeople());

                        // Lấy thông tin User từ Firebase
                        loadUserData(currentReservation.getUserId());

                        // Lấy thông tin Table từ Firebase
                        loadTableData(currentReservation.getTableId());
                    }
                } else {
                    Log.e("ReserveDetailActivity", "No such reservation with id " + reservationId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReserveDetailActivity", "Firebase query cancelled", error.toException());
            }
        });
    }

    private void loadUserData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(currentReservation.getUserId()));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user != null) {
                        // Hiển thị thông tin User lên giao diện
                        binding.nameTxt.setText(user.getName());
                        binding.phoneNumberTxt.setText(user.getPhoneNumber());
                    }
                } else {
                    Log.e("ReserveDetailActivity", "No such user with id " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReserveDetailActivity", "Firebase query cancelled", error.toException());
            }
        });
    }

    private void loadTableData(int tableId) {
        DatabaseReference tableRef = FirebaseDatabase.getInstance().getReference("Tables").child(String.valueOf(currentReservation.getTableId()));
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Tables table = snapshot.getValue(Tables.class);
                    if (table != null) {
                        // Hiển thị thông tin Table lên giao diện
                        binding.numTableTxt.setText(table.getName());

                        // Lấy thông tin nhà hàng từ Firebase
                        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("Restaurants").child(String.valueOf(table.getRestaurantId()));
                        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Restaurents restaurant = snapshot.getValue(Restaurents.class);
                                    if (restaurant != null) {
                                        binding.nameRestaurentTxt.setText(restaurant.getName());
                                        Picasso.get().load(restaurant.getImage()).into(binding.imageResTxt);
                                        binding.locationResTxt.setText(restaurant.getAddress());
                                    }
                                } else {
                                    Log.e("ReserveDetailActivity", "No such restaurant with id " + table.getRestaurantId());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("ReserveDetailActivity", "Firebase query cancelled", error.toException());
                            }
                        });
                    }
                } else {
                    Log.e("ReserveDetailActivity", "No such table with id " + tableId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReserveDetailActivity", "Firebase query cancelled", error.toException());
            }
        });
    }

    private void showMenuFoodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_list_order_food, null);
        builder.setView(dialogView);

        RecyclerView menuRecyclerView = dialogView.findViewById(R.id.foodsView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Foods");
        ArrayList<Foods> foodList = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    Foods food = foodSnapshot.getValue(Foods.class);
                    foodList.add(food);
                }
                FoodListOrderAdapter adapter = new FoodListOrderAdapter(foodList);
                menuRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReserveDetailActivity.this, "Error fetching menu", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

