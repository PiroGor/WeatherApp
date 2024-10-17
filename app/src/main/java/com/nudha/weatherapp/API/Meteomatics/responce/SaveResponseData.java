package com.nudha.weatherapp.API.Meteomatics.responce;

import android.content.Context;
import android.util.Log;

import com.nudha.weatherapp.API.Meteomatics.request.WeatherResponse;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.PrecipitationPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.TempPartRequest;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.WindSpeedPartRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SaveResponseData {
    private static final String WEATHER_24H = "weather_data_24H.txt";
    private static final String WEATHER_NOW = "weather_data.txt";

    //Save response data to
    public static void setWeather24h(Context context, WeatherResponse weatherResponse) throws IOException {
        try{
            saveToFileFor24H(context, collectWeatherDataFor24H(weatherResponse));
        }catch (IOException e){
            Log.e("SaveResponceData", "Error: " + e.getMessage());
        }
    }

    public static void setWeatherNow(Context context, WeatherResponse weatherResponse) throws IOException {
        try{
            saveToFile(context, collectWeatherData(weatherResponse));
        }catch (IOException e){
            Log.e("SaveResponceData", "Error: " + e.getMessage());
        }
    }

    // сохранение в файл данных погоды на 24 часа
    private static void saveToFileFor24H(Context context, String data) throws IOException {
        FileOutputStream fos = context.openFileOutput(WEATHER_24H, Context.MODE_PRIVATE);
        fos.write(data.getBytes());
        fos.close();
        //Log.d("SplashScreenActivity", "24H Data saved to file");
    }

    private static void saveToFile(Context context, String data) throws IOException {
        FileOutputStream fos = context.openFileOutput(WEATHER_NOW, Context.MODE_PRIVATE);
        fos.write(data.getBytes());
        fos.close();
        // Log.d("SplashScreenActivity", "Data saved to file");
    }

    // сбор и форматирование данных для сохранения в файл на 24 часа
    private static String collectWeatherDataFor24H(WeatherResponse weatherResponse) {
        StringBuilder data = new StringBuilder();

        // Получение данных температуры (t_2m:C) и иконок (weather_symbol_1h:idx)
        WeatherResponse.Data tempData = findParameter(weatherResponse.getData(), "t_2m:C");
        WeatherResponse.Data iconData = findParameter(weatherResponse.getData(), "weather_symbol_1h:idx");

        if (tempData != null && iconData != null) {
            List<WeatherResponse.Data.Coordinate.DateValue> tempDates = tempData.getCoordinates().get(0).getDates();
            List<WeatherResponse.Data.Coordinate.DateValue> iconDates = iconData.getCoordinates().get(0).getDates();

            // Проходим по данным и собираем информацию за 24 часа
            for (int i = 0; i < tempDates.size() && i < iconDates.size(); i++) {
                String time = tempDates.get(i).getDate();
                double temperature = tempDates.get(i).getValue();
                int icon = (int) iconDates.get(i).getValue();

                // Форматирование строки: Время: Температура: Иконка
                data.append(String.format("%s; %.1f; %d\n", time, temperature, icon));
            }
        }

        return data.toString();
    }

    // Форматирование данных для записи в файл
    private static String collectWeatherData(WeatherResponse weatherResponse) {
        StringBuilder data = new StringBuilder();
        data.append("tempNow: ")
                .append(setData(weatherResponse, TempPartRequest.getTemp(), "tempNow"))
                .append("\nhighTemp: ")
                .append(setData(weatherResponse, TempPartRequest.getTempStats("max24H"), "highTemp"))
                .append("\nlowTemp: ")
                .append(setData(weatherResponse, TempPartRequest.getTempStats("min24H"), "lowTemp"))
                .append("\npercipitation_now: ")
                .append(setData(weatherResponse, PrecipitationPartRequest.getPrecipitationPart("1h"), "percipitation_now"))
                .append("\nwind_speed: ")
                .append(setData(weatherResponse, WindSpeedPartRequest.getWindSpeedPart(), "wind_speed"))
                .append("\nuvIndx: ")
                .append(setData(weatherResponse, "uv:idx", "uvIndx"))
                .append("\niconNow: ")
                .append(setData(weatherResponse, "weather_symbol_1h:idx", "iconNow"));
        return data.toString();
    }

    private static String setData(WeatherResponse weatherResponse, String parameter, String paramName) {
        // Получаем данные из ответа
        WeatherResponse.Data data = findParameter(weatherResponse.getData(), parameter);
        WeatherResponse.Data.Coordinate coordinate = data.getCoordinates().get(0);
        WeatherResponse.Data.Coordinate.DateValue dateValue = coordinate.getDates().get(0);
        return String.valueOf(dateValue.getValue());
    }

    private static WeatherResponse.Data findParameter(List<WeatherResponse.Data> data, String parameter) {
        for (WeatherResponse.Data datum : data) {
            if (datum.getParameter().equals(parameter)) {
                return datum;
            }
        }
        return null;
    }
}
