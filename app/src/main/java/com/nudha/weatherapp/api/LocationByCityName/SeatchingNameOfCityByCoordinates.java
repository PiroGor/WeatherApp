package com.nudha.weatherapp.api.LocationByCityName;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SeatchingNameOfCityByCoordinates {
        private static final String BASE_URL = "https://nominatim.openstreetmap.org/reverse";
        private static final String FORMAT_ENDPOINT = "&format=json";
        private static final String LATITUDE_ENDPOINT = "?lat=";
        private static final String LONGITUDE_ENDPOINT = "&lon=";
        private static String cityName = "Unknown city";

        public static String searchCityName(String latitude, String longitude) {
            fetchCityName(latitude, longitude);
            return cityName;
        }

    private static void fetchCityName(String latitude, String longitude) {
        String url = BASE_URL + LATITUDE_ENDPOINT + latitude + LONGITUDE_ENDPOINT
                + longitude + FORMAT_ENDPOINT;

        new Thread(() -> {
            try {
                URL obj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Обработка JSON-ответа
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String city = jsonObject.getJSONObject("address").optString("city", "Unknown city");

                    if(city.equals("Unknown city")){
                        String village = jsonObject.getJSONObject("address").optString("village", "Unknown village");
                        cityName = village;
                    }else{
                        cityName = city;
                    }

                    Log.d("City name","City: "+cityName);
                    Log.d("SeatchingNameOfCityByCoordinates", "Response: " + response.toString());
                    // Используйте название города (city)
                } else {
                    Log.e("MainActivity", "Request failed. Response Code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
