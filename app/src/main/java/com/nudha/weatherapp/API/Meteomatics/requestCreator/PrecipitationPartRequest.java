package com.nudha.weatherapp.API.Meteomatics.requestCreator;

public class PrecipitationPartRequest {
    private static final String PRECIPPER24H ="precip_24h:mm";
    private static final String PRECIPPER1H ="precip_1h:mm";

    public static String getPrecipitationPart(String part) {
        switch (part) {
            case "24h":
                return PRECIPPER24H;
            case "1h":
                return PRECIPPER1H;
            default:
                return null;
        }
    }
}
