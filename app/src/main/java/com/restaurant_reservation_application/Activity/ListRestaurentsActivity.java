package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.restaurant_reservation_application.Adapter.RestaurentsListAdapter;
import com.restaurant_reservation_application.Model.Restaurents;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityListRestaurentsBinding;

import java.util.ArrayList;

public class ListRestaurentsActivity extends BaseActivity {

    ActivityListRestaurentsBinding binding;

    private RecyclerView.Adapter adapterListFood;
    private int id;
    private String name;
    private String searchText;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListRestaurentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
        binding.getLocationAllRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListRestaurentsActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Restaurants");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Restaurents> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Restaurents.class));
                    }
                    if (!list.isEmpty()) {
                        binding.foodListView.setLayoutManager(new GridLayoutManager(ListRestaurentsActivity.this, 2));
                        adapterListFood = new RestaurentsListAdapter(list);
                        binding.foodListView.setAdapter(adapterListFood);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getIntentExtra() {
        id = getIntent().getIntExtra("Id", 0);
        name = getIntent().getStringExtra("Name");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        binding.titleTxt.setText(name);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}
