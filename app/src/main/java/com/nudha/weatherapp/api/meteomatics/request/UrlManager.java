package com.nudha.weatherapp.api.meteomatics.request;

public class UrlManager {
    private static UrlManager instance;
    private String baseUrl;

    private UrlManager() {
        baseUrl = "https://api.meteomatics.com/";
    }

    public static UrlManager getInstance() {
        if (instance == null) {
            instance = new UrlManager();
        }
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}