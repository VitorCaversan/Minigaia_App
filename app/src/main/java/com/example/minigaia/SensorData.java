package com.example.minigaia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SensorData {
    private double ph;
    private double desiredPh;
    private double temperature;
    private double humidity;
    private double waterLvl;
    private String measureTime;
    private String date;

    public SensorData(double ph, double desiredPh, double temperature, double waterLvl, double humidity)
    {
        this.ph          = ph;
        this.desiredPh   = desiredPh;
        this.temperature = temperature;
        this.waterLvl    = waterLvl;
        this.humidity    = humidity;
        this.measureTime = "07:00";

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

    public double getPh() {
        return ph;
    }
    public void   setPh(double ph) {this.ph = ph;}

    public double getDesiredPh() {
        return desiredPh;
    }
    public void   setDesiredPh(double desiredPh) {this.desiredPh = desiredPh;}

    public double getTemperature() {
        return temperature;
    }
    public void   setTemperature(double temperature) {this.temperature = temperature;}

    public double getWaterLvl()
    {
        return this.waterLvl;
    }
    public void   setWaterLvl(double waterLvl) {this.waterLvl = waterLvl;}

    public double getHumidity()
    {
        return this.humidity;
    }
    public void   setHumidity(double humidity) {this.humidity = humidity;}

    public String getMeasureTime()
    {
        return measureTime;
    }
    public void setMeasureTime(String measureTime)
    {
        this.measureTime = measureTime;
    }
    public String getDate() {
        return date;
    }
}
