package com.example.tuchka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuchka.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        handler = new Handler();

        if (!isOnline(MainActivity.this))
            Toast.makeText(this, MainActivity.this.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();

        String cityName = new String();

        try {
            Bundle city = getIntent().getExtras();
            if (!city.isEmpty()) {
                cityName = city.getString("city");
                updateWeatherData(cityName);
            }

        } catch (Exception e){
            if (cityName.isEmpty())
                updateWeatherData("Москва");
            else {
                Toast.makeText(this, MainActivity.this.getString(R.string.city_not_found), Toast.LENGTH_LONG).show();
                Log.e("Weather", "City not found");
            }
        }
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
            binding.cityName.setText(json.getString("name"));//название города

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject  windData= json.getJSONObject("wind");
            binding.character.setText(details.getString("description").replace(" ", "\n"));
            binding.temperature.setText(main.getInt("temp") + "°");
            binding.humData.setText(main.getString("humidity") + "%");
            double barData = (main.getInt("pressure") * 100 / 133);
            binding.pressData.setText((barData + "мм"));
            binding.tempData.setText(main.getInt("temp") + "°");
            binding.dateToday.setText(setDateToday());
            renderWind(windData);//заполение данных по ветру

            setWeatherIcon(binding.weatherImg, details.getInt("id"), json.getJSONObject("sys").getLong("sunrise") * 1000,
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
            binding.windData.setText(windDir + windData.getString("speed") + " м/с");
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
                image.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
            } else {
                image.setImageDrawable(getResources().getDrawable(R.drawable.moon));
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
            image.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
        } else {
            Log.d("icon", "id" + id);
            switch (id) {

                case 0:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    break;
                case 1:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
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

        TextView[] dates = new TextView[6];
        dates[0] = binding.d0t;
        dates[1] = binding.d1t;
        dates[2] = binding.d2t;
        dates[3] = binding.d3t;
        dates[4] = binding.d4t;
        dates[5] = binding.d5t;

        for (int i = 0; i < 6; i++){
            c.add(Calendar.DATE, 1);
            dt = sdf.format(c.getTime());
            dates[i].setText(dt);
        }

        ImageView[] images = new ImageView[6];
        images[0] = binding.w0t;
        images[1] = binding.w1t;
        images[2] = binding.w2t;
        images[3] = binding.w3t;
        images[4] = binding.w4t;
        images[5] = binding.w5t;

        TextView[] temps = new TextView[6];
        temps[0] = binding.t0t;
        temps[1] = binding.t1t;
        temps[2] = binding.t2t;
        temps[3] = binding.t3t;
        temps[4] = binding.t4t;
        temps[5] = binding.t5t;

//заполнение ScrollView данными о погоде
        try {
            for (int i = 0; i < 6; i++) {
                JSONObject daily = json.getJSONArray("daily").getJSONObject(i);
                int tempInThatDay = daily.getJSONObject("temp").getInt("day");
                temps[i].setText(tempInThatDay + "");
                int icon = daily.getJSONArray("weather").getJSONObject(0).getInt("id");
                setWeatherIcon(images[i], icon);
            }
        } catch (Exception e){
            Log.e("Weather", "ParseOtherDaysError");
        }
    }

    public void onClickSearch(View view) {
        Intent intent = new Intent(MainActivity.this, SearchCity.class);
        startActivity(intent);
    }

    public void onClickMyList(View view) {
        Intent intent = new Intent(MainActivity.this, MyList.class);
        startActivity(intent);
    }

}