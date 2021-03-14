package com.example.tuchka;

import android.util.Log;
import android.widget.ImageView;

import java.util.Date;

public class IconHelper {
    public static int mainIconId;
    public static int[] iconsId = new int[6];

    public static void setWeatherIcon(ImageView image, int actualId, long sunrise, long sunset){
        mainIconId = actualId;
        int id = actualId / 100;

        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                image.setImageDrawable(image.getResources().getDrawable(R.drawable.sunny));
            } else {
                image.setImageDrawable(image.getResources().getDrawable(R.drawable.moon));
            }
        } else {
            Log.d("icon", "id " + id);
            switch (id) {
                case 2:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.thunder));
                    break;
                case 3:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.rain));
                    break;
                case 5:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.rain));
                    break;
                case 6:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.snow_weather_icon_152001));
                    break;
                case 7:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.foggy));
                    break;
                case 8:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.cloudy));
                    break;
            }
        }
    }

    public static void setWeatherIcon(ImageView image, int actualId, int day){
        iconsId[day] = actualId;
        int id = actualId / 100;
        if (actualId == 800) {
            image.setImageDrawable(image.getResources().getDrawable(R.drawable.sunny));
        } else {
            Log.d("icon", "id" + id);
            switch (id) {

                case 0:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.sunny));
                    break;
                case 1:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.sunny));
                    break;
                case 2:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.thunder));
                    break;
                case 3:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.rain));
                    break;
                case 5:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.rain));
                    break;
                case 6:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.snow_weather_icon_152001));
                    break;
                case 7:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.foggy));
                    break;
                case 8:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.cloudy));
                    break;
            }
        }
    }

    public static void setWeatherIconItem(ImageView image, int actualId){
        mainIconId = actualId;
        int id = actualId / 100;

        if (actualId == 800) {
                image.setImageDrawable(image.getResources().getDrawable(R.drawable.sunny));
        } else {
            Log.d("icon", "id " + id);
            switch (id) {
                case 2:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.thunder));
                    break;
                case 3:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.rain));
                    break;
                case 5:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.rain));
                    break;
                case 6:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.snow_weather_icon_152001));
                    break;
                case 7:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.foggy));
                    break;
                case 8:
                    image.setImageDrawable(image.getResources().getDrawable(R.drawable.cloudy));
                    break;
            }
        }
    }

    public static int getMainIconId() {
        return mainIconId;
    }

    public static int[] getIconsId() {
        return iconsId;
    }
}
