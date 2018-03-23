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

            Weather CityWeather = new Weather();

            try {
                JSONObject jsonWeather = new JSONObject(JSON);
                JSONObject currentJSON = jsonWeather.getJSONObject(CURRENT_OBSERVATION);
                JSONObject displayJSON = currentJSON.getJSONObject(DISPLAY_LOCATION);
                JSONArray alertsArray = jsonWeather.getJSONArray(ALERTS);

                int numberAlerts = alertsArray.length();
                String[] alerts = new String[numberAlerts];
                for(int i=0;i<numberAlerts;i++){
                    JSONObject alert = alertsArray.getJSONObject(i);
                    alerts[i] = alert.getString(ALERT_DESCRIPTION);
                }
                CityWeather = new Weather(
                        displayJSON.getString(FULL),
                        currentJSON.getString(OBSERVATION_TIME),
                        currentJSON.getString(TEMPERATURE_F),
                        currentJSON.getString(ICON),
                        currentJSON.getString(ICON_URL),
                        alerts);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return CityWeather;
        }
    }