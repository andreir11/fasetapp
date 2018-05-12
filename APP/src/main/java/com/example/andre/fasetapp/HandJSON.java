package com.example.andre.fasetapp;


import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HandJSON {
    private String country = "county";
    private String temperature = "temperature";
    private String humidity = "humidity";
    private String pressure = "pressure";
    private String urlString = null;
    private String dateString="";
    private String weather_type="";

    public volatile boolean parsingComplete = true;
    public HandJSON(String url,String date){
        this.urlString = url;
        this.dateString=date;
    }
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    public String getCountry(){
        return country;
    }
    public String getTemperature(){
        return temperature;
    }
    public String getHumidity(){
        return humidity;
    }
    public String getPressure(){
        return pressure;
    }
    public String getWeather_type(){return weather_type; }

    @SuppressLint("NewApi")
    public void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            //JSONObject sys  = reader.getJSONObject("sys");
            //country = sys.getString("country");

            JSONArray list = reader.getJSONArray("list");
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn="";
            int i=0;
            while(i!=list.length()) {
                JSONObject jsonObject = list.getJSONObject(i);
                updatedOn = FORMATTER.format(new Date(jsonObject.getLong("dt")*1000));
                if(updatedOn.equals(dateString))
                {   temperature= new SimpleDateFormat("HH:mm  dd MMM yy").format(new Date(jsonObject.getLong("dt")*1000));
                    JSONObject main=jsonObject.getJSONObject("main");
                    JSONObject weather=jsonObject.getJSONObject("weather");
                    weather_type=weather.getString("main");
                    double temp= Double.parseDouble(main.getString("temp_max"));
                    temp+=0;
                    temperature =String.valueOf(temp);
                    break;}
                else
                {temperature=" ";}
                i+=8;
            }   //JSONObject jsonObject = list.getJSONObject(6);
            //temperature=updatedOn+"date:"+dateString;
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