package com.nudha.weatherapp.API.Meteomatics;

public class LocationPartRequest {
    private static String latitude;
    private static String longitude;

    public static void setLatitude(Double latitude1) {
        latitude = String.valueOf(latitude1);

    }

    public static  void setLongitude(Double longitude1) {
        longitude = String.valueOf(longitude1);
    }

    public static String getLocationPart(){
        return latitude + "," + longitude;
    }

}
