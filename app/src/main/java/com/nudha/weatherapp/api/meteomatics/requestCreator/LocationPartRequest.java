package com.nudha.weatherapp.api.meteomatics.requestCreator;

import android.util.Log;

public class LocationPartRequest {
    private static String latitude = "51.12324958272044";
    private static String longitude = "17.045539809381435";

    public static void setLatitude(Double latitude1) {
        Log.d("Latitude","Lat changed"+latitude1);
        latitude = String.valueOf(latitude1);

    }

    public static  void setLongitude(Double longitude1) {
        Log.d("Longitude","Long changed"+longitude1);
        longitude = String.valueOf(longitude1);
    }

    public static String getLocationCoordinates(){
        return latitude + "," + longitude;
    }

    public static String getLatitude() {
        return latitude;
    }

    public static String getLongitude() {
        return longitude;
    }

}
