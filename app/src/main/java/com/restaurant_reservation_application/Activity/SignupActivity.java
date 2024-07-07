package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivitySignUpBinding;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

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
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailTxt.getText().toString().trim();
                String password = binding.passWordTxt.getText().toString().trim();
                String confirmPassword = binding.rePassWordTxt.getText().toString().trim();
                String fullName = binding.fullNameEdt.getText().toString().trim();
                String phoneNumber = binding.phoneNumberEdt.getText().toString().trim();

                if (validateInputs(email, password, confirmPassword, fullName, phoneNumber)) {
                    signUpUser(email, password, fullName, phoneNumber);
                }
            }
        });
    }

    private boolean validateInputs(String email, String password, String confirmPassword, String fullName, String phoneNumber) {
        if (fullName.isEmpty()) {
            binding.fullNameEdt.setError("Full Name is required");
            binding.fullNameEdt.requestFocus();
            return false;
        }

        if (phoneNumber.isEmpty() || !phoneNumber.matches("^0\\d{9}$")) {
            binding.phoneNumberEdt.setError("Phone Number must be 10 digits starting with 0");
            binding.phoneNumberEdt.requestFocus();
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailTxt.setError("Valid Email is required");
            binding.emailTxt.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            binding.passWordTxt.setError("Password must be at least 6 characters");
            binding.passWordTxt.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            binding.rePassWordTxt.setError("Passwords do not match");
            binding.rePassWordTxt.requestFocus();
            return false;
        }

        return true;
    }

    private void signUpUser(String email, String password, String fullName, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Users user = new Users(
                                    mAuth.getCurrentUser().getUid(),
                                    email,
                                    fullName,
                                    password,
                                    phoneNumber,
                                    0 // Default role
                            );

                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                MotionToast.Companion.darkToast(SignupActivity.this,
                                                        "Hurray success 😍",
                                                        "Account created successfully!",
                                                        MotionToastStyle.SUCCESS,
                                                        MotionToast.GRAVITY_BOTTOM,
                                                        MotionToast.LONG_DURATION,
                                                        ResourcesCompat.getFont(SignupActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                                                startActivity(new Intent(SignupActivity.this, SignInActivity.class));
                                                finish();
                                            } else {
                                                MotionToast.Companion.darkToast(SignupActivity.this,
                                                        "Sign Up Failed☹️",
                                                        "Failed to create account!",
                                                        MotionToastStyle.ERROR,
                                                        MotionToast.GRAVITY_BOTTOM,
                                                        MotionToast.LONG_DURATION,
                                                        ResourcesCompat.getFont(SignupActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                                            }
                                        }
                                    });
                        } else {
                            MotionToast.Companion.darkToast(SignupActivity.this,
                                    "Sign Up Failed☹️",
                                    "Failed to create account: " + task.getException().getMessage(),
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(SignupActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                        }
                    }
                });
    }
}

