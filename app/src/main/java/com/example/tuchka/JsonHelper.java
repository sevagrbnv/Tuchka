package com.example.tuchka;

import android.content.Context;
import android.os.Build;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonHelper {
    private static final String API_FOR_TODAY = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s&lang=ru";
    private static final String API_FOR_WEATHER = "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s&units=metric";
    private static final String API_KEY = BuildConfig.API_KEY;
/*
*Первое API - для заполнения первого дня,
* второе - для последующих шести
*/
    public static JSONObject getTodayWeather(Context context, String city){
        try{
            URL url = new URL (String.format(API_FOR_TODAY, city, API_KEY));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = bufferedReader.readLine()) != null)
                json.append(tmp).append("\n");
            bufferedReader.close();

            JSONObject data = new JSONObject(json.toString());

            if (data.getInt("cod") != 200)  return null;

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject getOtherDays (Context context, double lat, double lon){
        try{
            URL url = new URL (String.format(API_FOR_WEATHER, lat + "", lon + "", API_KEY));
            //URL url = new URL (API_FOR_WEATHER);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = bufferedReader.readLine()) != null)
                json.append(tmp).append("\n");
            bufferedReader.close();

            JSONObject data = new JSONObject(json.toString());

            if (data.getDouble("lat") != lat && data.getDouble("lon") != lon)  return null;

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
