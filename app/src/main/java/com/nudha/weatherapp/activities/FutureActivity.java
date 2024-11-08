
package com.nudha.weatherapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nudha.weatherapp.adapters.FutureAdapter;
import com.nudha.weatherapp.domains.FutureDomain;
import com.nudha.weatherapp.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class FutureActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterTomorrow;
    public RecyclerView recyclerView;
    private ImageView backBtn, icon;
    private final String WEATHER_DATA_FUTURE = "weather_data_future.txt";
    private final String WEATHER_DATA_TOMORROW = "weather_data_tomorrow.txt";
    private TextView tempTomorrowTxt, statusTomorrowTxt, precipitationTomorrowTxt, windTomorrowTxt,
            uvidxTomorrowTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);

        backBtn = findViewById(R.id.backBtn);
        tempTomorrowTxt = findViewById(R.id.temp_tomorrow_txt);
        precipitationTomorrowTxt = findViewById(R.id.precipitation_mm_TextView);
        windTomorrowTxt = findViewById(R.id.wind_speed_tomorrow_TextView);
        uvidxTomorrowTxt = findViewById(R.id.uvidx_TextView);
        icon = findViewById(R.id.icon_tomorrow_img);

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }

        setWeatherTomorrow();
        initRecyclerView();
        setVariable();
    }

    private void setVariable() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FutureActivity.this, MainActivity.class));
            }
        });
    }

    //INIT RECYCLER VIEW
    private void initRecyclerView() {
        ArrayList<FutureDomain> items = getFutureArrayList();

        recyclerView = findViewById(R.id.view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapterTomorrow = new FutureAdapter(items);
        recyclerView.setAdapter(adapterTomorrow);
    }

    private void setWeatherTomorrow(){
        String weatherData = readFromFile(WEATHER_DATA_TOMORROW);

        if (weatherData != null) {
            String[] lines = weatherData.split("\n");

            for (String line : lines) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    setWeatherTomorrowData(value, key);
                }
            }
        }else {
            Log.d("FutureActivity_Tomorrow", "No weather data found");
        }
    }

    private void setWeatherTomorrowData(String value, String key){
        if (key.equals("tempTomorrow")) {
            tempTomorrowTxt.setText(value + "°");
        }else if(key.equals("precipitationTomorrow")){
            precipitationTomorrowTxt.setText(value + "mm");
        }else if(key.equals("windTomorrow")){
            windTomorrowTxt.setText(value + "m/s");
        }else if(key.equals("uvidxTomorrow")){
            uvidxTomorrowTxt.setText(value);
        }else if(key.equals("iconTomorrow")){
            statusTomorrowTxt = findViewById(R.id.weather_status_tomorrow_txt);
            statusTomorrowTxt.setText(getIconIdxDescription(value, "description"));
            String iconName = getIconIdxDescription(value, "icon");
            int drawableId = getResources().getIdentifier(iconName, "drawable", getPackageName());
            if (drawableId != 0) {  // Проверяем, что ресурс найден
                // Устанавливаем Drawable на ImageView
                icon.setImageResource(drawableId);
            } else {
                Log.e("MainActivity", "Drawable not found");
            }
        }
    }



    private ArrayList<FutureDomain> getFutureArrayList(){
        ArrayList<FutureDomain> items = new ArrayList<>();

        String weatherData = readFromFile(WEATHER_DATA_FUTURE);

        if (weatherData == null || weatherData.isEmpty()) {
            Log.d("FutureActivityNull", "No weather data for 24H found");
            return items;
        }

        if (weatherData != null) {
            String[] lines = weatherData.split("\n");
            for(String line : lines){
                String[] parts = line.split("; ");
                //Log.d("Future", parts[0]+""); //415
                if(parts.length == 5){
                    String day = parts[1];
                    double tempMax = Double.parseDouble(parts[2]);
                    double tempMin = Double.parseDouble(parts[3]);
                    String iconNum = parts[4];
                    String icon = getIconIdxDescription(iconNum, "icon");
                    String status = getIconIdxDescription(iconNum, "description");

                    //Log.d("FutureActivity", "Pic: " + icon +" Status: "+ status +"IconNum: " + iconNum);

                    items.add(new FutureDomain(day, icon, status, tempMax, tempMin));
                }else{
                    Log.d("FutureActivity", "No weather data found");
                }
            }
        }
        if (weatherData == null || weatherData.isEmpty()) {
            Log.d("FutureActivity", "No weather data for 24H found");
            return items;
        }
        return items;
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
                    return icon[returnData];  // Возвращаем иконку
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

}
