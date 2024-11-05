package com.nudha.weatherapp.API.Meteomatics.requestCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimePartRequest {
    private static LocalDateTime nowDateTime = LocalDateTime.now();
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static StringBuilder stringBuilder = new StringBuilder();

    //TODO: change the name of the method
    //TODO: change if-else to switch
    public static String timeConvert(String period){
        if(period.equalsIgnoreCase("now")){
            stringBuilder.delete(0,stringBuilder.length());
            return nowTimeConverter();
        }else if(period.equalsIgnoreCase("24H")){
            stringBuilder.delete(0,stringBuilder.length());
            return next24HRequestConverter();
        }else if(period.equalsIgnoreCase("future")) {
            stringBuilder.delete(0, stringBuilder.length());
            return futureTimeConverter();
        }else if(period.equals("tommorow")){
            stringBuilder.delete(0, stringBuilder.length());
            return tomorrowTimeConverter();
        }else{
            return "You chose wrong time";
        }
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

    private static String futureTimeConverter(){
        return stringBuilder.append(LocalDate.now().plusDays(2).toString())
                .append("T13:00:00ZP5D:PT24H")
                .toString();
    }

    private static String tomorrowTimeConverter(){
        return stringBuilder.append(LocalDate.now().plusDays(1).toString())
                .append("T13:00:00Z")
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
