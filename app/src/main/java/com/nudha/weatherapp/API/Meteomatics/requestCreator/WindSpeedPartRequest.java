package com.nudha.weatherapp.API.Meteomatics.requestCreator;

public class WindSpeedPartRequest {
    private static final String WINDSPEED = "wind_speed_10m:ms";
    private static final String WINDDIR = "wind_dir_10m:d";

    public static String getWindSpeedPart() {
        return WINDSPEED;
    }

    public static String getWindDirPart() {
        return WINDDIR;
    }
}
