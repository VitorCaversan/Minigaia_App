package com.example.minigaia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SensorData {
    private String ph;
    private String desiredPh;
    private String temperature;
    private String humidity;
    private String waterLvl;
    private String earlyMeasureTime;
    private String date;

    public SensorData(String ph, String desiredPh, String temperature, String waterLvl, String humidity)
    {
        this.ph          = ph;
        this.desiredPh   = desiredPh;
        this.temperature = temperature;
        this.waterLvl    = waterLvl;
        this.humidity    = humidity;
        this.earlyMeasureTime = "07:00";

        // A HIGH API LEVEL IS NEEDED TO USE THESE FUNCTIONS (26+)
        try {
            long currentTimeMillis = System.currentTimeMillis();
            this.date = Long.toString((currentTimeMillis / 1000));
        }
        catch (Exception e)
        {
            this.date = "10/06/2000";
        }
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

    public String getearlyMeasureTime()
    {
        return earlyMeasureTime;
    }
    public void setearlyMeasureTime(String earlyMeasureTime)
    {
        this.earlyMeasureTime = earlyMeasureTime;
    }
    public String getDate() {
        return date;
    }
}
