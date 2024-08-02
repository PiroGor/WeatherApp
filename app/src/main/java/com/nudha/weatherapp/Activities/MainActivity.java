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
import com.nudha.weatherapp.API.Meteomatics.requestCreator.PrecipitationPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TempPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TimePartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.WindSpeedPartRequest;
import com.nudha.weatherapp.Domains.Hourly;
import com.nudha.weatherapp.Adapters.HourlyAdapters;
import com.nudha.weatherapp.R;
import com.nudha.weatherapp.permissions.LocationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    private LocationUtils locationUtils;
    private TextView tempNow, highLowTemp,
            windSpeedNow;
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
        highLowTemp = findViewById(R.id.high_low_temp_TextView);


        locationUtils = new LocationUtils(this);
        locationUtils.requestLocation();


        initRecyclerview();

        setVariable();

        setDataTime();

        String savedData = sharedPreferences.getString("tempNow", null);
        String savedTime = sharedPreferences.getString("timeNow", null);
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

    public void setDataTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd | HH:mm", Locale.ENGLISH);
        Date date = new Date();
        TextView data = findViewById(R.id.data_textView);
        data.setText(dateFormat.format(date));

    }

    public void setWeatherNow(){
        //https://api.meteomatics.com/2024-08-02T00:00:00ZPT1H/t_2m:C,t_max_2m_24h:C,t_min_2m_24h:C,precip_1h:mm,wind_speed_10m:ms,uv:idx,weather_symbol_1h:idx/50,10/json

        //t_2:C - actual temperature
        //t_max_2m_24h:C - max temperature
        //t_min_2m_24h:C - min temperature
        //precip_1h:mm - precipitation
        //wind_speed_10m:ms - wind speed
        //uv:idx - uv index
        //weather_symbol_1h:idx - weather symbol


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
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful()){
                    WeatherResponse weatherResponse = response.body();
                    Log.d("MainActivity", "Response: " + weatherResponse);

                    if (weatherResponse != null && weatherResponse.getData() != null
                            && !weatherResponse.getData().isEmpty()) {
                        tempNow.setText(setData(weatherResponse, TempPartRequest.getTemp(), "tempNow"));

                        String highTemp = setData(weatherResponse, TempPartRequest.getTempStats("max24H"), "highTemp");
                        String lowTemp = setData(weatherResponse, TempPartRequest.getTempStats("min24H"), "lowTemp");
                        String highLowTempString ="H: " + highTemp + " L: " + lowTemp;

                        highLowTemp.setText("highLowTempString");
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

    private String setData(WeatherResponse weatherResponse, String parameter, String paramName) {
        WeatherResponse.Data data = findParameter(weatherResponse.getData(), parameter);
        WeatherResponse.Data.Coordinate coordinate = data.getCoordinates().get(0);
        WeatherResponse.Data.Coordinate.DateValue dateValue = coordinate.getDates().get(0);

        // Сохраняем данные в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(paramName, String.valueOf(dateValue.getValue()));
        editor.apply();

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