package com.example.tuchka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Handler handler;

    private ImageButton add_city, showLoc, listOfCities;
    private TextView cityName;

    private ImageView weatherIcon;
    private TextView temperature, weatherCharacter;

    private TextView dateToday, temp, bar, hum, wind;

    private TextView[] dates;
    private ImageView[] dateIcons;
    private TextView[] dateChars;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        setId();

        if (!isOnline(MainActivity.this))
            Toast.makeText(this, MainActivity.this.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();

        updateWeatherData("Москва");

    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager connectManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    private void setId(){
        add_city = findViewById(R.id.add_btn);
        showLoc = findViewById(R.id.map_btn);
        listOfCities = findViewById(R.id.list_of_sities);
        cityName = findViewById(R.id.city_name);

        weatherIcon = findViewById(R.id.weather_img);
        temperature = findViewById(R.id.temperature);
        weatherCharacter = findViewById(R.id.character);

        dateToday = findViewById(R.id.date_today);
        temp = findViewById(R.id.temp_data);
        bar = findViewById(R.id.press_data);
        hum = findViewById(R.id.hum_data);
        wind = findViewById(R.id.wind_data);

        dates = new TextView[6];
        dateIcons = new ImageView[6];
        dateChars = new TextView[6];

        dates[0] = findViewById(R.id.d0t);
        dates[1] = findViewById(R.id.d1t);
        dates[2] = findViewById(R.id.d2t);
        dates[3] = findViewById(R.id.d3t);
        dates[4] = findViewById(R.id.d4t);
        dates[5] = findViewById(R.id.d5t);

        dateIcons[0] = findViewById(R.id.w0t);
        dateIcons[1] = findViewById(R.id.w1t);
        dateIcons[2] = findViewById(R.id.w2t);
        dateIcons[3] = findViewById(R.id.w3t);
        dateIcons[4] = findViewById(R.id.w4t);
        dateIcons[5] = findViewById(R.id.w5t);

        dateChars[0] = findViewById(R.id.t0t);
        dateChars[1] = findViewById(R.id.t1t);
        dateChars[2] = findViewById(R.id.t2t);
        dateChars[3] = findViewById(R.id.t3t);
        dateChars[4] = findViewById(R.id.t4t);
        dateChars[5] = findViewById(R.id.t5t);
    }

    //Отправка первого Json-запроса
    private void updateWeatherData(String city) {
        new Thread(){
            public void run(){
                JSONObject json = JsonHelper.getTodayWeather(MainActivity.this, city);
                if (json == null){
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    //Заполнение погоды по сегодняшнему дню
    private void renderWeather(JSONObject json){
        try {
            cityName.setText(json.getString("name"));//название города

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject  windData= json.getJSONObject("wind");
            weatherCharacter.setText(details.getString("description").replace(" ", "\n"));
            temperature.setText(main.getInt("temp") + "°");
            hum.setText(main.getString("humidity") + "%");
            double barData = (main.getInt("pressure") * 100 / 133);
            bar.setText((barData + "мм"));
            temp.setText(main.getInt("temp") + "°");
            dateToday.setText(setDateToday());
            renderWind(windData);//заполение данных по ветру

            setWeatherIcon(weatherIcon, details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);//установка подходящей иконки в соответствии с временем суток

            JSONObject coords = json.getJSONObject("coord");
            double lat = coords.getDouble("lat");
            double lon = coords.getDouble("lon");

            updateOtherDays(lat, lon);//Оправка запроса по второму API
        } catch (Exception e) {
            Log.e("Weather", "RenderError");
        }
    }

    private void renderWind(JSONObject windData){
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
            wind.setText(windDir + windData.getString("speed") + " м/с");
        } catch (Exception e){
            Log.e("Weather", "WindRenderError");
        }
    }

    private String setDateToday(){
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        Date d = new Date();
        String dayOfWeek = sdf.format(d);
        return dateText + ", " + dayOfWeek;
    }

    private void updateOtherDays(double lan, double lon){
        new Thread(){
            public void run(){
                JSONObject json = JsonHelper.getOtherDays(MainActivity.this, lan, lon);

                if (json == null){
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.other_days_not_found),
                                    Toast.LENGTH_LONG).show();

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderOtherDays(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void setWeatherIcon(ImageView image, int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
            } else {
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.moon));
            }
        } else {
            Log.d("icon", "id " + id);
            switch (id) {
                case 2:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.thunder));
                    break;
                case 3:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.rain));
                    break;
                case 5:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.rain));
                    break;
                case 6:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.snow_weather_icon_152001));
                    break;
                case 7:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.foggy));
                    break;
                case 8:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    break;
            }
        }
    }

    private void setWeatherIcon(ImageView image, int actualId){
        int id = actualId / 100;
        if (actualId == 800) {
            weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
        } else {
            Log.d("icon", "id_suka" + id);
            switch (id) {

                case 0:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    break;
                case 1:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    break;
                case 2:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.thunder));
                    break;
                case 3:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.rain));
                    break;
                case 5:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.rain));
                    break;
                case 6:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.snow_weather_icon_152001));
                    break;
                case 7:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.foggy));
                    break;
                case 8:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    break;
            }
        }
    }

    private void renderOtherDays(JSONObject json){
        //заполнение датами ScrollView
        Date currentDate = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd");
        String dt = sd.format(currentDate);
        Calendar c = Calendar.getInstance();
        try{
            c.setTime(sd.parse(dt));
        }catch (Exception e){
            Log.e("Weather", "ParseDateError");
        }
        for (int i = 0; i < 6; i++){
            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            dates[i].setText(dt);
        }
//заполнение ScrollView данными о погоде
        try {
            for (int i = 0; i < 6; i++) {
                JSONObject daily = json.getJSONArray("daily").getJSONObject(i);
                int tempInThatDay = daily.getJSONObject("temp").getInt("day");
                dateChars[i].setText(tempInThatDay + "");
                int icon = daily.getJSONArray("weather").getJSONObject(0).getInt("id");
                setWeatherIcon(dateIcons[i], icon);
            }
        } catch (Exception e){
            Log.e("Weather", "ParseOtherDaysError");
        }
    }
}