package com.example.tuchka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.example.tuchka.databinding.ActivitySearchCityBinding;

import java.util.Arrays;
import java.util.List;

public class SearchCity extends AppCompatActivity {
    private ActivitySearchCityBinding binding;
    final String[] cities = {"Москва", "Петербург", "Берлин", "Париж", "Лондон"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_city);

        connectAdapter();
    }

    private void connectAdapter(){
        String[] cats = getResources().getStringArray(R.array.city_names);
        List<String> cityList = Arrays.asList(cats);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, cityList);
        binding.autoCompleteTextView.setAdapter(adapter);
    }

    public void OnClickSearch(View view){
        String city = binding.autoCompleteTextView.getText().toString();
        if (city.length() > 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("city", binding.autoCompleteTextView.getText().toString());
            startActivity(intent);
        }
    }

    public void OnClickBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickMyList(View view) {
        Intent intent = new Intent(SearchCity.this, MyList.class);
        startActivity(intent);
    }
}