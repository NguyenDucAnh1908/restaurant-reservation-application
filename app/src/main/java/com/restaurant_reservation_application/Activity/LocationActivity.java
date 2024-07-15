package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.RestaurantLocationBinding;

import java.io.IOException;
import java.util.List;

public class LocationActivity extends BaseActivity implements OnMapReadyCallback {
    RestaurantLocationBinding binding;
    GoogleMap gMap;
    FrameLayout map;
    SearchView mapSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RestaurantLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        map = findViewById(R.id.map);
        if (savedInstanceState == null) {
            SupportMapFragment mapFragment = new SupportMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.map, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
        }



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;

        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);

        Intent intent = getIntent();
        String location = intent.getStringExtra("location");

        if (location != null) {
            Geocoder geocoder = new Geocoder(LocationActivity.this);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    MarkerOptions options = new MarkerOptions().position(latLng).title(location);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    gMap.addMarker(options);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}