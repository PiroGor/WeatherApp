package com.nudha.weatherapp.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.nudha.weatherapp.R;

public class InfoActivity extends AppCompatActivity {
    ImageView backBtn;
    ImageButton donateBtn;
    TextView infoText;
    String cardNumber = "5555 5555 5555 4444";

    private String text = "Thank you for installing Cozy Weather App! ♡\n" +
            "\n" +
            "   Your comfort is our priority. To receive accurate weather forecasts for your area, please allow access to your geolocation. In addition, we will send daily reminders at 8 pm, so please activate the alerts to stay informed about important weather changes.\n" +
            "\n" +
            "   Regular updates ensure that the data is up-to-date and the app's features are improved, so don't forget to download them. If you like our service, we'd appreciate your support - leave a rating and review in the store. This will help us make the app even better!\n" +
            "\n" +
            "Thank you in advance for your trust!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        backBtn = findViewById(R.id.backBtn_img);
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

        infoText.setText(text);

        donateBtn.setOnClickListener(v ->{
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("Card Number For Donate", cardNumber);

            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);

                Toast.makeText(this, "Card number copied!", Toast.LENGTH_SHORT).show();

            }

        });
    }
}