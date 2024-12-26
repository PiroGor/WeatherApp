package com.nudha.weatherapp.notifications;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nudha.weatherapp.R;
import com.nudha.weatherapp.data.SetWeather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DailyNotificationWorker extends Worker {
    String icon;
    private final Context context;

    public DailyNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotification(icon, "Tomorrow", getWeatherTomorrow());
        return Result.success();
    }

    private String getWeatherTomorrow() {
        SetWeather.setWeather(getApplicationContext());
        String weatherTomorrow = SetWeather.getTomorrowData();
        icon = weatherTomorrow.split(", ")[2];
        return "Max: " + weatherTomorrow.split(", ")[0] + "°C Min: " + weatherTomorrow.split(", ")[1]+"°C";
    }

    public void showNotification(String iconIdx, String title, String message) {
        String iconName = getIconIdxDescription(iconIdx, "icon");
        int iconResId = getIconDrawableId(iconName);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "DAILY_NOTIFICATION_CHANNEL")
                .setSmallIcon(iconResId != 0 ? iconResId : R.drawable.cearly_sky_night)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private int getIconDrawableId(String iconName) {
        return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
    }

    public String getIconIdxDescription(String iconIdx, String returnDataType) {
        if (returnDataType.equals("icon")) {
            return readFromWeatherStatusIconFile(1, iconIdx);
        } else if (returnDataType.equals("description")) {
            return readFromWeatherStatusIconFile(2, iconIdx);
        } else {
            return "0";
        }
    }

    private String readFromWeatherStatusIconFile(int returnData, String iconIdx) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.weather_status_icons);

        String iconPath = readFromFileInputStreamType(inputStream);

        if (iconPath != null && !iconPath.isEmpty()) {
            String[] parts = iconPath.split("\n");

            for (String part : parts) {
                String[] icon = part.split("; ");
                Double iconDouble = Double.parseDouble(iconIdx);
                int iconInt = iconDouble.intValue();
                String iconStr = iconInt + "";

                if (iconStr.equals(icon[0])) {
                    return icon[returnData];
                }
            }
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
            Log.e("DailyNotificationWorker", "Error reading file", e);
        }

        return stringBuilder.toString();
    }
}
