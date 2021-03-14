package com.example.tuchka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.tuchka.databinding.ActivityGoogleMapBinding;
import com.example.tuchka.databinding.ActivityGoogleMapBindingImpl;
import com.example.tuchka.databinding.ActivityYandexMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapAct extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ActivityGoogleMapBinding binding;

    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_map);

        if (getIntent().hasExtra("lat")){
            lat = getIntent().getExtras().getDouble("lat");
            lon = getIntent().getExtras().getDouble("lon");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.back.setOnClickListener(v -> {
            Intent intent = new Intent(GoogleMapAct.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng place = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(place));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 11.0f));
    }
}