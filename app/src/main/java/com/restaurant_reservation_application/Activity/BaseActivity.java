package com.restaurant_reservation_application.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.restaurant_reservation_application.R;

public class BaseActivity extends AppCompatActivity {

    //FirebaseAuth mAuth;
    FirebaseDatabase database;
    public String TAG="uilover";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        //mAuth = FirebaseAuth.getInstance();

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

    }
}
