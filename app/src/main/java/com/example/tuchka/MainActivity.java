package com.example.tuchka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static String MAIN_PREFERENCES = "main";
    private static String APP_PREFERENCES_NAME = "name";
    private static String APP_PREFERENCES_TEMP = "temp";
    private static String APP_PREFERENCES_CHAR = "char";
    private static int APP_PREFERENCES_ICON = 800;
    private static String APP_PREFERENCES_HUM = "hum";
    private static String APP_PREFERENCES_PRESS = "press";
    private static String APP_PREFERENCES_WIND = "wind";
    private static String LIST = "list";

    private ActivityMainBinding binding;
    public static SharedPreferences sPrefs;
    private Handler handler;

    String cityName;
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sPrefs = getSharedPreferences(MAIN_PREFERENCES, MODE_PRIVATE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        handler = new Handler();

        cityName = "Москва";


        if (getIntent().hasExtra("city")){
            cityName = getIntent().getExtras().getString("city");
        }

        if (!isOnline(MainActivity.this)) {
            Toast.makeText(this, MainActivity.this.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            setOldData();
        } else {
            updateWeatherData(cityName);
        }
        setList();
        setDates();
        Log.e("g", "fgg");
    }


    private void setList(){
        ArrayList<String> list = new ArrayList<>();
        list.addAll(sPrefs.getStringSet(LIST, null));
        if (!list.isEmpty())
            CityList.getCityList().addAll(list);
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager connectManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        return false;
    }

    //Отправка первого Json-запроса
    private void updateWeatherData(String city) {
        new Thread(){
            public void run(){
                JSONObject json = JsonHelper.getTodayWeather(city);
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
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject  windData= json.getJSONObject("wind");
            binding.cityName.setText(json.getString("name"));//название города
            binding.character.setText(details.getString("description").replace(" ", "\n"));
            binding.temperature.setText(main.getInt("temp") + "°");
            binding.humData.setText(main.getString("humidity") + "%");
            double barData = (main.getInt("pressure") * 100 / 133);
            binding.pressData.setText((barData + "мм"));
            binding.tempData.setText(main.getInt("temp") + "°");
            renderWind(windData);//заполение данных по ветру

            IconHelper.setWeatherIcon(binding.weatherImg, details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);//установка подходящей иконки в соответствии с временем суток

            JSONObject coords = json.getJSONObject("coord");
            lat = coords.getDouble("lat");
            lon = coords.getDouble("lon");

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
                JSONObject json = JsonHelper.getOtherDays(lan, lon);

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

    private void setDates(){
        binding.dateToday.setText(setDateToday());

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
    }

    private void renderOtherDays(JSONObject json){
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
                IconHelper.setWeatherIcon(images[i], icon, i);
            }
        } catch (Exception e){
            Log.e("Weather", "ParseOtherDaysError");
        }

        saveData();
    }

    public void onClickSearch(View view) {
        Intent intent = new Intent(MainActivity.this, SearchCity.class);
        startActivity(intent);
    }

    public void onClickMyList(View view) {
        Intent intent = new Intent(MainActivity.this, MyList.class);
        startActivity(intent);
    }

    public void onClickAddCity(View view){
        if (binding.cityName.getText().toString() != "-"
                && !CityList.getCityList().contains(binding.cityName.getText().toString())) {
            CityList.add(binding.cityName.getText().toString());
            Toast.makeText(this, MainActivity.this.getString(R.string.city_was_added), Toast.LENGTH_LONG).show();
        }
        saveData();
    }

    public void onClickMap(View view){
        if (!isOnline(MainActivity.this) || binding.cityName.getText().toString() == "-")
            Toast.makeText(this, MainActivity.this.getString(R.string.no_map), Toast.LENGTH_LONG).show();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setPositiveButton("Яндекс.Карты", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, YandexMap.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lon", lon);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Google карты", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, GoogleMapAct.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lon", lon);
                    startActivity(intent);
                }
            });

            AlertDialog alert = builder.create();
            alert.setTitle("Выберите карту");
            alert.show();
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putString(APP_PREFERENCES_NAME, binding.cityName.getText().toString());
        editor.putString(APP_PREFERENCES_TEMP, binding.temperature.getText().toString());
        editor.putInt(String.valueOf(APP_PREFERENCES_ICON), IconHelper.getMainIconId());
        editor.putString(APP_PREFERENCES_CHAR, binding.character.getText().toString());
        editor.putString(APP_PREFERENCES_HUM, binding.humData.getText().toString());
        editor.putString(APP_PREFERENCES_PRESS, binding.pressData.getText().toString());
        editor.putString(APP_PREFERENCES_WIND, binding.windData.getText().toString());
        Log.e("fh", binding.cityName.getText().toString());
        saveList();

        editor.apply();
    }

    public static void saveList(){
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putStringSet(LIST, CityList.getCityList());
        editor.apply();
    }

    private void setOldData(){
        binding.cityName.setText(sPrefs.getString(APP_PREFERENCES_NAME, "Москва"));
        binding.temperature.setText(sPrefs.getString(APP_PREFERENCES_TEMP, ""));
        binding.character.setText(sPrefs.getString(APP_PREFERENCES_CHAR, ""));
        IconHelper.setWeatherIconItem(binding.weatherImg, sPrefs.getInt(String.valueOf(APP_PREFERENCES_ICON), 800));
        binding.tempData.setText(sPrefs.getString(APP_PREFERENCES_TEMP, ""));
        binding.humData.setText(sPrefs.getString(APP_PREFERENCES_HUM, ""));
        binding.pressData.setText(sPrefs.getString(APP_PREFERENCES_PRESS, ""));
        binding.windData.setText(sPrefs.getString(APP_PREFERENCES_WIND, ""));
    }
}