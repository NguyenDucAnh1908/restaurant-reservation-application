package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityIntroBinding;
import com.restaurant_reservation_application.databinding.ActivitySignInBinding;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignInActivity extends BaseActivity {
    ActivitySignInBinding binding;

    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#2B2B2B"));
        FirebaseApp.initializeApp(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(SignInActivity.this, options);

        loginWithGoogle();
        setVariables();
    }

    private void loginWithGoogle() {
        binding.signInByGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });
    }

    private void setVariables() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailTxt.getText().toString().trim();
                String password = binding.passWordTxt.getText().toString().trim();

                if (validateInputs(email, password)) {
                    signInUser(email, password);
                }
            }
        });

        binding.textView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignupActivity.class));
            }
        });
    }

    private boolean validateInputs(String email, String password) {
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

        return true;
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            MotionToast.Companion.darkToast(SignInActivity.this,
                                    "Login in success 😍",
                                    "Success !",
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(SignInActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(SignInActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            MotionToast.Companion.darkToast(SignInActivity.this,
                                    "Authentication Failed ☹️",
                                    task.getException().getMessage()+"!",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(SignInActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                MotionToast.Companion.darkToast(SignInActivity.this,
                                        "Login in success 😍",
                                        "Success !",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(SignInActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                MotionToast.Companion.darkToast(SignInActivity.this,
                                        "Authentication Failed ☹️",
                                        task.getException().getMessage()+"!",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(SignInActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                                updateUI(null);
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });

}

