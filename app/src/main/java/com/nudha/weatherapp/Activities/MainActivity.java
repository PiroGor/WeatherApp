package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nudha.weatherapp.API.Meteomatics.request.ApiService;
import com.nudha.weatherapp.API.Meteomatics.request.WeatherResponse;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.LocationPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TempPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TimePartRequest;
import com.nudha.weatherapp.Domains.Hourly;
import com.nudha.weatherapp.Adapters.HourlyAdapters;
import com.nudha.weatherapp.R;
import com.nudha.weatherapp.permissions.LocationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    private LocationUtils locationUtils;
    private TextView tempNow, rainPercentNow,
            windSpeedNow, humidityPercentNow;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPopupTheme(R.style.MyMenuTheme);

        tempNow = findViewById(R.id.textView_tempNow);
        rainPercentNow = findViewById(R.id.rain_percent_now_TextView);
        windSpeedNow = findViewById(R.id.wind_speed_now_TextView);
        humidityPercentNow = findViewById(R.id.humidity_percent_now_TextView);

        locationUtils = new LocationUtils(this);
        locationUtils.requestLocation();


        initRecyclerview();

        setVariable();

        setData();

        //Сделать так чтобы оно с открытием приложения подгружало данные о погоде

        String savedData = sharedPreferences.getString("tempNow", null);
        if (savedData != null) {
            tempNow.setText(savedData);
        }else{
            setWeatherNow();
        }


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_update) {
            Toast.makeText(this, "You selected Update", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.archive) {
            Intent intent = new Intent(this, ArchiveActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top_main, menu);
        return true;
    }

    private void setVariable(){
        TextView next7days_btn = findViewById(R.id.nextBtn);
        next7days_btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FutureActivity.class))
        );
    }
//адаптировать после now
    private void initRecyclerview(){
        ArrayList<Hourly> items = new ArrayList<>();

        items.add(new Hourly("9 ",23,"cloudy"));
        items.add(new Hourly("11 ",24,"sunny"));
        items.add(new Hourly("12 ",26,"wind"));
        items.add(new Hourly("1 ",26,"rainy"));
        items.add(new Hourly("2 ",27,"storm"));

        recyclerView = findViewById(R.id.view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterHourly = new HourlyAdapters(items);
        recyclerView.setAdapter(adapterHourly);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationUtils.handlePermissionResult(requestCode, permissions, grantResults);
    }

    public void setData(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd | HH:mm", Locale.ENGLISH);
        Date date = new Date();
        TextView data = findViewById(R.id.data_textView);
        data.setText(dateFormat.format(date));

    }

    public void setWeatherNow(){
        ApiService.getInstance().changeBaseUrl("https://api.meteomatics.com/");

        ApiService.getInstance().getWeatherApi().getWeather(TimePartRequest.timeConvert("now"),
                TempPartRequest.getTemp(), LocationPartRequest.getLocationCoordinates()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful()){
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null && weatherResponse.getData() != null
                            && !weatherResponse.getData().isEmpty()) {
                        WeatherResponse.Data firstData = weatherResponse.getData().get(0);
                        WeatherResponse.Data.Coordinate firstCoordinate = firstData.getCoordinates().get(0);
                        WeatherResponse.Data.Coordinate.DateValue firstDateValue = firstCoordinate.getDates().get(0);

                        //Save data to shared preferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tempNow", String.valueOf(firstDateValue.getValue()));
                        editor.apply();

                        tempNow.setText(String.valueOf(firstDateValue.getValue()));
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}