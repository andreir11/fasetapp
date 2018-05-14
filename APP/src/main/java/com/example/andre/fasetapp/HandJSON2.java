package com.example.andre.fasetapp;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HandJSON2 {

    private String country = "county";
    private String weathers = " ";
    private String urlString = null;
    private String dateString="";

    public volatile boolean parsingComplete = true;
    public HandJSON2(String url,String date){
        this.urlString = url;
        this.dateString=date;
    }

    public String getWeatherStyle(){
        return weathers;
    }
    @SuppressLint("NewApi")
    public void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);
            JSONArray weather=reader.getJSONArray("weather");
            JSONObject weather_style=weather.getJSONObject(0);
            weathers=weather_style.getString("main");
            parsingComplete = false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void fetchJSON(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    String data = convertStreamToString(stream);
                    readAndParseJSON(data);
                    stream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
