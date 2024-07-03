package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivitySignUpBinding;

public class SignupActivity extends BaseActivity {
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#2B2B2B"));
        setVariables();
    }

    private void setVariables() {
        binding.signupBtn.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, SignInActivity.class)));
        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, SignInActivity.class)));
    }
}