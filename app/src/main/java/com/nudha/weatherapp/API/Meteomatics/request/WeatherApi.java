package com.nudha.weatherapp.API.Meteomatics.request;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherApi {
    @GET("{date}/{parameter}/{coordinates}/json")
    Call<WeatherResponse> getWeather(
            @Path("date") String date,
            @Path("parameter") String parameter,
            @Path("coordinates") String coordinates
    );
}