package com.nudha.weatherapp.data;

import android.content.Context;
import android.util.Log;

import com.nudha.weatherapp.api.meteomatics.request.WeatherResponse;
import com.nudha.weatherapp.api.meteomatics.requestCreator.PrecipitationPartRequest;
import com.nudha.weatherapp.api.meteomatics.requestCreator.TempPartRequest;
import com.nudha.weatherapp.api.meteomatics.requestCreator.WindSpeedPartRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SaveResponseData {
    private static final String WEATHER_24H = "weather_data_24H.txt";
    private static final String WEATHER_NOW = "weather_data.txt";
    private static final String WEATHER_FUTURE = "weather_data_future.txt";
    private static final String WEATHER_TOMORROW = "weather_data_tomorrow.txt";

    public static void setWeather(Context context, WeatherResponse weatherResponse, String typeToSave) throws IOException {
        try{
            if(typeToSave.equals("24H")){
                saveToFile(context, collectWeatherDataFor24H(weatherResponse), WEATHER_24H);
            }else if(typeToSave.equals("now")){
                saveToFile(context, collectWeatherData(weatherResponse), WEATHER_NOW);
            } else if (typeToSave.equals("future")) {
                //Log.d("SaveResponseData: "+ typeToSave, "Data: " + collectWeatherDataFuture5Days(weatherResponse));
                ReadOrWriteTextFile.updateLast5Records(context, collectWeatherDataFuture5Days(weatherResponse), WEATHER_FUTURE);
                ReadOrWriteTextFile.sortFileByDate(context, WEATHER_FUTURE);
            }else if(typeToSave.equals("tomorrow")){
                Log.d("SaveResponseData: "+ typeToSave, "Data: " + collectWeatherDataTomorrow(weatherResponse));
                saveToFile(context, collectWeatherDataTomorrow(weatherResponse), WEATHER_TOMORROW);
            }
        }catch (IOException e){
            Log.e("SaveResponseData: "+ typeToSave, "Error: " + e.getMessage());
        }
    }

    private static void saveToFile(Context context, String data, String fileName) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(data.getBytes());
        fos.close();
        // Log.d("SplashScreenActivity", "Data saved to file");
    }

    //https://api.meteomatics.com/2024-10-23ZP5D:PT24H/t_max_2m_24h:C,t_min_2m_24h:C,weather_symbol_1h:idx/50,10/json
    private static String collectWeatherDataFuture5Days(WeatherResponse weatherResponse) {
        StringBuilder data = new StringBuilder();

        // Получаем данные из ответа: дата, температута макс и мин, иконка
        WeatherResponse.Data tempMaxData = findParameter(weatherResponse.getData(), "t_max_2m_24h:C");
        WeatherResponse.Data tempMinData = findParameter(weatherResponse.getData(), "t_min_2m_24h:C");
        WeatherResponse.Data iconData = findParameter(weatherResponse.getData(), "weather_symbol_1h:idx");

        if(tempMaxData != null && tempMinData != null && iconData != null){
            List<WeatherResponse.Data.Coordinate.DateValue> tempMaxDates = tempMaxData.getCoordinates().get(0).getDates();
            List<WeatherResponse.Data.Coordinate.DateValue> tempMinDates = tempMinData.getCoordinates().get(0).getDates();
            List<WeatherResponse.Data.Coordinate.DateValue> iconDates = iconData.getCoordinates().get(0).getDates();

            // Проходим по данным и собираем информацию за 24 часа
            for (int i = 0; i < tempMaxDates.size() && i < tempMinDates.size() && i < iconDates.size(); i++) {
                double tempMax = tempMaxDates.get(i).getValue();
                double tempMin = tempMinDates.get(i).getValue();
                int icon = (int) iconDates.get(i).getValue();
                String date = tempMaxDates.get(i).getDate();
                String dayOfWeek = getDayOfWeek(date);

                // Форматирование строки: Дата: День недели: Температура макс: Температура мин: Иконка
                data.append(String.format("%s; %s; %.1f; %.1f; %d\n",date, dayOfWeek, tempMax, tempMin, icon));
            }

        }
        Log.d("SaveResponseData: future", "Data: " + data.toString());
        return data.toString();
    }

    private static String getDayOfWeek(String dateTime) {
        // Преобразование строки в объект LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String inputDate = dateTime.substring(0, 10) ;

        LocalDate date = LocalDate.parse(inputDate, formatter);

        // Получение дня недели
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Преобразование дня недели в сокращенную форму (Mon, Tue, ...)
        return dayOfWeek.name().substring(0, 3).toUpperCase(); //.toUpperCase

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

    private static String collectWeatherDataTomorrow(WeatherResponse weatherResponse){
        StringBuilder data = new StringBuilder();
        data.append("temp: ").append(setData(weatherResponse, TempPartRequest.getTemp(), "temp"))
                .append("\nicon: ").append(setData(weatherResponse, "weather_symbol_1h:idx", "icon"))
                .append("\nwind_speed: ").append(setData(weatherResponse, WindSpeedPartRequest.getWindSpeedPart(), "wind_speed"))
                .append("\nuvIndx: ").append(setData(weatherResponse, "uv:idx", "uvIndx"))
                .append("\npercipitation: ").append(setData(weatherResponse, PrecipitationPartRequest.getPrecipitationPart("24h"), "percipitation"));
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

    public static String collectWeatherDataTomorrowNotification(WeatherResponse weatherResponse) {
        StringBuilder data = new StringBuilder();

        // Получаем данные из ответа: дата, температута макс и мин, иконка
        WeatherResponse.Data tempMaxData = findParameter(weatherResponse.getData(), "t_max_2m_24h:C");
        WeatherResponse.Data tempMinData = findParameter(weatherResponse.getData(), "t_min_2m_24h:C");
        WeatherResponse.Data iconData = findParameter(weatherResponse.getData(), "weather_symbol_1h:idx");

        if(tempMaxData != null && tempMinData != null && iconData != null){
            List<WeatherResponse.Data.Coordinate.DateValue> tempMaxDates = tempMaxData.getCoordinates().get(0).getDates();
            List<WeatherResponse.Data.Coordinate.DateValue> tempMinDates = tempMinData.getCoordinates().get(0).getDates();
            List<WeatherResponse.Data.Coordinate.DateValue> iconDates = iconData.getCoordinates().get(0).getDates();

            double tempMax = tempMaxDates.get(0).getValue();
            double tempMin = tempMinDates.get(0).getValue();
            int icon = (int) iconDates.get(0).getValue();

            // Форматирование строки: Температура макс: Температура мин: Иконка
            data.append(String.format("Max. %.1f°C, Min. %.1f°C, %d\n",tempMax, tempMin, icon));
        }
        Log.d("SaveResponseData: tomorrow", "Data: " + data.toString());
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
