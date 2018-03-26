        package com.stark.jsonproject;

        import android.net.Uri;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.Scanner;

        public class ApiUtil {
            private ApiUtil(){}

        public static final String BASE_API_URL =
                "http://api.wunderground.com/api/58c5745a92e613f5";

        public static URL buildUrl(String title) {

            URL url = null;
            Uri uri = Uri.parse(BASE_API_URL + title);

            try {
                url = new URL(uri.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return url;
        }

        public static String getJson(URL url) throws IOException {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                InputStream stream = connection.getInputStream();
                Scanner scanner = new Scanner(stream);
                scanner.useDelimiter("\\A");
                boolean hasData = scanner.hasNext();
                if (hasData) {
                    return scanner.next();
                } else {
                    return null;
                }
            }
            catch (Exception e){
                Log.d("Error", e.toString());
                return null;
            }
            finally {
                connection.disconnect();
            }
        }

        public static Weather getWeatherFromJSON (String JSON) {

            final String DISPLAY_LOCATION = "display_location";
            final String CURRENT_OBSERVATION = "current_observation";
            final String FULL = "full";
            final String OBSERVATION_TIME = "observation_time";
            final String TEMPERATURE_F = "temp_f";
            final String ICON = "icon";
            final String ICON_URL = "icon_url";
            final String ALERTS = "alerts";
            final String ALERT_DESCRIPTION = "description";
            final String HOURLY_FORECAST = "hourly_forecast";
            final String HOUR = "hour";
            final String TEMP = "temp";
            final String TEMP_ENGLISH = "english";
            final String CONDITION = "icon_url";
            final String AMPM = "ampm";
            final String DATE = "date";
            final String SIMPLE_FORECAST = "simpleforecast";
            final String WEEKDAY = "weekday_short";
            final String HIGH_TEMP = "high";
            final String LOW_TEMP = "low";
            final String FAHRENHEIT = "fahrenheit";
            final String FORECAST_DAY = "forecastday";
            final String FORECAST = "forecast";

            Weather CityWeather = new Weather();

            try {
                JSONObject jsonWeather = new JSONObject(JSON);
                JSONObject currentJSON = jsonWeather.getJSONObject(CURRENT_OBSERVATION);
                JSONObject displayJSON = currentJSON.getJSONObject(DISPLAY_LOCATION);
                JSONArray alertsArray = jsonWeather.getJSONArray(ALERTS);
                JSONArray hourlyArray = jsonWeather.getJSONArray(HOURLY_FORECAST);
                JSONObject forecastJSON = jsonWeather.getJSONObject(FORECAST);
                JSONObject simpleJSON = forecastJSON.getJSONObject(SIMPLE_FORECAST);


                int numberAlerts = alertsArray.length();
                String[] alerts = new String[numberAlerts];
                for(int i=0;i<numberAlerts;i++){
                    JSONObject alert = alertsArray.getJSONObject(i);
                    alerts[i] = alert.getString(ALERT_DESCRIPTION);
                }
                //HOURLY
                String[] hourly = new String[8];
                String[] temp = new String[8];
                String[] condition = new String[8];
                int count=0;
                for(int i=0; i<=28;i++){
                    if(i%4==0){
                        JSONObject obJSON = hourlyArray.getJSONObject(i);
                        JSONObject tempJSON = obJSON.getJSONObject(TEMP);
                        JSONObject fctJSON = obJSON.getJSONObject("FCTTIME");

                        if(Integer.valueOf(fctJSON.getString(HOUR)) > 12) {
                            int time = Integer.valueOf(fctJSON.getString(HOUR)) - 12;
                            hourly[count] = String.valueOf(time)+ fctJSON.getString(AMPM);
                        }
                        else {
                            hourly[count] = fctJSON.getString(HOUR) + fctJSON.getString(AMPM);
                        }
                        temp[count] = tempJSON.getString(TEMP_ENGLISH);
                        condition[count] = obJSON.getString(CONDITION);
                        count++;
                    }
                }

                //DAILY
                JSONArray dailyJSON = simpleJSON.getJSONArray(FORECAST_DAY);
                String[] weeklyDays = new String[7];
                String[] weeklyHighs = new String[7];
                String[] weeklyLows = new String[7];
                String[] weeklyConds = new String[7];
                for(int i=0;i<7;i++){
                    JSONObject dayJSON = dailyJSON.getJSONObject(i);
                    JSONObject dateJSON = dayJSON.getJSONObject(DATE);
                    JSONObject highJSON = dayJSON.getJSONObject(HIGH_TEMP);
                    JSONObject lowJSON = dayJSON.getJSONObject(LOW_TEMP);
                    weeklyDays[i] = dateJSON.getString(WEEKDAY);
                    weeklyHighs[i] = highJSON.getString(FAHRENHEIT);
                    weeklyLows[i] = lowJSON.getString(FAHRENHEIT);
                    weeklyConds[i] = dayJSON.getString(ICON_URL);
                }

                CityWeather = new Weather(
                        displayJSON.getString(FULL),
                        currentJSON.getString(OBSERVATION_TIME),
                        currentJSON.getString(TEMPERATURE_F),
                        currentJSON.getString(ICON),
                        currentJSON.getString(ICON_URL),
                        alerts,
                        hourly,
                        temp,
                        condition,
                        weeklyDays,
                        weeklyHighs,
                        weeklyLows,
                        weeklyConds);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return CityWeather;
        }
    }