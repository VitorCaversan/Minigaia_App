package com.example.minigaia;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class ReservatoryActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    public  SensorData  sensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservatory);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Replace the hardcoded JSON string with the actual data from your ESP32 sensor
        String jsonString = "{\"ph\":7.2,\"desiredPh\":6.4,\"temperature\":25.3,\"waterLvl\":10.4,\"date\":\"2023-04-21\"}";
        this.sensorData = parseJsonData(jsonString);
        this.tableLayout = findViewById(R.id.rulerTableLayout);
        this.createTableContent(this.sensorData);
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

        TextView date = new TextView(this);
        date.setText("Date " + sensorData.getDate());
        date.setTextSize(25);
        row3.addView(date);
        TextView dateValue = new TextView(this);
        dateValue.setText(" " + sensorData.getDate());
        dateValue.setTextSize(25);
        row3.addView(dateValue);
    }
    private SensorData parseJsonData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            double ph          = jsonObject.getDouble("ph");
            double desiredPh   = jsonObject.getDouble("desiredPh");
            double temperature = jsonObject.getDouble("temperature");
            double waterLvl    = jsonObject.getDouble("waterLvl");
            String date = jsonObject.getString("date");

            return new SensorData(ph, desiredPh, temperature, waterLvl, date);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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