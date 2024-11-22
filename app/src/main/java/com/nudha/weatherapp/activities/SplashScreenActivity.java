package com.nudha.weatherapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import com.nudha.weatherapp.R;
import com.nudha.weatherapp.data.SetWeather;
import com.nudha.weatherapp.permissions.LocationUtils;

public class SplashScreenActivity extends AppCompatActivity {
    private LocationUtils locationUtils;
    private Context context = SplashScreenActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        locationUtils = new LocationUtils(this);
        locationUtils.requestLocation();


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
                SetWeather.setWeather(context);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Intent to Login Activity
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
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
}
