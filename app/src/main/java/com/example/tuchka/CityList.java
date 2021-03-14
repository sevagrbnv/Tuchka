package com.example.tuchka;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Handler;

public class CityList extends Application {

    private static HashSet<String> list = new HashSet<>();

    public static void add(String city){
        list.add(city);
    }

    public static void remove(Item item){
        list.remove(item.getName());
    }

    public static HashSet<String> getCityList(){
        return list;
    }
}
