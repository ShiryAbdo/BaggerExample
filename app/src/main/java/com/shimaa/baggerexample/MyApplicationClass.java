package com.shimaa.baggerexample;

import android.app.Application;

import com.fatboyindustrial.gsonjodatime.DateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MyApplicationClass extends Application {
    private Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();
//                        .cache(cache)

        // --- NETWORK ---
        // Prepare timber and OkHttp's HttpLoggingInterceptor to log urls and etc
        Timber.plant(new Timber.DebugTree());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.i(message);
            }
        });

        // Prepare OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // --- PICASSO ---
        // We want Picasso to use our OkHttp3 client instead of default
        picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();

        // --- CLIENT ---
        // Initialize Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        Gson gson = gsonBuilder.create();
        // Build retrofit
        Retrofit gitHubRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient) // tell retrofit to use our OkHttpClient
                .baseUrl("https://api.github.com/")
                .build();
    }
    public Picasso getPicasso() {
        return picasso;
    }
}
