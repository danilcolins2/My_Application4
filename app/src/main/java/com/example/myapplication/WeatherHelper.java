package com.example.myapplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WeatherHelper {
    private final String key = "3b7d395eb97dc26959e2f95f1f84b3ee";
    OnDownloadedWeather onDownloadedWeather;

    private Context context;

    public WeatherHelper(Context context) {
        this.context = context;
    }

    public void setOnDownloadedWeather(OnDownloadedWeather onDownloadedWeather){
        this.onDownloadedWeather = onDownloadedWeather;
    }

    public void getWeatherByGPS() {
        Log.d("weather_helper", "Вызван getWeatherByGPS");


        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("weather_helper", "latitude: " + latitude);
                Log.d("weather_helper", "longitude: " + longitude);
                getWeather(latitude, longitude);
                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        try {
            // Запрос обновлений местоположения через GPS
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    public void getWeather(double latitudem, double longitude){
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+ latitudem+"&lon="+longitude+"&appid=" + key + "&units=metric&lang=ru";
        new DownloadWeatherTask().execute(url);
    }
    public void getWeather(String city){
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
        new DownloadWeatherTask().execute(url);
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String surl = params[0];
            URL url;
            StringBuilder builder = new StringBuilder();

            try {
                url = new URL(surl);
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        builder.append(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                onDownloadedWeather.onDownload(result);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
