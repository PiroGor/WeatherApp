package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.nudha.weatherapp.R;

public class InfoActivity extends AppCompatActivity {
    ImageView backBtn;
    ImageButton rateBtn, donateBtn;
    TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        backBtn = findViewById(R.id.backBtn_img);
        rateBtn = findViewById(R.id.rateBtn);
        donateBtn = findViewById(R.id.donateBtn);
        infoText = findViewById(R.id.instruction_textView);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(InfoActivity.this, MainActivity.class));
        });

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }

        //Write code for RATEBTN and DONATEBTN


    }
}