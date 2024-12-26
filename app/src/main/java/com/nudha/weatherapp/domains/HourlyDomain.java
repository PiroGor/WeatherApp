package com.nudha.weatherapp.domains;

public class HourlyDomain {
    private String hour;
    private Double temp;
    private String picPath;

    public HourlyDomain(String hour, double temp, String picPath) {
        this.hour = hour;
        this.temp = temp;
        this.picPath = picPath;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
