package com.nudha.weatherapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nudha.weatherapp.API.Meteomatics.request.ApiService;
import com.nudha.weatherapp.API.Meteomatics.request.WeatherResponse;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.LocationPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.PrecipitationPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TempPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TimePartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.WindSpeedPartRequest;

import com.nudha.weatherapp.R;
import com.nudha.weatherapp.permissions.LocationUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000; // 3 seconds
    private LocationUtils locationUtils;
    private static final String FILE_NAME = "weather_data.txt";
    private static final String FILE_NAME_24H = "weather_data_24H.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        locationUtils = new LocationUtils(this);
        locationUtils.requestLocation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveWeatherData();
                saveWeatherDataFor24H();
            }
        }, SPLASH_TIME_OUT);
    }

    public void saveWeatherDataFor24H(){
        Log.d("Splash","24H Weather");
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
                            saveToFileFor24H(collectWeatherDataFor24H(weatherResponse));
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(SplashScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFileFor24H(String data) throws IOException {
        FileOutputStream fos = openFileOutput(FILE_NAME_24H, Context.MODE_PRIVATE);
        fos.write(data.getBytes());
        fos.close();
        Log.d("SplashScreenActivity", "24H Data saved to file");
    }

    private String collectWeatherDataFor24H(WeatherResponse weatherResponse) {
        StringBuilder data = new StringBuilder();

        // Получение данных температуры (t_2m:C) и иконок (weather_symbol_1h:idx)
        WeatherResponse.Data tempData = findParameter(weatherResponse.getData(), "t_2m:C");
        WeatherResponse.Data iconData = findParameter(weatherResponse.getData(), "weather_symbol_1h:idx");

        if (tempData != null && iconData != null) {
            List<WeatherResponse.Data.Coordinate.DateValue> tempDates = tempData.getCoordinates().get(0).getDates();
            List<WeatherResponse.Data.Coordinate.DateValue> iconDates = iconData.getCoordinates().get(0).getDates();

            // Проходим по данным и собираем информацию за 24 часа
            for (int i = 0; i < tempDates.size() && i < iconDates.size(); i++) {
                String time = tempDates.get(i).getDate();
                double temperature = tempDates.get(i).getValue();
                int icon = (int) iconDates.get(i).getValue();

                // Форматирование строки: Время: Температура: Иконка
                data.append(String.format("%s; %.1f; %d\n", time, temperature, icon));
            }
        }

        return data.toString();
    }


    public void saveWeatherData() {
        Log.d("Splash", LocationPartRequest.getLocationCoordinates());
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
                            saveToFile(collectWeatherData(weatherResponse));
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(SplashScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Сохранение данных в файл
    private void saveToFile(String data) throws IOException {
        FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(data.getBytes());
        fos.close();
        Log.d("SplashScreenActivity", "Data saved to file");
    }

    // Форматирование данных для записи в файл
    private String collectWeatherData(WeatherResponse weatherResponse) {
        StringBuilder data = new StringBuilder();
        data.append("tempNow: ")
                .append(setData(weatherResponse, TempPartRequest.getTemp(), "tempNow"))
                .append("\nhighTemp: ")
                .append(setData(weatherResponse, TempPartRequest.getTempStats("max24H"), "highTemp"))
                .append("\nlowTemp: ")
                .append(setData(weatherResponse, TempPartRequest.getTempStats("min24H"), "lowTemp"))
                .append("\npercipitation_now: ")
                .append(setData(weatherResponse, PrecipitationPartRequest.getPrecipitationPart("1h"), "percipitation_now"))
                .append("\nwind_speed: ")
                .append(setData(weatherResponse, WindSpeedPartRequest.getWindSpeedPart(), "wind_speed"))
                .append("\nuvIndx: ")
                .append(setData(weatherResponse, "uv:idx", "uvIndx"))
                .append("\niconNow: ")
                .append(setData(weatherResponse, "weather_symbol_1h:idx", "iconNow"));
        return data.toString();
    }

    private String setData(WeatherResponse weatherResponse, String parameter, String paramName) {
        // Получаем данные из ответа
        WeatherResponse.Data data = findParameter(weatherResponse.getData(), parameter);
        WeatherResponse.Data.Coordinate coordinate = data.getCoordinates().get(0);
        WeatherResponse.Data.Coordinate.DateValue dateValue = coordinate.getDates().get(0);
        return String.valueOf(dateValue.getValue());
    }

    private WeatherResponse.Data findParameter(List<WeatherResponse.Data> data, String parameter) {
        for (WeatherResponse.Data datum : data) {
            if (datum.getParameter().equals(parameter)) {
                return datum;
            }
        }
        return null;
    }
}
