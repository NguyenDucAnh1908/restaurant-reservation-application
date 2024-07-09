package com.restaurant_reservation_application.Activity;

import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

public class ZaloPayActivity extends AppCompatActivity {

    private static final String TAG = "ZaloPayActivity";
    private static final int ZALO_PAY_REQUEST_CODE = 1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
