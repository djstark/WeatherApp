package com.stark.jsonproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private ProgressBar mLoadingProgress;
    private FusedLocationProviderClient mFusedLocationClient;
    private String locationQuery = null;
    private Location userLocation;
    private LatLng manualLocation;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;



    final private String CONDITION_QUERY = "/alerts/conditions/q/";
    final private String RADAR_QUERY = "http://api.wunderground.com/api/58c5745a92e613f5/radar/";
    private String fullRadarQuery = null;
    private static final int MAP_ACTIVITY_REQUEST_CODE = 0;
    Weather cityWeather = new Weather();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);

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
        Intent intent = new Intent(this, MapsActivity.class);
        if(userLocation != null) {
            intent.putExtra("lat", userLocation.getLatitude());
            intent.putExtra("long", userLocation.getLongitude());
        }
        else {
            intent.putExtra("lat", manualLocation.latitude);
            intent.putExtra("long", manualLocation.longitude);
        }
        startActivityForResult(intent,MAP_ACTIVITY_REQUEST_CODE);
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

            mLoadingProgress.setVisibility(View.INVISIBLE);
            if (result == null) {
                TextView tvError = findViewById(R.id.tv_error);
                tvError.setVisibility(View.VISIBLE);
            }
            else {

            }
            ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
            cityWeather = ApiUtil.getWeatherFromJSON(result);
            binding.setWeather(cityWeather);

            ImageView ivWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);
            ImageView ivRadar = (ImageView) findViewById(R.id.radar);
            TextView tvAlerts = (TextView) findViewById(R.id.alerts);
            String updateAlerts = "";
            if(cityWeather.alerts.length==0){
                updateAlerts = "No Weather alerts for this area.";
            }
            else{
                for (String alert : cityWeather.alerts) {
                    updateAlerts += alert + "\n";
                }
            }
            tvAlerts.setText(updateAlerts);
            Picasso.get()
                    .load(cityWeather.iconURL)
                    .resize(100, 100)
                    .into(ivWeatherIcon);
            Picasso.get()
                    .load(fullRadarQuery)
                    .resize(400,400)
                    .into(ivRadar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }
}

