package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.restaurant_reservation_application.Adapter.NotificationAdapter;
import com.restaurant_reservation_application.Model.Notification;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends BaseActivity {
    private NotificationAdapter notificationAdapter;
    private DatabaseReference notificationsRef;
    private String userId;

    private ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the root view from binding

        notificationAdapter = new NotificationAdapter();

        // Get current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            // Handle if user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Firebase reference to Notifications
        notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications");

        // RecyclerView setup
        RecyclerView recyclerView = binding.notificationRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notificationAdapter);

        // Fetch notifications for the current user
        fetchNotifications();

        // Setup bottom navigation bar
        setupBottomNavigationBar();
    }

    private void fetchNotifications() {
        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notificationList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if (notification != null && notification.getUserId().equals(userId)&& notification.getStatus().equals("unread")) {
                        notificationList.add(notification);
                    }
                }
                notificationAdapter.setNotifications(notificationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching notifications: " + error.getMessage());
            }
        });

        // Handle click event in RecyclerView items
        notificationAdapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Mark notification as read
                notificationAdapter.markNotificationAsRead(position);
                fetchNotifications();
            }
        });
    }

    private void setupBottomNavigationBar() {
        binding.chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.nav_home) {
                    // Handle Home navigation
                    Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_notifications) {
                    // Handle Notifications navigation
                } else if (id == R.id.nav_history) {
                    Intent intent = new Intent(NotificationActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_more) {
                    Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (id == R.id.chat) {
                    Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
