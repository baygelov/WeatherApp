package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class WeatherFragment extends Fragment {
    private final Context context;
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    Handler handler;

    public WeatherFragment(Context context){
        this.context = context;
        handler = new Handler();
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
         ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.frag_weather,container,false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        weatherFont = ResourcesCompat.getFont(context, R.font.weather);
        updateWeatherData(new CityPreference(requireActivity()).getCity());
    }
    private void updateWeatherData (final String city){
        new Thread(){
            public void run(){
                JSONObject json;

                try {
                    json = RemoteFetch.getJSON(getActivity(), city);
                    assert json != null;
                    handler.post(() -> renderWeather(json));

                } catch (IOException ioE) {

                    ioE.printStackTrace();
                    handler.post(() -> Toast.makeText(getActivity(),
                            requireActivity().getString(R.string.place_not_found),
                            Toast.LENGTH_LONG).show());
                }

                }
            }.start();
        }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp") - 273.15)+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch (JSONException jsonE){
            Log.e("WeatherApp", "One or more field not found in this JSON data");
        }
    }
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime < sunset){
                icon = requireActivity().getString(R.string.weather_sunny);
            }else{
                icon = requireActivity().getString(R.string.weather_clear_night);
            }
        }else{
            switch (id){
                case 2: icon = requireActivity().getString(R.string.weather_thunder);
                        break;
                case 3: icon = requireActivity().getString(R.string.weather_drizzle);
                        break;
                case 7: icon = requireActivity().getString(R.string.weather_foggy);
                        break;
                case 8: icon = requireActivity().getString(R.string.weather_cloudy);
                        break;
                case 6: icon = requireActivity().getString(R.string.weather_snowy);
                        break;
                case 5: icon = requireActivity().getString(R.string.weather_rainy);
                        break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }
}
