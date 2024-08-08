package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;// 3 seconds
    private SharedPreferences sharedPreferences;
    private LocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        locationUtils = new LocationUtils(this);
        locationUtils.requestLocation();

        saveWeatherData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    // Определяем тип данных и передаем в Intent
                    if (entry.getValue() instanceof String) {
                        intent.putExtra(entry.getKey(), (String) entry.getValue());
                    } else if (entry.getValue() instanceof Integer) {
                        intent.putExtra(entry.getKey(), (Integer) entry.getValue());
                    } else if (entry.getValue() instanceof Boolean) {
                        intent.putExtra(entry.getKey(), (Boolean) entry.getValue());
                    } else if (entry.getValue() instanceof Float) {
                        intent.putExtra(entry.getKey(), (Float) entry.getValue());
                    } else if (entry.getValue() instanceof Long) {
                        intent.putExtra(entry.getKey(), (Long) entry.getValue());
                    }}
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationUtils.handlePermissionResult(requestCode, permissions, grantResults);

    }


    //https://api.meteomatics.com/2024-08-02T00:00:00ZPT1H/t_2m:C,t_max_2m_24h:C,t_min_2m_24h:C,precip_1h:mm,wind_speed_10m:ms,uv:idx,weather_symbol_1h:idx/50,10/json

    //t_2:C - actual temperature
    //t_max_2m_24h:C - max temperature
    //t_min_2m_24h:C - min temperature
    //precip_1h:mm - precipitation
    //wind_speed_10m:ms - wind speed
    //uv:idx - uv index
    //weather_symbol_1h:idx - weather symbol
    public void saveWeatherData(){
        ApiService.getInstance().changeBaseUrl("https://api.meteomatics.com/");

        String parameters = TempPartRequest.getTemp() + ","
                + TempPartRequest.getTempStats("max24H") + ","
                + TempPartRequest.getTempStats("min24H") + ","
                + PrecipitationPartRequest.getPrecipitationPart("1h") + ","
                + WindSpeedPartRequest.getWindSpeedPart() + ","
                + "uv:idx" + "," + "weather_symbol_1h:idx" ;

        ApiService.getInstance().getWeatherApi().getWeather(TimePartRequest.timeConvert("now"),
                parameters, LocationPartRequest.getLocationCoordinates()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if(response.isSuccessful()){
                    WeatherResponse weatherResponse = response.body();
                    Log.d("MainActivity", "Response: " + weatherResponse);
                    if (weatherResponse != null && weatherResponse.getData() != null
                            && !weatherResponse.getData().isEmpty()) {
                        setData(weatherResponse, TempPartRequest.getTemp(), "tempNow");

                        Log.d("MainActivity", "Response body: " + weatherResponse.getData());
                        setData(weatherResponse, TempPartRequest.getTempStats("max24H"), "highTemp");
                        setData(weatherResponse, TempPartRequest.getTempStats("min24H"), "lowTemp");

                        setData(weatherResponse, PrecipitationPartRequest.getPrecipitationPart("1h"), "percipitation_now");

                        setData(weatherResponse, WindSpeedPartRequest.getWindSpeedPart(), "wind_speed");

                        setData(weatherResponse, "uv:idx", "uvIndx");

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

    private void setData(WeatherResponse weatherResponse, String parameter, String paramName) {
        // Получаем данные из ответа
        WeatherResponse.Data data = findParameter(weatherResponse.getData(), parameter);
        WeatherResponse.Data.Coordinate coordinate = data.getCoordinates().get(0);
        WeatherResponse.Data.Coordinate.DateValue dateValue = coordinate.getDates().get(0);

        // Сохраняем данные в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(paramName, String.valueOf(dateValue.getValue()));
        editor.apply();
        Log.d("Saved param", String.valueOf(editor));

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