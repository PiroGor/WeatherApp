package com.nudha.weatherapp.API.Meteomatics.requestCreator;

public class WeatherRequest {
    public static String nowWeather(){
        return "https://api.meteomatics.com/" + TimePartRequest.timeConvert("now")
                + "/" + TempPartRequest.getTemp()
                + "," + PrecipitationPartRequest.getPrecipitationPart("1h")
                + "," + WindSpeedPartRequest.getWindSpeedPart()
                + "," + WindSpeedPartRequest.getWindDirPart()
                + "/" + LocationPartRequest.getLocationCoordinates()
                + "/" + "json";
    }
    public static void setLocation(double latitude, double longitude){
        LocationPartRequest.setLatitude(latitude);
        LocationPartRequest.setLongitude(longitude);
    }
}
