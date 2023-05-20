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

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private TableLayout tableLayout;
    public Intent timeIntent;
    public MainActivity.SensorData sensorData;

    ///// Bluetooth variables /////
    public BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ///////////////// THIS CAME WITH THE TEMPLATE (??) ///////////////////

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);

        // Replace the hardcoded JSON string with the actual data from your ESP32 sensor
        String jsonString = "{\"ph\":7.2,\"desiredPh\":6.4,\"temperature\":25.3,\"waterLvl\":10.4,\"humidity\":\"67.9\"}";
        this.sensorData = parseJsonData(jsonString);
        this.tableLayout = findViewById(R.id.rulerTableLayout);
        this.createTableContent(this.sensorData);

        ///////////////// INTENTS USED IN BUTTONS ///////////////////

        this.timeIntent = new Intent(this, TimesActivity.class);

        this.enableBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK)
            {
                // Bluetooth was enabled successfully
                // Continue with your Bluetooth operations
            }
            else
            {
                // Bluetooth was not enabled
                // Handle the case when Bluetooth is not enabled
            }
        });

        ///////////////// BUTTONS FUNCTIONS ///////////////////

        binding.bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBluetoothActivity(view);
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

            }
        });
    }

    public void openTimeActivity(View view)
    {
        startActivity(this.timeIntent);
    }
    public void startBluetoothActivity(View view)
    {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null == bluetoothAdapter)
        {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (false == bluetoothAdapter.isEnabled())
            {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.enableBluetoothLauncher.launch(enableBluetoothIntent);
            }

            // Bluetooth is available and enabled
            if (true == bluetoothAdapter.isEnabled())
            {
                if (ContextCompat.checkSelfPermission(this,
                                                      "android.permission.ACCESS_FINE_LOCATION") !=
                    PackageManager.PERMISSION_GRANTED)
                {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(this,
                            new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                            REQUEST_BLUETOOTH_PERMISSION);
                }
                else
                {
                    startBluetoothDiscovery();
                }
            }
        }
    }

    private void startBluetoothDiscovery() {
        if (this.bluetoothAdapter.isDiscovering()) {
            // Bluetooth discovery is already in progress, cancel it first
            this.bluetoothAdapter.cancelDiscovery();
        }
        // Start Bluetooth discovery
        this.bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Permission is granted, proceed with Bluetooth functionality
                startBluetoothDiscovery();
            }
            else
            {
                // Permission is denied, handle the case accordingly
            }
        }
    }


    private void createTableContent(MainActivity.SensorData sensorData) {
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
    private MainActivity.SensorData parseJsonData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            double ph          = jsonObject.getDouble("ph");
            double desiredPh   = jsonObject.getDouble("desiredPh");
            double temperature = jsonObject.getDouble("temperature");
            double waterLvl    = jsonObject.getDouble("waterLvl");
            double humidity     = jsonObject.getDouble("humidity");

            return new MainActivity.SensorData(ph, desiredPh, temperature, waterLvl, humidity);
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

        outState.putDouble("ph", this.sensorData.ph);
        outState.putDouble("desiredPh", this.sensorData.desiredPh);
        outState.putDouble("temperature", this.sensorData.temperature);
        outState.putDouble("humidity", this.sensorData.humidity);
        outState.putDouble("waterLvl", this.sensorData.waterLvl);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedState)
    {
        super.onRestoreInstanceState(savedState);

        this.sensorData.ph          = savedState.getDouble("ph");
        this.sensorData.desiredPh   = savedState.getDouble("desiredPh");
        this.sensorData.temperature = savedState.getDouble("temperature");
        this.sensorData.humidity    = savedState.getDouble("humidity");
        this.sensorData.waterLvl    = savedState.getDouble("waterLvl");
    }

    private static class SensorData {
        private double ph;
        private double desiredPh;
        private double temperature;
        private double humidity;
        private double waterLvl;
        private String date;

        public SensorData(double ph, double desiredPh, double temperature, double waterLvl, double humidity) {
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

        public double getPh() {
            return ph;
        }

        public double getDesiredPh() {
            return desiredPh;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getWaterLvl()
        {
            return this.waterLvl;
        }

        public double gethumidity()
        {
            return this.humidity;
        }

        public String getDate() {
            return date;
        }
    }
}