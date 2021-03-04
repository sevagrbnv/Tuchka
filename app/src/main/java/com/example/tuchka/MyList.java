package com.example.tuchka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tuchka.databinding.ActivityMyListBinding;

public class MyList extends AppCompatActivity {
    private ActivityMyListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_list);

    }

    public void OnClickBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickAddCity(View view) {
        Intent intent = new Intent(MyList.this, SearchCity.class);
        startActivity(intent);
    }
}