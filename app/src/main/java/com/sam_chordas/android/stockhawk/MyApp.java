package com.sam_chordas.android.stockhawk;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sam_chordas.android.stockhawk.retrofit.StockApi;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by axolotl on 16/5/14.
 */
public class MyApp extends Application {

    private StockApi mApi;
    public static MyApp myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        myApp = this;
        Gson gson = new GsonBuilder()
                .setLenient()
                .excludeFieldsWithoutExposeAnnotation().create();
        mApi = new Retrofit.Builder()
                .client(new OkHttpClient().newBuilder().addNetworkInterceptor(new StethoInterceptor()).build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(StockApi.DETAIL_URL)
                .build().create(StockApi.class);
    }

    public static MyApp getMyApp() {
        return myApp;
    }

    public StockApi getStockApi() {
        return mApi;
    }



}
