package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityIntroBinding;
import com.restaurant_reservation_application.databinding.ActivitySignInBinding;

public class SignInActivity extends BaseActivity {
    ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#2B2B2B"));
        setVariables();
    }

    private void setVariables() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, HistoryActivity.class));
            }
        });
    }
}