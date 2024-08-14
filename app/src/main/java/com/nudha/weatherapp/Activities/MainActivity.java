package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.nudha.weatherapp.permissions.LocationUtils;

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
    private SharedPreferences sharedPreferences;

    private SimpleDateFormat sdf = new SimpleDateFormat("HH");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

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
        if (sharedPreferences != null) {
            for (String key : sharedPreferences.getAll().keySet()) {
                String value = sharedPreferences.getString(key, null);
                if (value != null) {
                    setWeatherMain(value, key);
                }
            }
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
        //https://api.meteomatics.com/2024-08-08T16:00:00ZP1D:PT1H/t_2m:C,weather_symbol_1h:idx/50,10/json
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

    public void setDataTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd | HH:mm", Locale.ENGLISH);
        Date date = new Date();
        TextView data = findViewById(R.id.data_textView);
        data.setText(dateFormat.format(date));

    }

    public void setWeatherNow(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                sharedPreferences.edit().putString(key, value.toString()).apply();
                setWeatherMain(value.toString(), key);
            }
        }else{
            Log.d("MainActivity", "No extras found");
        }
    }

    private void setWeatherMain(String value, String key){
        if (key.equals("tempNow")) {
            tempNow.setText(value.toString());
        }else if(key.equals("highTemp")){
            highTemp.setText("H: " + value.toString());
        }else if(key.equals("lowTemp")){
            lowTemp.setText(" L: " + value.toString());
        }else if(key.equals("percipitation_now")){
            percipitation_now.setText(value.toString());
        }else if(key.equals("wind_speed")){
            wind_speed.setText(value.toString());
        }else if(key.equals("uvIndx")){
            uvIndx.setText(value.toString());
        }
    }





}