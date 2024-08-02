package com.nudha.weatherapp.API.Meteomatics.request;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import java.util.List;

public class WeatherResponse {
    private String version;
    private String user;
    private String dateGenerated;
    private String status;
    private List<Data> data;

    // Getters and Setters

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getDateGenerated() { return dateGenerated; }
    public void setDateGenerated(String dateGenerated) { this.dateGenerated = dateGenerated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Data> getData() { return data; }
    public void setData(List<Data> data) { this.data = data; }

    public static class Data {
        private String parameter;
        private List<Coordinate> coordinates;

        // Getters and Setters

        public String getParameter() { return parameter; }
        public void setParameter(String parameter) { this.parameter = parameter; }

        public List<Coordinate> getCoordinates() { return coordinates; }
        public void setCoordinates(List<Coordinate> coordinates) { this.coordinates = coordinates; }

        public static class Coordinate {
            private double lat;
            private double lon;
            private List<DateValue> dates;

            // Getters and Setters

            public double getLat() { return lat; }
            public void setLat(double lat) { this.lat = lat; }

            public double getLon() { return lon; }
            public void setLon(double lon) { this.lon = lon; }

            public List<DateValue> getDates() { return dates; }
            public void setDates(List<DateValue> dates) { this.dates = dates; }

            public static class DateValue {
                private String date;
                private double value;

                // Getters and Setters

                public String getDate() { return date; }
                public void setDate(String date) { this.date = date; }

                public double getValue() { return value; }
                public void setValue(double value) { this.value = value; }
            }
        }
    }
}

