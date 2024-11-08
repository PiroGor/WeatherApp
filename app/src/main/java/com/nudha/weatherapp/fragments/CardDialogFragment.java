package com.nudha.weatherapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nudha.weatherapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class CardDialogFragment extends DialogFragment {

    private TextView cardText;
    private boolean isFlipped = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_dialog, container, false);

        cardText = view.findViewById(R.id.card_text);

        view.findViewById(R.id.card_layout).setOnClickListener(v -> flipCard());

        view.setOnClickListener(v -> dismiss()); // Закрыть при нажатии за границу карточки

        return view;
    }

    private void flipCard() {
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_in);
        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_out);

        flipOut.setTarget(cardText);
        flipIn.setTarget(cardText);

        flipOut.start();
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Random random = new Random();
                cardText.setText(isFlipped ? "Show today advice" : readFromWeatherTipsFile(random.nextInt(100)));
                isFlipped = !isFlipped;
                flipIn.start();
            }
        });
    }

    private String readFromWeatherTipsFile(int returnData){
        // Получаем InputStream для файла
        InputStream inputStream = getResources().openRawResource(R.raw.weather_tips);

        // Читаем содержимое файла
        String iconPath = readFromFileInputStreamType(inputStream);
        String numOfTip = returnData + "";

        if (iconPath != null && !iconPath.isEmpty()) {
            String[] parts = iconPath.split("\n");

            for (String part : parts) {
                String[] tip = part.split("; ");
                if(numOfTip.equals(tip[0])){
                    return tip[1];

                }
            }

            Log.d("Fragment", "No tips data found for the given number");
            Log.d("Fragment", "Status: " + returnData);
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