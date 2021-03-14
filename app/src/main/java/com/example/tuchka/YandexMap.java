package com.example.tuchka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tuchka.databinding.ActivityMainBinding;
import com.example.tuchka.databinding.ActivityYandexMapBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import java.util.Locale;

public class YandexMap extends AppCompatActivity {

    private ActivityYandexMapBinding binding;

    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey("b09a57a0-ed8a-4815-b4b7-ff2a8d85161a");
        MapKitFactory.initialize(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_yandex_map);

        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("lat")){
            lat = getIntent().getExtras().getDouble("lat");
            lon = getIntent().getExtras().getDouble("lon");
        }

        binding.mapview.getMap().move(
                new CameraPosition(new Point(lat, lon), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        binding.mapview.getMap().getMapObjects().addPlacemark(new Point(lat, lon));

        binding.back.setOnClickListener(v -> {
            Intent intent = new Intent(YandexMap.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }
}