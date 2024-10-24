package com.nudha.weatherapp.API.Meteomatics.responce;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReadOrWriteTextFile {

//    public static void updateLast5Records(Context context, String newData, String fileName) throws IOException {
//        File file = new File(context.getFilesDir(), fileName);
//        List<String> fileContent = new ArrayList<>();
//        BufferedReader reader = null;
//
//        try {
//            if (file.exists()) {
//                reader = new BufferedReader(new FileReader(file));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    fileContent.add(line); // Добавляем каждую строку в список
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        String[] newRecords = newData.split("\n");
//        int start = Math.max(0, fileContent.size() - 5); // Начинаем с последних 5 записей
//        List<String> last5Records = new ArrayList<>(fileContent.subList(start, fileContent.size()));
//        List<String> updatedRecords = new ArrayList<>(last5Records); // Список для обновлённых записей
//
//        // Сравниваем и обновляем последние 5 записей файла
//        for (String newRecord : newRecords) {
//            String newDate = newRecord.split(";")[0].trim();
//            boolean isUpdated = false;
//
//            for (int i = 0; i < updatedRecords.size(); i++) {
//                String existingDate = updatedRecords.get(i).split(";")[0].trim();
//                if (existingDate.equals(newDate)) {
//                    updatedRecords.set(i, newRecord); // Обновляем строку, если даты совпадают
//                    isUpdated = true;
//                    break;
//                }
//            }
//
//            if (!isUpdated) {
//                // Добавляем новую запись только если у нас меньше 5 строк
//                if (updatedRecords.size() < 5) {
//                    updatedRecords.add(newRecord);
//                }
//            }
//        }
//
//        // Обновляем файл с учётом ограничений
//        for (int i = 0; i < 5; i++) {
//            if (i < updatedRecords.size()) {
//                if (start + i < fileContent.size()) {
//                    fileContent.set(start + i, updatedRecords.get(i)); // Обновляем существующую строку
//                } else {
//                    fileContent.add(updatedRecords.get(i)); // Добавляем строку, если она новая
//                }
//            } else {
//                break; // Прерываем, если больше нет обновлённых записей
//            }
//        }
//
//        // Перезаписываем файл
//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new FileWriter(file, false));  // Перезапись файла
//            for (String line : fileContent) {
//                writer.write(line);
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


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
}
