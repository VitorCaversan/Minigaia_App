package com.example.minigaia;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import org.json.JSONException;
import org.json.JSONObject;

public class RulerActivity extends AppCompatActivity {
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Replace the hardcoded JSON string with the actual data from your ESP32 sensor
        String jsonString = "{\"ph\":7.2,\"desiredPh\":VaiMataTudoAsPranta,\"temperature\":25.3,\"date\":\"2023-04-21\"}";
        SensorData sensorData = parseJsonData(jsonString);
        this.tableLayout = findViewById(R.id.rulerTableLayout);
        this.createTableContent(sensorData);
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
        currentPh.setText("Nível Atual do Ph: " + sensorData.getPh());
        currentPh.setTextSize(18);
        row0.addView(currentPh);

        TextView desiredPh = new TextView(this);
        desiredPh.setText("Nível Desejado de Ph:" + sensorData.getDesiredPh());
        desiredPh.setTextSize(18);
        row1.addView(desiredPh);

        TextView text2 = new TextView(this);
        text2.setText("Nível da água: " + sensorData.getTemperature());
        text2.setTextSize(18);
        row2.addView(text2);

        TextView date = new TextView(this);
        date.setText("Date " + sensorData.getDate());
        date.setTextSize(18);
        row3.addView(date);
    }
    private SensorData parseJsonData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            double ph = jsonObject.getDouble("ph");
            String desiredPh = jsonObject.getString("desiredPh");
            double temperature = jsonObject.getDouble("temperature");
            String date = jsonObject.getString("date");

            return new SensorData(ph, desiredPh, temperature, date);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static class SensorData {
        private final double ph;

        private final String desiredPh;
        private final double temperature;
        private final String date;

        public SensorData(double ph, String desiredPh, double temperature, String date) {
            this.ph = ph;
            this.desiredPh = desiredPh;
            this.temperature = temperature;
            this.date = date;
        }

        public double getPh() {
            return ph;
        }

        public String getDesiredPh() {
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