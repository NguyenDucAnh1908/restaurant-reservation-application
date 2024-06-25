package com.restaurant_reservation_application.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.restaurant_reservation_application.Adapter.HistoryAdapter;
import com.restaurant_reservation_application.Model.HistoryModel;
import com.restaurant_reservation_application.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private ListView historyListView;
    private List<HistoryModel> historyItems;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.history_list_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Tạo dữ liệu mẫu
        historyItems = new ArrayList<>();
        historyItems.add(new HistoryModel("Sea Grill of Merrick Park", "2 hrs ago", "Reserved", "Cancel Booking", "17 December 2022 | 12:15 PM", "2 Guests"));
        historyItems.add(new HistoryModel("Sea Grill of Merrick Park", "2 Days ago", "Cancelled", "", "17 December 2022 | 12:15 PM", "2 Guests"));
        historyItems.add(new HistoryModel("Sea Grill of Merrick Park", "10 Days ago", "Completed", "", "17 December 2022 | 12:15 PM", "2 Guests"));

        HistoryAdapter adapter = new HistoryAdapter(this, historyItems);
        historyListView.setAdapter(adapter);

        // Xử lý sự kiện cho Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // Chuyển sang màn hình Home
                    // startActivity(new Intent(HistoryActivity.this, HomeActivity.class));
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    // Chuyển sang màn hình Notifications
                    // startActivity(new Intent(HistoryActivity.this, NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_history) {
                    // Đang ở màn hình History
                    return true;
                } else if (itemId == R.id.nav_more) {
                    // Chuyển sang màn hình More
                    // startActivity(new Intent(HistoryActivity.this, MoreActivity.class));
                    return true;
                }

                return false;
            }
        });
    }
}
