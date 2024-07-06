package com.restaurant_reservation_application.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.restaurant_reservation_application.Adapter.HistoryAdapter;
import com.restaurant_reservation_application.Adapter.PopularAdapter;
import com.restaurant_reservation_application.Model.HistoryModel;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.Restaurents;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    ActivityHistoryBinding binding;

    private RecyclerView.Adapter adapterHistory;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;
    private ChipNavigationBar chipNavigationBar;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance(); // Khởi tạo database
        setupBottomNavigationBar();
        initList();
        getIntenExtra();
    }

    private void initList() {
    }

    private void getIntenExtra() {
        databaseReference = database.getReference("Reservation");
        binding.progressBarHistory.setVisibility(View.VISIBLE);
        ArrayList<Reservation> list = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
//                        Reservation reservation = issue.getValue(Reservation.class);
//                        if (reservation != null) {
//                            list.add(reservation);
//                        }
                        list.add(issue.getValue(Reservation.class));
                    }
                    if (!list.isEmpty()) {
                        binding.historyView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this, LinearLayoutManager.VERTICAL, false));
                        adapterHistory = new HistoryAdapter(list);
                        binding.historyView.setAdapter(adapterHistory);
                    }
                    binding.progressBarHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarHistory.setVisibility(View.GONE);
                // Handle error
            }
        });
    }


    private void setupBottomNavigationBar() {
        chipNavigationBar = binding.chipNavigationBar; // Thêm dòng này
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.nav_home) {
                    startActivity(new Intent(HistoryActivity.this, MainActivity.class));
                } else if (id == R.id.nav_notifications) {
                    // Handle Notifications navigation
                    // startActivity(new Intent(HistoryActivity.this, NotificationsActivity.class));
                } else if (id == R.id.nav_history) {
                    // Currently on the History screen
                } else if (id == R.id.nav_more) {
                    // Handle More navigation
                    // startActivity(new Intent(HistoryActivity.this, MoreActivity.class));
                }
            }
        });
    }
}

