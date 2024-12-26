package com.nudha.weatherapp.api.LocationByCityName;

import android.util.Log;

import com.nudha.weatherapp.api.meteomatics.requestCreator.LocationPartRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchCoordinatesUsingNominatim {
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/";
    private static final String SEARCH_ENDPOINT = "search?q=";
    private static final String FORMAT_ENDPOINT = "&format=json";

    public static void searchCoordinates(String cityName) {
        fetchCoordinates(cityName);
    }

    private static void fetchCoordinates(String cityName) {
        String url = BASE_URL+ SEARCH_ENDPOINT + cityName + FORMAT_ENDPOINT +"&limit=1";

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
                    JSONArray jsonArray = new JSONArray(response.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        double latitude = jsonObject.getDouble("lat");
                        double longitude = jsonObject.getDouble("lon");

                        // Используйте координаты latitude и longitude
                        LocationPartRequest.setLatitude(latitude);
                        LocationPartRequest.setLongitude(longitude);
                        Log.d("Coordinates", "City: " + cityName + ", Latitude: " + latitude + ", Longitude: " + longitude);
                    }
                } else {
                    Log.e("MainActivity", "Request failed. Response Code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}