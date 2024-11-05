package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nudha.weatherapp.Adapters.FutureAdapter;
import com.nudha.weatherapp.Domains.FutureDomain;
import com.nudha.weatherapp.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity {
    private ImageView backBtn;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterTomorrow;
    private final String WEATHER_DATA_ARCHIVE = "weather_data_future.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        backBtn = findViewById(R.id.backBtn_Archive);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(ArchiveActivity.this, MainActivity.class));
        });
        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }

        initRecyclerView();

    }

    private void initRecyclerView() {
        ArrayList<FutureDomain> items = getArchiveArrayList();

        recyclerView = findViewById(R.id.view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapterTomorrow = new FutureAdapter(items);
        recyclerView.setAdapter(adapterTomorrow);
    }

    private ArrayList<FutureDomain> getArchiveArrayList() {
        ArrayList<FutureDomain> items = new ArrayList<>();

        String weatherData = readFromFile(WEATHER_DATA_ARCHIVE);

        if (weatherData != null) {
            String[] lines = weatherData.split("\n");

            for (String line : lines) {
                String[] parts = line.split("; ");

                String day = parts[1];
                double tempMax = Double.parseDouble(parts[2]);
                double tempMin = Double.parseDouble(parts[3]);
                String iconNum = parts[4];
                String icon = getIconIdxDescription(iconNum, "icon");
                String status = getIconIdxDescription(iconNum, "description");

                items.add(new FutureDomain(day, icon, status, tempMax, tempMin));
            }
        }
        if (weatherData == null || weatherData.isEmpty()) {
            Log.d("ArchiveActivity", "No weather data for 24H found");
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