package com.nudha.weatherapp.API.Meteomatics.request;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("version")
    public String version;

    @SerializedName("user")
    public String user;

    @SerializedName("dateGenerated")
    public String dateGenerated;

    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public List<WeatherData> data;

    public static class WeatherData {
        @SerializedName("parameter")
        public String parameter;

        @SerializedName("coordinates")
        public List<Coordinate> coordinates;
    }

    public static class Coordinate {
        @SerializedName("lat")
        public double lat;

        @SerializedName("lon")
        public double lon;

        @SerializedName("dates")
        public List<DateValue> dates;
    }

    public static class DateValue {
        @SerializedName("date")
        public String date;

        @SerializedName("value")
        public double value;
    }
}
