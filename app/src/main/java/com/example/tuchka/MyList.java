package com.example.tuchka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tuchka.databinding.ActivityMyListBinding;

import java.util.ArrayList;

public class MyList extends AppCompatActivity {
    private ActivityMyListBinding binding;
    private RecyclerView recyclerView;
    private static String MAIN_PREFERENCES = "main";
    private static String LIST = "list";
    private static ArrayList<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_list);

        recyclerView = binding.recView;
        setList();
        initData();

        initRecyclerView();
        MainActivity.sPrefs = getSharedPreferences(MAIN_PREFERENCES, MODE_PRIVATE);
    }

    public void OnClickBack (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickAddCity (View view){
        Intent intent = new Intent(MyList.this, SearchCity.class);
        startActivity(intent);
    }

    public void initData(){
        itemList = new ArrayList<>();
        for (String city : CityList.getCityList()){
            if (city != null) {
                Item item = new Item(city);
                itemList.add(item);
            } else {
                CityList.getCityList().remove(city);
            }
        }
    }

    public void initRecyclerView(){
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);
    }
    private void setList(){
        ArrayList<String> list = new ArrayList<>();
        list.addAll(MainActivity.sPrefs.getStringSet(LIST, null));
        if (!list.isEmpty())
            CityList.getCityList().addAll(list);
    }
}