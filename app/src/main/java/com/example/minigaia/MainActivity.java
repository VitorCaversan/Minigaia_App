package com.example.minigaia;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private TableLayout tableLayout;
    public MainActivity.SensorData sensorData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);

        // Replace the hardcoded JSON string with the actual data from your ESP32 sensor
        String jsonString = "{\"ph\":7.2,\"desiredPh\":6.4,\"temperature\":25.3,\"waterLvl\":10.4,\"date\":\"2023-04-21\"}";
        this.sensorData = parseJsonData(jsonString);
        this.tableLayout = findViewById(R.id.rulerTableLayout);
        this.createTableContent(this.sensorData);

        ///////////////// BUTTONS FUNCTIONS ///////////////////

        binding.bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });
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

        TextView date = new TextView(this);
        date.setText("Date " + sensorData.getDate());
        date.setTextSize(25);
        row3.addView(date);
        TextView dateValue = new TextView(this);
        dateValue.setText(" " + sensorData.getDate());
        dateValue.setTextSize(25);
        row3.addView(dateValue);
    }
    private MainActivity.SensorData parseJsonData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            double ph          = jsonObject.getDouble("ph");
            double desiredPh   = jsonObject.getDouble("desiredPh");
            double temperature = jsonObject.getDouble("temperature");
            double waterLvl    = jsonObject.getDouble("waterLvl");
            String date = jsonObject.getString("date");

            return new MainActivity.SensorData(ph, desiredPh, temperature, waterLvl, date);
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

    private static class SensorData {
        private final double ph;
        private final double desiredPh;
        private final double temperature;
        private final double waterLvl;
        private final String date;

        public SensorData(double ph, double desiredPh, double temperature, double waterLvl, String date) {
            this.ph          = ph;
            this.desiredPh   = desiredPh;
            this.temperature = temperature;
            this.waterLvl    = waterLvl;

            // A HIGHER API LEVEL IS NEEDED TO USE THESE FUNCTIONS (26+)
//            LocalDate currentDate = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            String dateString = currentDate.format(formatter);
            this.date = date;
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

        public String getDate() {
            return date;
        }
    }
}