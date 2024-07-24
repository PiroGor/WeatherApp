package com.nudha.weatherapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nudha.weatherapp.Adapters.FutureAdapter;
import com.nudha.weatherapp.Domains.FutureDomain;
import com.nudha.weatherapp.R;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity {
    private ImageView backBtn;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterTomorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        backBtn = findViewById(R.id.backBtn_Archive);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(ArchiveActivity.this, MainActivity.class));
        });

        initRecyclerView();

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