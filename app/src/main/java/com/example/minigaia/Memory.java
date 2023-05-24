package com.example.minigaia;

import android.content.Context;
import android.content.SharedPreferences;

public class Memory
{
    private SharedPreferences sharedPreferences;

    public Memory(Context context)
    {
        this.sharedPreferences = context.getSharedPreferences("storedPrefs", Context.MODE_PRIVATE);
    }

    /**
     * Saves data in a preference file with the name of storedPrefs
     *
     * @param sensorData A SensorData class
     * @param context    The context where the preference will be stored
     */
    public void saveData(SensorData sensorData, Context context)
    {
        sharedPreferences = context.getSharedPreferences("storedPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("pH", sensorData.getPh());
        editor.putString("desiredPh", sensorData.getDesiredPh());
        editor.putString("humidity", sensorData.getHumidity());
        editor.putString("temperature", sensorData.getTemperature());
        editor.putString("waterLvl", sensorData.getWaterLvl());
        editor.putString("earlyMeasureTime", sensorData.getEarlyMeasureTime());

        editor.apply();
    }

    /**
     * Loads data from the storedPrefs file and, if all values were found, puts them in a a new
     * SensorData class
     *
     * @return new SensorData class, filled with "0" if an error occurred 
     */
    public SensorData loadData()
    {
        SensorData sensorData = new SensorData("0","0","0","0","0");
        boolean    loadingWorked = true;
        try
        {
            String valueString = sharedPreferences.getString("pH", "");
            if ("" != valueString)
            {
                sensorData.setPh(valueString);
            }
            else
            {
                loadingWorked = false;
            }

            valueString = sharedPreferences.getString("desiredPh", "");
            if ("" != valueString)
            {
                sensorData.setDesiredPh(valueString);
            }
            else
            {
                loadingWorked = false;
            }

            valueString = sharedPreferences.getString("humidity", "");
            if ("" != valueString)
            {
                sensorData.setHumidity(valueString);
            }
            else
            {
                loadingWorked = false;
            }

            valueString = sharedPreferences.getString("temperature", "");
            if ("" != valueString)
            {
                sensorData.setTemperature(valueString);
            }
            else
            {
                loadingWorked = false;
            }

            valueString = sharedPreferences.getString("waterLvl", "");
            if ("" != valueString)
            {
                sensorData.setWaterLvl(valueString);
            }
            else
            {
                loadingWorked = false;
            }

            valueString = sharedPreferences.getString("earlyMeasureTime", "");
            if ("" != valueString)
            {
                sensorData.setEarlyMeasureTime(valueString);
            }
            else
            {
                loadingWorked = false;
            }
        }
        catch (ClassCastException cce)
        {
            cce.printStackTrace();
        }

        if (true == loadingWorked)
        {
            return sensorData;
        }
        else
        {
            return new SensorData("0","0","0","0","0");
        }
    }
}
