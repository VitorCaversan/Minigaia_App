package com.example.minigaia;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.minigaia.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private TableLayout tableLayout;
    public Intent timeIntent;
    public SensorData sensorData;
    public BluetoothActivity bluetoothActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Replace the hardcoded JSON string with the actual data from your ESP32 sensor
        String jsonString = "{\"ph\":7.2,\"desiredPh\":6.4,\"temperature\":25.3,\"waterLvl\":10.4,\"humidity\":\"67.9\"}";
        this.sensorData = parseJsonData(jsonString);
        this.tableLayout = findViewById(R.id.rulerTableLayout);
        this.createTableContent(this.sensorData);

        this.bluetoothActivity = new BluetoothActivity(sensorData);

        ///////////////// THIS CAME WITH THE TEMPLATE (??) ///////////////////

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);


        ///////////////// INTENTS USED IN BUTTONS ///////////////////

        this.timeIntent = new Intent(this, TimesActivity.class);

        ///////////////// BUTTONS FUNCTIONS ///////////////////

        binding.bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeActivity(view);
            }
        });

        binding.syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    bluetoothActivity.syncToConnectedDevice();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void openTimeActivity(View view)
    {
        startActivity(this.timeIntent);
    }


    private void createTableContent(SensorData sensorData) {
        TableRow row0 = new TableRow(this);
        this.tableLayout.addView(row0);
        TableRow row1 = new TableRow(this);
        this.tableLayout.addView(row1);
        TableRow row2 = new TableRow(this);
        this.tableLayout.addView(row2);
        TableRow row3 = new TableRow(this);
        this.tableLayout.addView(row3);

        TextView currentPh = new TextView(this);
        currentPh.setText("Nível Atual do Ph:");
        currentPh.setTextSize(25);
        row0.addView(currentPh);
        TextView currentPhValue = new TextView(this);
        currentPhValue.setText(" " + sensorData.getPh());
        currentPhValue.setTextSize(25);
        row0.addView(currentPhValue);

        TextView desiredPh = new TextView(this);
        desiredPh.setText("Nível Desejado de Ph:");
        desiredPh.setTextSize(25);
        row1.addView(desiredPh);
        TextView desiredPhValue = new TextView(this);
        desiredPhValue.setText(" " + sensorData.getDesiredPh());
        desiredPhValue.setTextSize(25);
        row1.addView(desiredPhValue);

        TextView waterLvl = new TextView(this);
        waterLvl.setText("Nível da água:");
        waterLvl.setTextSize(25);
        row2.addView(waterLvl);
        TextView waterLvlValue = new TextView(this);
        waterLvlValue.setText(" " + sensorData.getTemperature());
        waterLvlValue.setTextSize(25);
        row2.addView(waterLvlValue);

        TextView temperature = new TextView(this);
        temperature.setText("Date " + sensorData.getTemperature());
        temperature.setTextSize(25);
        row3.addView(temperature);
        TextView temperatureValue = new TextView(this);
        temperatureValue.setText(" " + sensorData.getTemperature());
        temperatureValue.setTextSize(25);
        row3.addView(temperatureValue);
    }
    private SensorData parseJsonData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            double ph          = jsonObject.getDouble("ph");
            double desiredPh   = jsonObject.getDouble("desiredPh");
            double temperature = jsonObject.getDouble("temperature");
            double waterLvl    = jsonObject.getDouble("waterLvl");
            double humidity     = jsonObject.getDouble("humidity");

            return new SensorData(ph, desiredPh, temperature, waterLvl, humidity);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putDouble("ph", this.sensorData.getPh());
        outState.putDouble("desiredPh", this.sensorData.getDesiredPh());
        outState.putDouble("temperature", this.sensorData.getTemperature());
        outState.putDouble("humidity", this.sensorData.getHumidity());
        outState.putDouble("waterLvl", this.sensorData.getWaterLvl());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedState)
    {
        super.onRestoreInstanceState(savedState);

        this.sensorData.setPh(savedState.getDouble("ph"));
        this.sensorData.setDesiredPh(savedState.getDouble("desiredPh"));
        this.sensorData.setTemperature(savedState.getDouble("temperature"));
        this.sensorData.setHumidity(savedState.getDouble("humidity"));
        this.sensorData.setWaterLvl(savedState.getDouble("waterLvl"));
    }

}