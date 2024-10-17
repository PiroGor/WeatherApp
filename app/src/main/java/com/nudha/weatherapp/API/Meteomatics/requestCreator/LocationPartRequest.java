package com.nudha.weatherapp.API.Meteomatics.requestCreator;

public class LocationPartRequest {
    private static String latitude = "51.12324958272044";
    private static String longitude = "17.045539809381435";

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
