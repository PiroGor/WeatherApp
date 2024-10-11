
package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nudha.weatherapp.Adapters.FutureAdapter;
import com.nudha.weatherapp.Domains.FutureDomain;
import com.nudha.weatherapp.R;

import java.util.ArrayList;

public class FutureActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterTomorrow;
    public RecyclerView recyclerView;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);

        backBtn = findViewById(R.id.backBtn);

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }

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

    private void initRecyclerView() {
        ArrayList<FutureDomain> items = new ArrayList<>();

        items.add(new FutureDomain("Sat","storm","Storm",21,8));
        items.add(new FutureDomain("Sun","cloudy","Cloudy",25,10));
        items.add(new FutureDomain("Mon","windy","Windy",24,9));
        items.add(new FutureDomain("Tue","cloudy_sunny","Cloudy Sunny",25,12));
        items.add(new FutureDomain("Wed","sunny","Sunny",27,13));
        items.add(new FutureDomain("Thu","rainy","Rainy",22,10));
        items.add(new FutureDomain("Fri","rainy","Rainy",21,8));

        recyclerView = findViewById(R.id.view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapterTomorrow = new FutureAdapter(items);
        recyclerView.setAdapter(adapterTomorrow);
    }


}
