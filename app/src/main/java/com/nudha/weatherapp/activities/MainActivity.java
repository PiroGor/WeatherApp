package com.nudha.weatherapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nudha.weatherapp.api.LocationByCityName.SearchCoordinatesUsingNominatim;
import com.nudha.weatherapp.api.LocationByCityName.SeatchingNameOfCityByCoordinates;
import com.nudha.weatherapp.api.meteomatics.requestCreator.LocationPartRequest;
import com.nudha.weatherapp.data.SetWeather;
import com.nudha.weatherapp.domains.Hourly;
import com.nudha.weatherapp.adapters.HourlyAdapters;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;
    private TextView tempNow, icon_description_now,highTemp, lowTemp,
            precipitation_now, wind_speed, uvIndx;
    private ImageView iconNow;

    private static final String WEATHER_DATA = "weather_data.txt";
    private static final String WEATHER_DATA_24_H = "weather_data_24H.txt";

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPopupTheme(R.style.MyMenuTheme);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        tempNow = findViewById(R.id.textView_tempNow);
        highTemp = findViewById(R.id.highTempTxt);
        lowTemp = findViewById(R.id.low_temp_TextView);
        precipitation_now = findViewById(R.id.percipitation_now_TextView);
        wind_speed = findViewById(R.id.wind_speed_now_TextView);
        uvIndx = findViewById(R.id.uvIndx_now_TextView);
        icon_description_now = findViewById(R.id.wether_description_now_TextView);

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                setVariable();
                setDataTime();

                Objects.requireNonNull(getSupportActionBar()).setTitle(SeatchingNameOfCityByCoordinates.
                    searchCityName(LocationPartRequest.getLatitude(), LocationPartRequest.getLongitude()));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initRecyclerview();
                        setWeatherNow();
                    }
                });
            }
        }).start();

        swipeRefreshLayout.setOnRefreshListener(this::refreshWeatherData);

    }

    private void refreshWeatherData(){
        swipeRefreshLayout.setRefreshing(true); // Показать индикатор загрузки

        // Обновление данных
        updateWeatherData();
        setDataTime();

        // Обновляем заголовок города
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                SeatchingNameOfCityByCoordinates.searchCityName(
                        LocationPartRequest.getLatitude(),
                        LocationPartRequest.getLongitude()
                )
        );

        // Обновление RecyclerView
        recyclerView.post(() -> {
            initRecyclerview(); // Обновление элементов списка
            adapterHourly.notifyDataSetChanged(); // Уведомить адаптер об изменениях
        });

        // Скрыть индикатор загрузки после задержки
        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
    }

    private void updateWeatherData() {
        new Thread(() -> {
            // Обновляем данные погоды и сохраняем их
            SetWeather.setWeather(MainActivity.this);

            runOnUiThread(() -> {
                // Обновляем интерфейс с новыми данными
                setWeatherNow();

                // Обновляем данные для адаптера
                ArrayList<Hourly> items = getHourlyArrayList();
                ((HourlyAdapters) adapterHourly).updateData(items); // Обновляем данные адаптера
                adapterHourly.notifyDataSetChanged(); // Уведомляем адаптер о новых данных
            });
        }).start();
    }



    @Override
    protected void onResume() {
        super.onResume();
        setWeatherNow();
        initRecyclerview();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_update) {
            updateWeatherData();
            return true;
        } else if (itemId == R.id.archive) {
            Intent intent = new Intent(this, ArchiveActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        }else if(itemId == R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Обработка поискового запроса
                SearchCoordinatesUsingNominatim.searchCoordinates(query);
                Toast.makeText(MainActivity.this, "Поиск: " + query, Toast.LENGTH_SHORT).show();

                // Закрываем строку поиска после отправки запроса
                searchView.clearFocus();  // Убираем фокус с SearchView
                searchItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Реагируем на изменение текста, если нужно
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Можно добавить дополнительную логику при закрытии
                Toast.makeText(MainActivity.this, "Поиск закрыт", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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

        if (adapterHourly == null) {
            adapterHourly = new HourlyAdapters(items, this);
            recyclerView.setAdapter(adapterHourly);
        } else {
            adapterHourly.notifyDataSetChanged();
        }
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
                    double temp = Double.parseDouble(temperature);
                    String icon_status = parts[2];
                    //Log.d("MainActivity", "Icon Status: " + icon_status);
                    String icon = getIconIdxDescription(icon_status, "icon");
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


    public String getIconIdxDescription(String iconIdx, String returnDataType){
        if(returnDataType.equals("icon")){
         return readFromWeatherStatusIconFile(1, iconIdx);
        }else if(returnDataType.equals("description")){
            return readFromWeatherStatusIconFile(2, iconIdx);
        }else{
            return "0";
        }
    }

    private String readFromWeatherStatusIconFile(int returnData, String iconIdx){
        // Получаем InputStream для файла
        InputStream inputStream = getResources().openRawResource(R.raw.weather_status_icons);

        // Читаем содержимое файла
        String iconPath = readFromFileInputStreamType(inputStream);

            if (iconPath != null && !iconPath.isEmpty()) {
                String[] parts = iconPath.split("\n");

                for (String part : parts) {
                    String[] icon = part.split("; ");
                    Double iconDouble = Double.parseDouble(iconIdx);
                    int iconInt = iconDouble.intValue();
                    //Log.d("MainActivity", "Icon int: " + iconInt);
                    String iconStr = iconInt +"";

                    if (iconStr.equals(icon[0])) {
                        return icon[returnData];  // Возвращаем иконк0у
                    }
                }

                Log.d("MainActivity", "No weather data found for the given status");
                Log.d("MainActivity", "Status: " + returnData);
                return "0";
            }

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
            precipitation_now.setText(value + "mm");
        }else if(key.equals("wind_speed")){
            wind_speed.setText(value + "m/s");
        }else if(key.equals("uvIndx")){
            uvIndx.setText(value);
        }else if(key.equals("iconNow")){
            icon_description_now.setText(getIconIdxDescription(value, "description"));
            iconNow = findViewById(R.id.weather_status_now_img);
            String icon = getIconIdxDescription(value, "icon");
            int drawableId = getResources().getIdentifier(icon, "drawable", getPackageName());
            if (drawableId != 0) {  // Проверяем, что ресурс найден
                // Устанавливаем Drawable на ImageView
                iconNow.setImageResource(drawableId);
            } else {
                Log.e("MainActivity", "Drawable not found");
            }
        }
    }

//    private void updateWeatherData() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SetWeather.setWeather(MainActivity.this);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setWeatherNow();
//                        initRecyclerview();
//                    }
//                });
//            }
//
//        }).start();
//    }

//    private void refreshWeatherData(){
//        updateWeatherData();
//        setDataTime();
//
//        Objects.requireNonNull(getSupportActionBar()).setTitle(SeatchingNameOfCityByCoordinates.
//                searchCityName(LocationPartRequest.getLatitude(), LocationPartRequest.getLongitude()));
//
//        Log.d("Main","Coordinates "+ LocationPartRequest.getLocationCoordinates());
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        },2000);
//    }
}