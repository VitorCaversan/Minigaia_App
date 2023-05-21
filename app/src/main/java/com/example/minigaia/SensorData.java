package com.example.minigaia;

public class SensorData {
    private double ph;
    private double desiredPh;
    private double temperature;
    private double humidity;
    private double waterLvl;
    private String date;

    public SensorData(double ph, double desiredPh, double temperature, double waterLvl, double humidity)
    {
        this.ph          = ph;
        this.desiredPh   = desiredPh;
        this.temperature = temperature;
        this.waterLvl    = waterLvl;
        this.humidity    = humidity;

        // A HIGHER API LEVEL IS NEEDED TO USE THESE FUNCTIONS (26+)
//            LocalDate currentDate = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            String dateString = currentDate.format(formatter);
        this.date = "10/06/2000";
    }

    public String getValuesAsString()
    {
        return (Double.toString(this.desiredPh) + this.date);
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

    public String getDate() {
        return date;
    }
}
