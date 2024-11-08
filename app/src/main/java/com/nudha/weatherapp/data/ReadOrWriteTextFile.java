package com.nudha.weatherapp.data;


import android.content.Context;
import android.util.Log;

import com.nudha.weatherapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReadOrWriteTextFile {

    public static void updateLast5Records(Context context, String newData, String fileName) throws IOException {
        File file = new File(context.getFilesDir(), fileName);
        List<String> fileContent = new ArrayList<>();
        BufferedReader reader = null;

        try {
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.add(line); // Добавляем каждую строку в список
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Преобразуем новые данные в список строк
        String[] newRecords = newData.split("\n");

        // Обрабатываем все записи
        for (String newRecord : newRecords) {
            String newDate = newRecord.split(";")[0].trim();
            boolean isUpdated = false;

            // Проверяем, если дата совпадает с какой-либо записью из файла
            for (int i = 0; i < fileContent.size(); i++) {
                String existingDate = fileContent.get(i).split(";")[0].trim();
                if (existingDate.equals(newDate)) {
                    fileContent.set(i, newRecord);  // Обновляем строку, если даты совпадают
                    isUpdated = true;
                    break;
                }
            }

            // Если дата не совпала ни с одной из записей, добавляем новую запись
            if (!isUpdated) {
                fileContent.add(newRecord);
            }
        }

        // Удаляем все, кроме последних 5 записей
        if (fileContent.size() > 6) {
            fileContent = fileContent.subList(fileContent.size() - 6, fileContent.size());
        }

        // Перезаписываем файл
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));  // Перезапись файла
            for (String line : fileContent) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sortFileByDate(Context context, String fileName) throws IOException {
        File file = new File(context.getFilesDir(), fileName);
        List<String> fileContent = new ArrayList<>();
        BufferedReader reader = null;

        try {
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                String line;

                // Чтение файла построчно
                while ((line = reader.readLine()) != null) {
                    fileContent.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Сортируем строки по дате
        Collections.sort(fileContent, new Comparator<String>() {
            @Override
            public int compare(String line1, String line2) {
                String date1 = line1.split(";")[0].trim();
                String date2 = line2.split(";")[0].trim();
                return date1.compareTo(date2); // Сравнение дат
            }
        });

        // Перезаписываем файл с отсортированными данными
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false)); // Перезапись файла
            for (String line : fileContent) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private String readFromFile(String fileName) {
//        // Читаем данные из файла
//        //Log.d("MainActivity", "Reading data from file");
//        StringBuilder data = new StringBuilder();
//        try (FileInputStream fis = openFileInput(fileName);
//             InputStreamReader isr = new InputStreamReader(fis);
//             BufferedReader br = new BufferedReader(isr)) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                data.append(line).append("\n");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return data.toString();
//    }

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

//    private String readFromWeatherStatusIconFile(int returnData, String iconIdx){
//        // Получаем InputStream для файла
//        InputStream inputStream = getResources().openRawResource(R.raw.weather_status_icons);
//
//        // Читаем содержимое файла
//        String iconPath = readFromFileInputStreamType(inputStream);
//
//        if (iconPath != null && !iconPath.isEmpty()) {
//            String[] parts = iconPath.split("\n");
//
//            for (String part : parts) {
//                String[] icon = part.split("; ");
//                Double iconDouble = Double.parseDouble(iconIdx);
//                int iconInt = iconDouble.intValue();
//                //Log.d("MainActivity", "Icon int: " + iconInt);
//                String iconStr = iconInt +"";
//
//                if (iconStr.equals(icon[0])) {
//                    return icon[returnData];  // Возвращаем иконку
//                }
//            }
//
//            Log.d("MainActivity", "No weather data found for the given status");
//            Log.d("MainActivity", "Status: " + returnData);
//            return "0";
//        }
//
//        return "0";
//    }
}
