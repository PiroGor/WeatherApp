package com.nudha.weatherapp.API.Meteomatics.requestCreator;

public class LocationPartRequest {
    private static String latitude = "50.4501";
    private static String longitude = "30.5234";

    public static void setLatitude(Double latitude1) {
        latitude = String.valueOf(latitude1);

    }

    public static  void setLongitude(Double longitude1) {
        longitude = String.valueOf(longitude1);
    }

    public static String getLocationCoordinates(){
        return latitude + "," + longitude;
    }

}
