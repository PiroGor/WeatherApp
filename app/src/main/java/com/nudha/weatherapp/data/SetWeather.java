package com.nudha.weatherapp.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nudha.weatherapp.api.meteomatics.request.ApiService;
import com.nudha.weatherapp.api.meteomatics.request.WeatherResponse;
import com.nudha.weatherapp.api.meteomatics.requestCreator.LocationPartRequest;
import com.nudha.weatherapp.api.meteomatics.requestCreator.PrecipitationPartRequest;
import com.nudha.weatherapp.api.meteomatics.requestCreator.TempPartRequest;
import com.nudha.weatherapp.api.meteomatics.requestCreator.TimePartRequest;
import com.nudha.weatherapp.api.meteomatics.requestCreator.WindSpeedPartRequest;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetWeather {
    private static String tomorrow="";

    public static void setWeather(Context context){
        setWeatherDataNow(context);
        setWeatherDataFor24H(context);
        setWeatherDataFuture(context);
        setWeatherDataTomorrow(context);
    }

    private static void setWeatherDataFuture(Context context){
        ApiService.getInstance().changeBaseUrl("https://api.meteomatics.com/");
        String parameters = TempPartRequest.getTempStats("max24H") + ","
                + TempPartRequest.getTempStats("min24H") + ","
                + "weather_symbol_1h:idx";

        ApiService.getInstance().getWeatherApi().getWeather(TimePartRequest.timeConvert("future"),
                parameters, LocationPartRequest.getLocationCoordinates()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                //Log.d("Splash", "Response: " + response);
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null && weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                        // Сохраняем данные в файл
                        try {
                            SaveResponseData.setWeather(context, weatherResponse, "future");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("SplashFuture", "Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void setWeatherDataFor24H(Context context){
        ApiService.getInstance().changeBaseUrl("https://api.meteomatics.com/");

        String parameters = TempPartRequest.getTemp() + "," + "weather_symbol_1h:idx";

        ApiService.getInstance().getWeatherApi().getWeather(TimePartRequest.timeConvert("24H"),
                parameters, LocationPartRequest.getLocationCoordinates()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null && weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                        // Сохраняем данные в файл
                        try {
                            SaveResponseData.setWeather(context, weatherResponse, "24H");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setWeatherDataNow(Context context) {
        //Log.d("Splash", LocationPartRequest.getLocationCoordinates());
        ApiService.getInstance().changeBaseUrl("https://api.meteomatics.com/");

        String parameters = TempPartRequest.getTemp() + ","
                + TempPartRequest.getTempStats("max24H") + ","
                + TempPartRequest.getTempStats("min24H") + ","
                + PrecipitationPartRequest.getPrecipitationPart("1h") + ","
                + WindSpeedPartRequest.getWindSpeedPart() + ","
                + "uv:idx" + "," + "weather_symbol_1h:idx";

        ApiService.getInstance().getWeatherApi().getWeather(TimePartRequest.timeConvert("now"),
                parameters, LocationPartRequest.getLocationCoordinates()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null && weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                        // Сохраняем данные в файл
                        try {
                            SaveResponseData.setWeather(context, weatherResponse, "now");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //Log.d("Splash", "Response: " + response);
            }


            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void setWeatherDataTomorrow(Context context){
        ApiService.getInstance().changeBaseUrl("https://api.meteomatics.com/");
        String parameters = TempPartRequest.getTempStats("max24H") + ","
                + TempPartRequest.getTempStats("min24H") + ","
                + "weather_symbol_1h:idx";

        ApiService.getInstance().getWeatherApi().getWeather(TimePartRequest.timeConvert("tomorrow"),
                parameters, LocationPartRequest.getLocationCoordinates()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null && weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                        // Сохраняем данные в файл
                        tomorrow = SaveResponseData.collectWeatherDataTomorrowNotification(weatherResponse);
                    }
                }
                //Log.d("Splash", "Response: " + response);
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static String getTomorrowData(){
        return tomorrow;
    }
}
