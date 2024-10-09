package com.nudha.weatherapp.API.Meteomatics.request;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor("university_nudha_anastasiia", "D5H8gc2kVw"))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(UrlManager.getInstance().getBaseUrl())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }
}