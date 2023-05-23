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
    }

    public interface ESP32Service {
        @GET("/")
        Call<ResponseBody> getValue();
        @GET("/led")
        Call<ResponseBody> toggleLED();
        @GET("/sync")
        Call<ResponseBody> getMeasures();
        @FormUrlEncoded
        @POST("/time")
        Call<ResponseBody> setTime(@Field("time") String time);

        @FormUrlEncoded
        @POST("/ESPget")
        Call<ResponseBody> sendData(@Field("time")          String time,
                                    @Field("target_pH")     String targetPh,
                                    @Field("scheduledTime") String measureTime);
    }

    /**
     * Makes a request and gets
     */
    public void getResponseFromESP() {
        ESP32Service service = retrofit.create(ESP32Service.class);
        Call<ResponseBody> call = service.getValue();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()) {
                        String responseBodyString = response.body().string();
                        Toast.makeText(mainActivityContext, responseBodyString, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the error here
                        Toast.makeText(mainActivityContext, "Error: " + response.errorBody(), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendCurrentTime();
                Toast.makeText(mainActivityContext, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public SensorData updateSensorData(String desiredPh) {
        ESP32Service service = retrofit.create(ESP32Service.class);
        Call<ResponseBody> call = service.getMeasures();
        sendCurrentTime();

        //  It has to be an array, otherwise it can't be accessed by the following functions
        SensorData[] sensorData = {new SensorData("0", desiredPh, "0", "0", "0")};

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();

                        sensorData[0] = parseResponseString(responseString, desiredPh);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mainActivityContext, "Error updating measures", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mainActivityContext, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return sensorData[0];
    }

    public SensorData parseResponseString(String responseString, String desiredPh)
    {
        String sensorValues;
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
