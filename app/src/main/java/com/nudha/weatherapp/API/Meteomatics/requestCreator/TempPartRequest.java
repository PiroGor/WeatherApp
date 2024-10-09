package com.nudha.weatherapp.API.Meteomatics.requestCreator;

public class TempPartRequest {
    private static final String temp = "t_2m:C";

    public static String getTemp() {
        return temp;
    }

    public static String getTempStats(String statsTemp){
        if(statsTemp.equals("max24H")){
            return "t_max_2m_24h:C";
        }else if(statsTemp.equals("min24H")){
            return "t_min_2m_24h:C";
        }else{
            return temp;
        }
    }
}
