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
import com.restaurant_reservation_application.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {
    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#2B2B2B"));
        setVariabe();
    }

    private void setVariabe() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, SignInActivity.class));
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, SignupActivity.class));
            }
        });
    }
}