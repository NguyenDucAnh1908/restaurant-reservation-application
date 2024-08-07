package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.restaurant_reservation_application.Adapter.CartAdapter;
import com.restaurant_reservation_application.Helper.ManagmentCart;
import com.restaurant_reservation_application.Model.FoodOrder;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityCartBinding;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;
    private int tableId;
    private int reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tableId = getIntent().getIntExtra("tableId", -1);
        reservationId = getIntent().getIntExtra("reservationId", -1); // Đã sửa: Lấy giá trị reservationId từ Intent

        managmentCart = new ManagmentCart(this);
        setVariable();
        calculateCart();
        initList();
    }

    private void initList() {
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        }else{
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cartView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart());
        binding.cartView.setAdapter(adapter);
    }

    private void calculateCart() {
        double percentTax = 0.02; //percent 2% tax
        double delivery = 10;// 10 dollar
        tax = Math.round(managmentCart.getTotalFee()*percentTax* 100) / 100;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;
        binding.totafreeTxt.setText("$"+itemTotal);
        binding.taxTxt.setText("$"+tax);
        binding.totalTxt.setText("$"+total);
        binding.deliveryTxt.setText("$"+delivery);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.OrderFoodBtn.setOnClickListener(v -> {
            // Lưu thông tin món ăn vào Firebase
            saveFoodOrderToFirebase();
        });
    }

    private void saveFoodOrderToFirebase() {
        DatabaseReference foodOrderRef = FirebaseDatabase.getInstance().getReference("FoodOrder");

        // Lấy danh sách món ăn trong giỏ hàng
        List<Foods> foodList = managmentCart.getListCart();

        // Tạo một danh sách các tác vụ lưu trữ Firebase để kiểm tra khi tất cả đều hoàn thành
        List<Task<Void>> tasks = new ArrayList<>();

        for (Foods food : foodList) {
            String foodOrderId = foodOrderRef.push().getKey();
            FoodOrder foodOrder = new FoodOrder(foodOrderId, food.getId(), tableId);
            Task<Void> task = foodOrderRef.child(foodOrderId).setValue(foodOrder);
            tasks.add(task);
        }

        // Chờ tất cả các tác vụ lưu trữ hoàn thành
        Tasks.whenAll(tasks)
                .addOnSuccessListener(aVoid -> {
                    // Lưu thành công tất cả các món ăn
                    Toast.makeText(CartActivity.this, "Order saved successfully!", Toast.LENGTH_SHORT).show();

                    // Xóa tất cả các item trong giỏ hàng
                    managmentCart.clearCart();

                    // Quay lại ReserveDetailActivity và truyền lại reservationId
                    Intent intent = new Intent(CartActivity.this, ReserveDetailActivity.class);
                    intent.putExtra("reservationId", reservationId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Có lỗi xảy ra khi lưu đơn hàng
                    Toast.makeText(CartActivity.this, "Failed to save order", Toast.LENGTH_SHORT).show();
                });
    }
}
