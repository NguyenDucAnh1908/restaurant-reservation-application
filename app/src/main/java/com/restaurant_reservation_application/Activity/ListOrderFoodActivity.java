package com.restaurant_reservation_application.Activity;

import android.os.Bundle;

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
import com.restaurant_reservation_application.Adapter.FoodListAdapter;
import com.restaurant_reservation_application.Adapter.FoodListOrderAdapter;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityListFoodsBinding;
import com.restaurant_reservation_application.databinding.ActivityListOrderFoodBinding;

import java.util.ArrayList;

public class ListOrderFoodActivity extends BaseActivity {

    ActivityListOrderFoodBinding binding;
    //private String categoryName;
    private String searchText;
    private boolean isSearch;
    private RecyclerView.Adapter adapterListFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListOrderFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        initList();
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Foods");
        //binding.progressBarFoodsList.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query = null;
        if (isSearch) {
            query = myRef.orderByChild("Title").startAt(searchText).endAt(searchText + "\uf8ff");
        }
//        else {
//            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
//        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Foods.class));
                    }
                    if (list.size() > 0) {
                        binding.foodsView.setLayoutManager(new GridLayoutManager(ListOrderFoodActivity.this, 2));
                        adapterListFood = new FoodListOrderAdapter(list);
                        binding.foodsView.setAdapter(adapterListFood);
                    }
                    //binding.progressBarFoodsList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getIntentExtra() {
        //Log.d("CategoryAdapter", "CategoryId: " + categoryId);
        //categoryId = getIntent().getIntExtra("CategoryId", 0);
        //categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        //binding.titleTxt.setText(categoryName);
        //binding.backBtn.setOnClickListener(v -> finish());
    }
}