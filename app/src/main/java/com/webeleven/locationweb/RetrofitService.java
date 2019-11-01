package com.webeleven.locationweb;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("https://quasar-test-two.free.beeceptor.com")
    Call<JSONObject> sendLocation(@Field("lat") String lat, @Field("log") String log, @Field("time") String time);

}
