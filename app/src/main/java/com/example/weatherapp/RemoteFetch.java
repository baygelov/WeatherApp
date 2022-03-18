package com.example.weatherapp;


import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import retrofit2.Retrofit;

public class RemoteFetch {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String APP_ID = "2b5125d9ef5b1f5168217e9d0f4f37bf";

    public static JSONObject getJSON(Context context, String city) throws IOException {

        HttpURLConnection connection =
                (HttpURLConnection) (new URL(BASE_URL + city + "&APPID" + APP_ID)).openConnection();
        try {
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

        } catch (ProtocolException protocolE) {
            protocolE.printStackTrace();
        }

        StringBuilder sbJson = new StringBuilder();
        InputStream inpStr = connection.getInputStream();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inpStr));
        String strJson;
        while ((strJson = reader.readLine()) != null) {

            sbJson.append(strJson);

        }
        reader.close();

        JSONObject jData = null;
        try {
            jData = new JSONObject(sbJson.toString());
        }
        catch (JSONException jsoneE) {
            jsoneE.printStackTrace();
        }

        return jData;
    }
}