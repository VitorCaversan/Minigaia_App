package com.example.minigaia;

import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class WebServer
{
    static final long HOUR_IN_SEC = 3600;
    static final long MIN_IN_SEC  = 60;

    private SensorData sensorData;
    private Retrofit retrofit;

    android.content.Context mainActivityContext;

    public WebServer(android.content.Context context)
    {
        //Setting base URL
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.15.99") // Your ESP32 IP address
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.mainActivityContext = context;

        this.sensorData = new SensorData("0", "0", "0", "0", "0");
    }

    public interface ESP32Service {
        @GET("/")
        Call<ResponseBody> getValue();
        @GET("/led")
        Call<ResponseBody> toggleLED();
        @GET("/sync")
        Call<ResponseBody> getMeasurements();
        @FormUrlEncoded
        @POST("/time")
        Call<ResponseBody> setTime(@Field("time") String time);

        @FormUrlEncoded
        @POST("/ESPget")
        Call<ResponseBody> sendData(@Field("time")          String time,
                                    @Field("target_pH")     String targetPh,
                                    @Field("scheduledTime") String measureTime,
                                    @Field("measureNow")    String measureNow);
    }

    /**
     * The base to request and receive data from a web server
     */
    public SensorData updateSensorData(SensorData mainSensData) {
        ESP32Service service = retrofit.create(ESP32Service.class);
        Call<ResponseBody> call = service.getMeasurements();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                {
                    try {
                        String responseString = response.body().string();

                        sensorData = parseResponseString(responseString, mainSensData.getDesiredPh());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(mainActivityContext, "Data updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mainActivityContext, "Error updating data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mainActivityContext, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return sensorData;
    }

    /**
     * The base to data to a web server
     *
     * @param sensorData Sensor data to be used and later return the correct values
     */
    public void sendSyncData(SensorData sensorData, boolean measureNow)
    {
        long currentTimeMillis = System.currentTimeMillis();

        String measureTime = sensorData.getearlyMeasureTime();
        int hour = Integer.parseInt(measureTime.substring(0,2));
        int min  = Integer.parseInt(measureTime.substring(3,measureTime.length()));

        long schedTimeInSec = (hour * HOUR_IN_SEC) + (min + MIN_IN_SEC);

        // Create the service
        ESP32Service service = retrofit.create(ESP32Service.class);

        // Makes the call
        Call<ResponseBody> call = service.sendData(Long.toString(currentTimeMillis/1000),
                                                   sensorData.getDesiredPh(),
                                                   Long.toString(schedTimeInSec),
                                                   Boolean.toString(measureNow));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(mainActivityContext, "Data sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mainActivityContext, "Error sending data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mainActivityContext, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public SensorData parseResponseString(String responseString, String desiredPh)
    {
        SensorData sensorData = new SensorData("0", desiredPh,"0","0","0");

        try
        {
            sensorData.setPh(responseString.substring(0,5));
            sensorData.setTemperature(responseString.substring(10,15));
            sensorData.setWaterLvl(responseString.substring(15,20));
            sensorData.setHumidity(responseString.substring(5,10));
        }
        catch (Exception e)
        {
            Toast.makeText(mainActivityContext, "Sync failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return sensorData;
    }

    public void toggleLED() {
        ESP32Service service = retrofit.create(ESP32Service.class);
        Call<ResponseBody> call = service.toggleLED();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Handle the response here
                if(response.isSuccessful()) {
                    Toast.makeText(mainActivityContext, "LED state toggled", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the error here
                    Toast.makeText(mainActivityContext, "Error toggling LED state", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mainActivityContext, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendCurrentTime() {
        // Get the current time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create the service
        ESP32Service service = retrofit.create(ESP32Service.class);

        // Make the call
        Call<ResponseBody> call = service.setTime(currentTime);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(mainActivityContext, "Time updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mainActivityContext, "Error setting time", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mainActivityContext, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
