package com.nudha.weatherapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.nudha.weatherapp.API.Meteomatics.request.ApiService;
import com.nudha.weatherapp.API.Meteomatics.request.WeatherResponse;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.LocationPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.PrecipitationPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TempPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TimePartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.WindSpeedPartRequest;

import com.nudha.weatherapp.API.Meteomatics.responce.SaveResponseData;
import com.nudha.weatherapp.R;
import com.nudha.weatherapp.permissions.LocationUtils;

import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private LocationUtils locationUtils;
    private Context context = SplashScreenActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        locationUtils = new LocationUtils(this);
        locationUtils.requestLocation();

//        FileOutputStream fos = null;
//        try {
//            // Открываем файл в режиме перезаписи (это очистит содержимое файла)
//            fos = context.openFileOutput("weather_data_future.txt", Context.MODE_PRIVATE);
//            // Поскольку мы ничего не пишем, файл будет очищен
//            // Просто закрываем поток
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        clearImageCache();
        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }



        new Thread(new Runnable() {
            @Override
            public void run() {
                setWeatherData();
                setWeatherDataFor24H();
                setWeatherDataFuture();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Скрытие Splash Screen и отображение основного содержимого
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        }).start();
    }

    private void clearImageCache(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(SplashScreenActivity.this).clearDiskCache();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(SplashScreenActivity.this).clearMemory();
                    }
                });
            }
        }).start();
    }

    public void setWeatherDataFuture(){
        ApiService.getInstance().changeBaseUrl("https://api.meteomatics.com/");
        Log.d("Splash", LocationPartRequest.getLocationCoordinates());
        String parameters = TempPartRequest.getTempStats("max24H") + ","
                + TempPartRequest.getTempStats("min24H") + ","
                + "weather_symbol_1h:idx";

        ApiService.getInstance().getWeatherApi().getWeather(TimePartRequest.timeConvert("future"),
                parameters, LocationPartRequest.getLocationCoordinates()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d("Splash", "Response: " + response);
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
                Toast.makeText(SplashScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setWeatherDataFor24H(){
        //Log.d("Splash","24H Weather");
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
                Toast.makeText(SplashScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setWeatherData() {
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
                Toast.makeText(SplashScreenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
