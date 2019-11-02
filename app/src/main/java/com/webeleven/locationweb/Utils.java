package com.webeleven.locationweb;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";
    private static Retrofit retrofit;
    public static String date;

    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    static String getLocationText(Location location) {
        sendToServer(location);
        return location == null ? "Sinal de GPS perdido" :
                "( Lat: " + location.getLatitude() + ", Long: " + location.getLongitude() + ")";
    }

    static String getLocationTitle(Context context) {
        return context.getString(R.string.localizacao_atualizada,
                DateFormat.getDateTimeInstance().format(new Date()));
    }

    static String setDate(String _date) {
        date = _date;
        return date;
    }



    static void sendToServer(Location location) {

        String baseUrl = "https://quasar-test-two.free.beeceptor.com";

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        intialize(location.getLatitude()+"", location.getLongitude()+"");
        Log.d("LOCATION:", retrofit.baseUrl().toString());
    }

    static void intialize(String latitude, String longitude) {

        //Treço para criar e imprimir a data em que o usuario fizer uma requisição do metodo
        Calendar currentTime = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("America/Sao_Paulo");
        currentTime.setTimeZone(tz);
        Date date = currentTime.getTime();
        String time = android.text.format.DateFormat.format("dd-MM-yyyy---hh-mm-ss", date).toString();
        Log.d("LOCATION", "DATE: " + android.text.format.DateFormat.format("dd-MM-yyyy-hh:mm:ss", date));

        setDate(android.text.format.DateFormat.format("dd/MM/yyyy  hh:mm:ss", date)+"");

        Call<JSONObject> call = new Utils().getRetrofitService().sendLocation(latitude, longitude, time);

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.d("LOCATION RESPONSE: ", response.code()+" --- Message:" + response.message());
                if (response.isSuccessful()) {
                    Log.d("LOCATION ERROR", "Sucesso na requisição: " + response.body());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.d("LOCATION:", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
            }
        });

    }

    public RetrofitService getRetrofitService() {
        return retrofit.create(RetrofitService.class);
    }

    static String getAndroid() {

        return Build.MODEL + " - SDK: " + Build.VERSION.SDK_INT;
    }

}
