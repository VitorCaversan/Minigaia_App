package com.example.minigaia;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorData {
    private String ph;
    private String desiredPh;
    private String temperature;
    private String humidity;
    private String waterLvl;
    private String earlyMeasureTime;

    static final long HOUR_IN_SEC = 3600;
    static final long MIN_IN_SEC  = 60;

    public SensorData(String ph, String desiredPh, String temperature, String waterLvl, String humidity)
    {
        this.ph          = ph;
        this.desiredPh   = desiredPh;
        this.temperature = temperature;
        this.waterLvl    = waterLvl;
        this.humidity    = humidity;
        this.earlyMeasureTime = "07:00";
    }

    public String getPh() {
        return ph;
    }
    public void   setPh(String ph) {this.ph = ph;}

    public String getDesiredPh() {
        return desiredPh;
    }
    public void   setDesiredPh(String desiredPh) {this.desiredPh = desiredPh;}

    public String getTemperature() {
        return temperature;
    }
    public void   setTemperature(String temperature) {this.temperature = temperature;}

    public String getWaterLvl()
    {
        return this.waterLvl;
    }
    public void   setWaterLvl(String waterLvl) {this.waterLvl = waterLvl;}

    public String getHumidity()
    {
        return this.humidity;
    }
    public void   setHumidity(String humidity) {this.humidity = humidity;}

    public String getEarlyMeasureTime()
    {
        return earlyMeasureTime;
    }
    public void setEarlyMeasureTime(String earlyMeasureTime)
    {
        this.earlyMeasureTime = earlyMeasureTime;
    }

    public JSONObject toJson(Boolean measureNow) throws JSONException {
        measureNow = (measureNow == null) ? false : measureNow;

        JSONObject json = new JSONObject();

        json.put("time", System.currentTimeMillis()/1000);
        json.put("target_pH", this.desiredPh);

        String measureTime = this.earlyMeasureTime;
        int hour = Integer.parseInt(measureTime.substring(0,2));
        int min  = Integer.parseInt(measureTime.substring(3,measureTime.length()));
        long schedTimeInSec = (hour * HOUR_IN_SEC) + (min + MIN_IN_SEC);
        json.put("schedule", schedTimeInSec);
        
        json.put("measureNow", measureNow);

        return json;
    }

    public void updateValues(JSONObject jsonValues) throws JSONException
    {
        int maxLength = 5;
        String input  = jsonValues.getString("ph");
        this.ph          = (input.length() > maxLength) ? input.substring(0, maxLength) : input;

        input = jsonValues.getString("temperature");
        this.temperature = (input.length() > maxLength) ? input.substring(0, maxLength) : input;

        input = jsonValues.getString("humidity");
        this.humidity    = (input.length() > maxLength) ? input.substring(0, maxLength) : input;

        input = jsonValues.getString("water_volume");
        this.waterLvl    = (input.length() > maxLength) ? input.substring(0, maxLength) : input;
    }
}
