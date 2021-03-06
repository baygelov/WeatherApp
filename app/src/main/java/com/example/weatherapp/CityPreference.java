package com.example.weatherapp;

import android.app.Activity;
import android.content.SharedPreferences;


public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return prefs.getString("city", "Sterlitamak");
    }

    public void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }

}
