package com.nudha.weatherapp.API.Meteomatics.request;

public class ApiService {
    private static ApiService instance;
    private WeatherApi weatherApi;

    private ApiService() {
        weatherApi = RetrofitClient.getClient().create(WeatherApi.class);
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public WeatherApi getWeatherApi() {
        return weatherApi;
    }

    public void changeBaseUrl(String newBaseUrl) {
        UrlManager.getInstance().setBaseUrl(newBaseUrl);
        RetrofitClient.resetClient();
        weatherApi = RetrofitClient.getClient().create(WeatherApi.class);
    }
}