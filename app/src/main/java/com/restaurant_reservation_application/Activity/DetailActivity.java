package com.restaurant_reservation_application.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.restaurant_reservation_application.Helper.ManagmentCart;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityDetailBinding;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        managmentCart = new ManagmentCart(this);
        binding.backBtn.setOnClickListener(v -> finish());
        Glide.with(this)
                .load(object.getImage())
                .into(binding.pic);

        binding.priceTxt.setText("$"+object.getPrice());
        binding.titleTxt.setText(object.getName());
        binding.descriptionTxt.setText(object.getDescription());
        //binding.rateTxt.setText(object.getStar()+" Rating");
        //binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText(num*object.getPrice()+" $");
        //binding.timeTxt.setText(object.getTimeValue()+" min");

        binding.plusBtn.setOnClickListener(v -> {
            num = num+1;
            binding.numTxt.setText(num+"");
            binding.totalTxt.setText(" $"+(num*object.getPrice()));
        });
        binding.miusBtn.setOnClickListener(v -> {
            if(num>1){
                num = num-1;
                binding.numTxt.setText(num+"");
                binding.totalTxt.setText(" $"+(num*object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
        });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}