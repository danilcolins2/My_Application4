package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {


    private TextView profile_name;
    private EditText ed_city;
    private Button btn_def_location;
    private Button btn_find;
    private Button btn_exit;
    private TextView city_name;
    private TextView temp;
    private TextView humidity;
    private TextView wind_speed;

    private ImageView image_icon;
    private final String key = "3b7d395eb97dc26959e2f95f1f84b3ee";


    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle args = getIntent().getExtras();

        profile_name = findViewById(R.id.profile_name);
        ed_city = findViewById(R.id.ed_city);
        btn_find = findViewById(R.id.btn_find);
        btn_def_location = findViewById(R.id.btn_def_location);
        city_name = findViewById(R.id.city_name);
        temp = findViewById(R.id.temp);
        humidity = findViewById(R.id.humidity);
        wind_speed = findViewById(R.id.wind);
        image_icon = findViewById(R.id.image_icon);
        btn_exit = findViewById(R.id.btn_exit);
        profile_name.setText("Привет " + args.get("user_name").toString());

    }


    @Override
    protected void onResume() {
        super.onResume();
        btn_find.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    clickFind();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Weather.Mode = Weather.HAND_MODE;
                Weather.updateAllWidgets(getApplicationContext(), R.layout.weather, Weather.class);

            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickExit();
            }
        });

        btn_def_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("my_location", "click");
                Weather.Mode = Weather.GPS_MODE;
                Weather.updateAllWidgets(getApplicationContext(), R.layout.weather, Weather.class);
                requestLocation();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("my_location", "latitude: " + latitude);
                Log.d("my_location", "longitude: " + longitude);
                getWeather(latitude, longitude, key);

                // Здесь можно использовать полученные координаты
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("my_location", "onStatusChanged");

            }
            @Override
            public void onProviderEnabled(String provider) {
                Log.d("my_location", "onProviderEnabled");
            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.d("my_location", "onProviderDisabled");
            }
        };



    }

    private void requestLocation(){
        if(checkPermission()){

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        } else {
            Log.d("my_location", "checkSelfPermission is false");
        }
    }
    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            Log.d("my_location", "checkSelfPermission is false in checkPermission");
            return false;
        } else {
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(checkPermission()){
                    requestLocation();
                }
            } else {
                // Пользователь отклонил запрос на предоставление разрешений
                Log.d("my_location", "Permission denied in onRequestPermissionsResult");
            }
        }
    }




    private void clickFind() throws IOException, JSONException {





        locationManager.removeUpdates(locationListener);
        if (ed_city.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Пустое поле", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("weather", "Получен текст " + ed_city.getText().toString());
            getWeather(ed_city.getText().toString(), key);
        }

    }

    private  void getWeather(double latitudem, double longitude, String key){
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+ latitudem+"&lon="+longitude+"&appid=" + key + "&units=metric&lang=ru";
        new DownloadWeatherTask().execute(url);
    }
    private  void getWeather(String city, String key){
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
        new DownloadWeatherTask().execute(url);
    }

    @SuppressLint("SetTextI18n")
    private void onDownloadWeather(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);

        //Получение города
        String value_city = jsonObject.getString("name");
        city_name.setText(value_city);
        Weather.city_name = value_city;

        //Получение температуры
        int temp_value = (int)Math.round(jsonObject.getJSONObject("main").getDouble("temp"));
        temp.setText( (temp_value < 0 ? "-" : "+") + temp_value + "°");

        //Получение скорости ветра
        double wind_speed_value = jsonObject.getJSONObject("wind").getDouble("speed");
        wind_speed.setText("Ветер: "+wind_speed_value + "м/с");

        //Получение влажности
        String humidity_value = Integer.toString(jsonObject.getJSONObject("main").getInt("humidity"));
        humidity.setText("Влажность: "+humidity_value + "%");

        String icon = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");

        String iconUrl = "https://openweathermap.org/img/wn/" + icon +"@4x.png";

        Glide.with(this)
                .load(iconUrl)
                .into(image_icon);



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
                onDownloadWeather(result);
            } catch (Exception ex){
                Toast.makeText(getBaseContext(), "Неправильный ввод", Toast.LENGTH_SHORT).show();
                Log.d("widget_test", ex.toString());
            }
        }
    }

    public void onClickExit(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}