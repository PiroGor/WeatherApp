package com.nudha.weatherapp.API.Meteomatics.requestCreator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimePartRequest {
    private static LocalDateTime nowDateTime = LocalDateTime.now();
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static StringBuilder stringBuilder = new StringBuilder();
    private static String lowTimeTemp = "03:00:00";
    private static String highTimeTemp = "15:00:00";

    //TODO: change the name of the method
    //TODO: change if-else to switch
    public static String timeConvert(String period){
        if(period.equalsIgnoreCase("now")){
            stringBuilder.delete(0,stringBuilder.length());
            return nowTimeConverter();
        }else if(period.equalsIgnoreCase("24H")){
            stringBuilder.delete(0,stringBuilder.length());
            return next24HRequestConverter();
        }else if(period.equalsIgnoreCase("7DaysLow")){
            stringBuilder.delete(0,stringBuilder.length());
            return next7DaysRequestConverter(lowTimeTemp);
        }else if (period.equalsIgnoreCase("7dayshigh")){
            stringBuilder.delete(0,stringBuilder.length());
            return next7DaysRequestConverter(highTimeTemp);
        }else{
            return "You chose wrong time";
        }
    }

    //TODO: connect with working archive weather API
    private static String previousTimeRequestConverter(int previousPeriod, String time) {
        return stringBuilder.append(requestFormat(nowDateTime.minusDays(previousPeriod)))
                .append("--")
                .append(nowTimeConverter())
                .append(":P1D")
                .replace(11,18, time)
                .toString();

    }

    private static String nowTimeConverter(){
        return requestFormat(nowDateTime).toString();
    }

    private static String next24HRequestConverter(){
        //2024-08-08T13:00:00ZP1D:PT1H
        return stringBuilder.append(nowTimeConverter())
                .append("P1D:PT1H")
                .toString();

    }

    private static String next7DaysRequestConverter(String time){
        return stringBuilder.append(requestFormat(nowDateTime.plusDays(1)))
                .append("--")
                .append(requestFormat(nowDateTime.plusDays(6)))
                .append(":P1D")
                .replace(11,19, time)
                .replace(33,41, time)
                .toString();
    }

    private static StringBuilder requestFormat(LocalDateTime time){
        StringBuilder dateTimeF = new StringBuilder(time.format(dateTimeFormatter)+"Z");
        dateTimeF.replace(10,11,"T");
        return dateTimeF;
    }

    private static void cleanBuilder(){
        stringBuilder.delete(0,stringBuilder.length());
    }

}
