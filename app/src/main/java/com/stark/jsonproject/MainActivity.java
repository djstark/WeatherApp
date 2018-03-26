package com.stark.jsonproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import com.stark.jsonproject.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mLoadingProgress;
    private String locationQuery = null;
    private Location userLocation;
    private LatLng manualLocation;

    final private String CONDITION_QUERY = "/alerts/hourly/geolookup/forecast10day/conditions/q/";
    final private String RADAR_QUERY = "http://api.wunderground.com/api/58c5745a92e613f5/radar/";
    private String fullRadarQuery = null;
    private static final int MAP_ACTIVITY_REQUEST_CODE = 0;
    Weather cityWeather = new Weather();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingProgress = findViewById(R.id.pb_loading);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // User doesn't permit location services. Defaults to center of US, 39.8283 , 98.5795° W Lebanon, Kansas
            manualLocation = new LatLng(39.8283,-98.5795);
            locationQuery = CONDITION_QUERY + 39.8283 + ',' + -98.5795 + ".json";
            URL weatherUrl =  ApiUtil.buildUrl(locationQuery);
            fullRadarQuery = RADAR_QUERY + "image.gif?centerlat=" + 39.8283 + "&centerlon=" + -98.5795 + "&radius=100&width=280&height=280&newmaps=1";
            new WeatherQueryTask().execute(weatherUrl);
        }
        else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                userLocation = location;
                                locationQuery = CONDITION_QUERY + Double.toString(location.getLatitude()) + ',' + Double.toString(location.getLongitude()) + ".json";
                                URL weatherUrl =  ApiUtil.buildUrl(locationQuery);
                                fullRadarQuery = RADAR_QUERY + "image.gif?centerlat=" + Double.toString(location.getLatitude()) + "&centerlon=" + Double.toString(location.getLongitude()) + "&radius=100&width=280&height=280&newmaps=1";
                                    new WeatherQueryTask().execute(weatherUrl);
                            }
                        }
                    });
        }
    }



    public void onClick(View v) {
        if(v == findViewById(R.id.wunderGround))
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wunderground.com/?apiref=1631cf51b4a0be29")));

        if(v == findViewById(R.id.citySearch)) {
            Intent intent = new Intent(this, MapsActivity.class);
            if (userLocation != null) {
                intent.putExtra("lat", userLocation.getLatitude());
                intent.putExtra("long", userLocation.getLongitude());
            } else {
                intent.putExtra("lat", manualLocation.latitude);
                intent.putExtra("long", manualLocation.longitude);
            }
            startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
        }
        if (v == findViewById(R.id.refreshView)) {
            if(userLocation != null){
                locationQuery = CONDITION_QUERY + userLocation.getLatitude() + ',' + userLocation.getLongitude() + ".json";
                URL weatherUrl = ApiUtil.buildUrl(locationQuery);
                fullRadarQuery = RADAR_QUERY + "image.gif?centerlat="+ userLocation.getLatitude() + "&centerlon=" + userLocation.getLongitude() + "&radius=100&width=280&height=280&newmaps=1";
                new WeatherQueryTask().execute(weatherUrl);
            }
            else{
                locationQuery = CONDITION_QUERY + manualLocation.latitude + ',' + manualLocation.longitude + ".json";
                URL weatherUrl = ApiUtil.buildUrl(locationQuery);
                fullRadarQuery = RADAR_QUERY + "image.gif?centerlat=" + manualLocation.latitude + "&centerlon=" + manualLocation.longitude + "&radius=100&width=280&height=280&newmaps=1";
                new WeatherQueryTask().execute(weatherUrl);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if(userLocation!=null) {
                    userLocation.setLatitude(data.getDoubleExtra("lat", 0));
                    userLocation.setLongitude(data.getDoubleExtra("long", 0));
                    locationQuery = CONDITION_QUERY + data.getDoubleExtra("lat", 0) + ',' + data.getDoubleExtra("long", 0) + ".json";
                    URL weatherUrl = ApiUtil.buildUrl(locationQuery);
                    fullRadarQuery = RADAR_QUERY + "image.gif?centerlat=" + data.getDoubleExtra("lat", 0) + "&centerlon=" + data.getDoubleExtra("long", 0) + "&radius=100&width=280&height=280&newmaps=1";
                    new WeatherQueryTask().execute(weatherUrl);
                }
                else {
                    manualLocation = new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("long", 0));
                    locationQuery = CONDITION_QUERY + data.getDoubleExtra("lat", 0) + ',' + data.getDoubleExtra("long", 0) + ".json";
                    URL weatherUrl = ApiUtil.buildUrl(locationQuery);
                    fullRadarQuery = RADAR_QUERY + "image.gif?centerlat=" + data.getDoubleExtra("lat", 0) + "&centerlon=" + data.getDoubleExtra("long", 0) + "&radius=100&width=280&height=280&newmaps=1";
                    new WeatherQueryTask().execute(weatherUrl);
                }
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class WeatherQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls){
            URL searchURL = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJson(searchURL);
            }
            catch (IOException e) {
                Log.e("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            mLoadingProgress.setVisibility(View.GONE);
            View hourlyLayout = findViewById(R.id.hourlyLayout);
            hourlyLayout.setVisibility(View.VISIBLE);
            if (result == null) {
                TextView tvError = findViewById(R.id.tv_error);
                tvError.setVisibility(View.VISIBLE);
            }
            else {
                ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
                cityWeather = ApiUtil.getWeatherFromJSON(result);
                binding.setWeather(cityWeather);

                ImageView ivWeatherIcon = findViewById(R.id.weatherIcon);
                ImageView ivRadar =  findViewById(R.id.radar);
                ImageView ivCond1 = findViewById(R.id.cond1);
                ImageView ivCond2 = findViewById(R.id.cond2);
                ImageView ivCond3 = findViewById(R.id.cond3);
                ImageView ivCond4 = findViewById(R.id.cond4);
                ImageView ivCond5 = findViewById(R.id.cond5);
                ImageView ivCond6 = findViewById(R.id.cond6);
                ImageView ivCond7 = findViewById(R.id.cond7);
                ImageView ivCond8 = findViewById(R.id.cond8);
                ImageView ivWeeklyC1 = findViewById(R.id.weeklyCond1);
                ImageView ivWeeklyC2 = findViewById(R.id.weeklyCond2);
                ImageView ivWeeklyC3 = findViewById(R.id.weeklyCond3);
                ImageView ivWeeklyC4 = findViewById(R.id.weeklyCond4);
                ImageView ivWeeklyC5 = findViewById(R.id.weeklyCond5);
                ImageView ivWeeklyC6 = findViewById(R.id.weeklyCond6);
                ImageView ivWeeklyC7 = findViewById(R.id.weeklyCond7);
                TextView tvAlerts = findViewById(R.id.alerts);
                StringBuilder updateAlerts = new StringBuilder();

                if (cityWeather.alerts != null)
                    if (cityWeather.alerts.length == 0) {
                        updateAlerts = new StringBuilder("No Weather alerts.");
                        tvAlerts.setText(updateAlerts.toString());
                    } else {
                        for (String alert : cityWeather.alerts) {
                            updateAlerts.append(alert).append("\n");
                        }
                        String severeWeatherLink = "https://www.wunderground.com/severe/" + cityWeather.country + '/' + cityWeather.state + '/' + cityWeather.city;
                        String html = "<a href=\"" + severeWeatherLink + "\">" + updateAlerts + "</a>";
                        Spanned resultHTML;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            resultHTML = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
                        } else {
                            resultHTML = Html.fromHtml(html);
                        }
                        tvAlerts.setText(resultHTML);
                        tvAlerts.setMovementMethod(LinkMovementMethod.getInstance());
                    }

                ImageView logo = findViewById(R.id.wunderGround);
                Picasso.get()
                        .load("http://icons-ak.wxug.com/graphics/wu2/logo_130x80.png")
                        .resize(390, 240)
                        .into(logo);
                Picasso.get()
                        .load(cityWeather.iconURL)
                        .resize(150, 150)
                        .into(ivWeatherIcon);
                Picasso.get()
                        .load(fullRadarQuery)
                        .resize(530, 530)
                        .into(ivRadar);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[0])
                        .into(ivCond1);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[1])
                        .into(ivCond2);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[2])
                        .into(ivCond3);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[3])
                        .into(ivCond4);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[4])
                        .into(ivCond5);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[5])
                        .into(ivCond6);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[6])
                        .into(ivCond7);
                Picasso.get()
                        .load(cityWeather.hourlyCondition[7])
                        .into(ivCond8);
                Picasso.get()
                        .load(cityWeather.weeklyCondition[0])
                        .into(ivWeeklyC1);
                Picasso.get()
                        .load(cityWeather.weeklyCondition[1])
                        .into(ivWeeklyC2);
                Picasso.get()
                        .load(cityWeather.weeklyCondition[2])
                        .into(ivWeeklyC3);
                Picasso.get()
                        .load(cityWeather.weeklyCondition[3])
                        .into(ivWeeklyC4);
                Picasso.get()
                        .load(cityWeather.weeklyCondition[4])
                        .into(ivWeeklyC5);
                Picasso.get()
                        .load(cityWeather.weeklyCondition[5])
                        .into(ivWeeklyC6);
                Picasso.get()
                        .load(cityWeather.weeklyCondition[6])
                        .into(ivWeeklyC7);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }
}

