package com.example.tuchka;

import android.util.Log;

import org.json.JSONObject;

import android.os.Handler;

public class Item {
    private Handler handler = new Handler();

    private String name;
    private String temp;
    private int iconId;

    private String hum;
    private String press;
    private String wind;

    private boolean expanded;

    public boolean isExpanded(){
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Item(String name){
        this.name = name;
        updateWeatherItem(name);
        this.expanded = false;
    }

    private JSONObject updateWeatherItem(String city) {
        new Thread(){
            public void run(){
                JSONObject json = JsonHelper.getTodayWeather(city);
                if (json == null){
                    handler.post(new Runnable() {
                        public void run() {
                            Log.e("Weather", "ItemError");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeatherItem(json);
                        }
                    });
                }
            }
        }.start();
        return null;
    }

    private void renderWeatherItem(JSONObject json){
        try{
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject  windData= json.getJSONObject("wind");
            this.temp = main.getInt("temp") + "";
            this.iconId = details.getInt("id");
            this.hum = main.getString("humidity");
            this.press = main.getInt("pressure") * 100 / 133 + "";
            this.wind = renderWind(windData);
        } catch (Exception e){
            Log.e("Weather", "ItemRenderError");
        }
    }

    private String renderWind(JSONObject windData){
        try{
            String windDir;
            if (windData.getInt("deg") == 0) windDir = "С, ";
            else if (windData.getInt("deg") == 90) windDir = "В, ";
            else if (windData.getInt("deg") == 180) windDir = "Ю, ";
            else if (windData.getInt("deg") == 270) windDir = "З, ";
            else if (windData.getInt("deg") > 0 && windData.getInt("deg") < 90) windDir = "СВ, ";
            else if (windData.getInt("deg") > 90 && windData.getInt("deg") < 180) windDir = "ЮВ, ";
            else if (windData.getInt("deg") > 180 && windData.getInt("deg") < 270) windDir = "ЮЗ, ";
            else windDir = "СЗ, ";
            return windDir + windData.getString("speed");
        } catch (Exception e){
            Log.e("Weather", "WindRenderError");
            return  "-";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemp() {
        return temp;
    }

    public int getIconId() {
        return iconId;
    }

    public String getHum() {
        return hum;
    }

    public String getPress() {
        return press;
    }

    public String getWind() {
        return wind;
    }
}
