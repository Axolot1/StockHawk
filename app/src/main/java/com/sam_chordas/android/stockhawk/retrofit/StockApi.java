package com.sam_chordas.android.stockhawk.retrofit;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by axolotl on 16/5/19.
 */
public interface StockApi {
    String DETAIL_URL = "http://chartapi.finance.yahoo.com/instrument/1.0/";

    @GET("{symbol}/chartdata;type=close;range=1d/json")
    Call<ResponseBody> getTodayPrice(@Path("symbol") String symbol);
}
