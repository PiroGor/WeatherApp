package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nudha.weatherapp.Domains.Hourly;
import com.nudha.weatherapp.Adapters.HourlyAdapters;
import com.nudha.weatherapp.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    private TextView tempNow, highTemp, lowTemp,
            percipitation_now, wind_speed, uvIndx;
    private ImageView iconNow;

    private SimpleDateFormat sdf = new SimpleDateFormat("HH");

    private static final String WEATHER_DATA = "weather_data.txt";
    private static final String WEATHER_DATA_24_H = "weather_data_24H.txt";
    private static final String WEATHER_STATUS_ICONS = "weather_status_icons.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPopupTheme(R.style.MyMenuTheme);

        tempNow = findViewById(R.id.textView_tempNow);
        highTemp = findViewById(R.id.highTempTxt);
        lowTemp = findViewById(R.id.low_temp_TextView);
        percipitation_now = findViewById(R.id.percipitation_now_TextView);
        wind_speed = findViewById(R.id.wind_speed_now_TextView);
        uvIndx = findViewById(R.id.uvIndx_now_TextView);


        setVariable();

        setDataTime();
        setWeatherNow();

        initRecyclerview();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setWeatherNow();
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
//адаптировать после now TODO
    private void initRecyclerview(){
        //https://api.meteomatics.com/2024-08-08T16:00:00ZP1D:PT1H/t_2m:C,weather_symbol_1h:idx/50,10/json
        ArrayList<Hourly> items = getHourlyArrayList();

        recyclerView = findViewById(R.id.view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterHourly = new HourlyAdapters(items);
        recyclerView.setAdapter(adapterHourly);
    }

    private ArrayList<Hourly> getHourlyArrayList(){
        //Log.d("MainActivity", "Get Hourly ArrayList method called");
        ArrayList<Hourly> items = new ArrayList<>();

        String weatherData24H = readFromFile(WEATHER_DATA_24_H);

        if (weatherData24H != null) {
            String[] lines = weatherData24H.split("\n");
            for (String line : lines) {
                String[] parts = line.split("; ");
                if (parts.length == 3) {
                    String time = parts[0];
                    String temperature = parts[1];
                    Double temp = Double.parseDouble(temperature);
                    String icon_status = parts[2];
                    //Log.d("MainActivity", "Icon Status: " + icon_status);
                    String icon = getIcon(icon_status);
                    items.add(new Hourly(time.substring(11,13)+":00", temp, icon));
                }else {
                    Log.d("MainActivity", "No weather data found");
                }
            }
        }
        if (weatherData24H == null || weatherData24H.isEmpty()) {
            Log.d("MainActivity", "No weather data for 24H found");
            return items;
        }
        return items;
    }

    private String getIcon(String iconStatus) {
        Log.d("MainActivity", "Get Icon method called");

        // Получаем InputStream для файла
        InputStream inputStream = getResources().openRawResource(R.raw.weather_status_icons);

        // Читаем содержимое файла
        String iconPath = readFromFileInputStreamType(inputStream);

        if (iconPath != null && !iconPath.isEmpty()) {
            String[] parts = iconPath.split("\n");

            for (String part : parts) {
                String[] icon = part.split("; ");
                if (iconStatus.equals(icon[0]) || iconStatus.equals(icon[1])) {
                    return icon[2];  // Возвращаем иконку
                }
            }

            Log.d("MainActivity", "No weather data found for the given status");
            Log.d("MainActivity", "Status: " + iconStatus);
            return "0";
        }

        Log.d("MainActivity", "No weather data for 24H found");
        return "0";
    }

    private String readFromFileInputStreamType(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading file", e);
        }

        return stringBuilder.toString();
    }



    public void setDataTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd | HH:mm", Locale.ENGLISH);
        Date date = new Date();
        TextView data = findViewById(R.id.data_textView);
        data.setText(dateFormat.format(date));

    }

    public void setWeatherNow(){
        //Log.d("MainActivity", "Set Weather Now method called");
        String weatherData = readFromFile(WEATHER_DATA);

        if (weatherData != null) {
            String[] lines = weatherData.split("\n");

            for (String line : lines) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    setWeatherMain(value, key);
                }
            }
        }else {
            Log.d("MainActivity", "No weather data found");
        }
    }

    private String readFromFile(String fileName) {
        // Читаем данные из файла
        //Log.d("MainActivity", "Reading data from file");
        StringBuilder data = new StringBuilder();
        try (FileInputStream fis = openFileInput(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                data.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    private void setWeatherMain(String value, String key){
        //Log.d("MainActivity", "Set Weather Main method called");
        if (key.equals("tempNow")) {
            tempNow.setText(value + "°");
        }else if(key.equals("highTemp")){
            highTemp.setText("H: " + value + "°");
        }else if(key.equals("lowTemp")){
            lowTemp.setText(" L: " + value + "°");
        }else if(key.equals("percipitation_now")){
            percipitation_now.setText(value + "mm");
        }else if(key.equals("wind_speed")){
            wind_speed.setText(value + "m/s");
        }else if(key.equals("uvIndx")){
            uvIndx.setText(value);
        }else if(key.equals("iconNow")){
            iconNow = findViewById(R.id.weather_status_now_img);
            String icon = getIcon(value);
            int drawableId = getResources().getIdentifier(icon, "drawable", getPackageName());
            if (drawableId != 0) {  // Проверяем, что ресурс найден
                // Устанавливаем Drawable на ImageView
                iconNow.setImageResource(drawableId);
            } else {
                Log.e("MainActivity", "Drawable not found");
            }
        }
    }
}