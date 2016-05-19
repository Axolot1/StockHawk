package com.sam_chordas.android.stockhawk.mvp;

import android.util.Log;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sam_chordas.android.stockhawk.MyApp;
import com.sam_chordas.android.stockhawk.retrofit.bean.ChartDataResult;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by axolotl on 16/5/19.
 */
public class StockDetailPresenter extends MvpBasePresenter<StockDetailView> {

    //http://stackoverflow.com/questions/34421851/retrofit-how-to-parse-this-response
    private static final Pattern JSONP = Pattern.compile("(?s)\\w+\\((.*)\\).*");

    public static String jsonpToJson(String jsonStr) {
        Matcher matcher = JSONP.matcher(jsonStr);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Unknown jsonp format");
        }
    }


    public void loadTodayStockDetail(final boolean pullToRefresh, String symbol){
        MyApp.getMyApp().getStockApi().getTodayPrice(symbol).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && isViewAttached()){
//                    getView().setData(response.body());
//                    getView().showContent();

                    try {
                        String jsonString = jsonpToJson(response.body().string());
                        Gson gson = new Gson();
                        ChartDataResult chartDataResult = gson.fromJson(jsonString, ChartDataResult.class);
                        getView().setData(chartDataResult);
                        getView().showContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isViewAttached()) {
                    getView().showError(t, pullToRefresh);
                }
                Log.i("mosby", t.getMessage());
            }
        });
    }
}
