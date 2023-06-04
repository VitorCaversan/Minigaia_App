package com.example.minigaia;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.minigaia.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    static final int DESIRED_PH_BTN           = 0;
    static final double TEMPERATURE_THRESHOLD = 25.0;
    static final double PH_TOO_ACID           = 5.0;
    static final double PH_TOO_BASIC          = 9.0;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Bluetooth bluetooth;
    private Memory memory;
    public  Intent timeIntent;
    public  SensorData sensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        this.memory    = new Memory(MainActivity.this);
        this.bluetooth = new Bluetooth(MainActivity.this);

        this.sensorData = this.memory.loadData();

        if (Objects.equals(this.sensorData.getPh(), "0"))
        {
            String jsonString = "{\"ph\":7.2,\"desiredPh\":6.4,\"temperature\":25.3,\"waterLvl\":10.4,\"humidity\":\"67.9\"}";
            this.sensorData = parseJsonData(jsonString);
        }

        connectToEsp();
        // Sets initial text for the buttons
        updateButtons();

        ///////////////// THIS CAME WITH THE TEMPLATE (??) ///////////////////

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);


        ///////////////// INTENTS USED IN BUTTONS ///////////////////

        this.timeIntent = new Intent(this, TimesActivity.class);

        ///////////////// BUTTONS FUNCTIONS ///////////////////

        binding.syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
//                    SensorData newSensData = webServer.updateSensorData(sensorData);
//                    if (!Objects.equals(newSensData.getPh(), "0"))
//                    {
//                        sensorData = newSensData;
//                    }

                    bluetooth.send(sensorData.toJson(false));
                    updateButtons();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

        binding.syncButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast newToast = Toast.makeText(getApplicationContext(),
                                           "Sincronizar o telefone com o controlador",
                                                Toast.LENGTH_SHORT);
                newToast.setGravity(Gravity.TOP, 0, 10);
                newToast.show();
                return true; // Return true to consume the long click event
            }
        });


        binding.measureNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
    //                SensorData newSensData = webServer.updateSensorData(sensorData);
    //                if (!Objects.equals(newSensData.getPh(), "0"))
    //                {
    //                    sensorData = newSensData;
    //                }

                    bluetooth.send(sensorData.toJson(true));
                    updateButtons();
                }
                catch (Exception e)
                {
                    throw new RuntimeException();
                }
            }
        });

        binding.measureNowButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast newToast = Toast.makeText(getApplicationContext(),
                        "Requisitar a medida imediata dos sensores",
                        Toast.LENGTH_SHORT);
                newToast.setGravity(Gravity.TOP, 0, 10);
                newToast.show();
                return true; // Return true to consume the long click event
            }
        });

        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeActivity(view);
            }
        });

        binding.desiredPhBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTextInput(DESIRED_PH_BTN);
            }
        });

        binding.desiredPhBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast newToast = Toast.makeText(getApplicationContext(),
                        "Valor do pH alvo",
                        Toast.LENGTH_SHORT);
                newToast.setGravity(Gravity.TOP, 0, 0);
                newToast.show();
                return true; // Return true to consume the long click event
            }
        });

        binding.phButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast newToast = Toast.makeText(getApplicationContext(),
                        "Valor do pH atual",
                        Toast.LENGTH_SHORT);
                newToast.setGravity(Gravity.TOP, 0, 0);
                newToast.show();
                return true; // Return true to consume the long click event
            }
        });

        binding.humidityBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast newToast = Toast.makeText(getApplicationContext(),
                        "Humidade atual na régua",
                        Toast.LENGTH_SHORT);
                newToast.setGravity(Gravity.TOP, 0, 0);
                newToast.show();
                return true; // Return true to consume the long click event
            }
        });

        binding.temperatureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast newToast = Toast.makeText(getApplicationContext(),
                        "Temperatura atual na régua",
                        Toast.LENGTH_SHORT);
                newToast.setGravity(Gravity.TOP, 0, 0);
                newToast.show();
                return true; // Return true to consume the long click event
            }
        });

        binding.waterLvlBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast newToast = Toast.makeText(getApplicationContext(),
                        "Nível de água no reservatório",
                        Toast.LENGTH_SHORT);
                newToast.setGravity(Gravity.TOP, 0, 0);
                newToast.show();
                return true; // Return true to consume the long click event
            }
        });
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

        outState.putString("ph", this.sensorData.getPh());
        outState.putString("desiredPh", this.sensorData.getDesiredPh());
        outState.putString("temperature", this.sensorData.getTemperature());
        outState.putString("humidity", this.sensorData.getHumidity());
        outState.putString("waterLvl", this.sensorData.getWaterLvl());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedState)
    {
        super.onRestoreInstanceState(savedState);
        this.sensorData.setPh(savedState.getString("ph"));
        this.sensorData.setDesiredPh(savedState.getString("desiredPh"));
        this.sensorData.setTemperature(savedState.getString("temperature"));
        this.sensorData.setHumidity(savedState.getString("humidity"));
        this.sensorData.setWaterLvl(savedState.getString("waterLvl"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);

        // If the request code is the one from TimeActivity
        if (requestCode == 1)
        {
            // Check if the result is RESULT_OK
            if (resultCode == Activity.RESULT_OK)
            {
                String resultData = returnIntent.getStringExtra("earlyMeasureTime");

                this.sensorData.setEarlyMeasureTime(resultData);
            }
            else
            {
                // Handle the case where the result is not RESULT_OK
            }
        }
    }

    private void createTextInput(int pressedBtn)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Valor pH desejado:");

        // Sets up the input
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Sets up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredText = input.getText().toString();

                switch (pressedBtn)
                {
                    case DESIRED_PH_BTN:
                    {
                        treatDesiredPhBtn(enteredText);
                    }
                    break;
                    // Other buttons
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void openTimeActivity(View view)
    {
        // Puts extra values in a buffer to be read when initializing the target activity
        this.timeIntent.putExtra("earlyMeasureTime", this.sensorData.getEarlyMeasureTime());
        int requestCode = 1;

        startActivityForResult(this.timeIntent, requestCode);
    }

    private void treatDesiredPhBtn(String enteredText)
    {
        try
        {
            double value = Double.parseDouble(enteredText);

            if ((value < 0) || (value > 14))
            {
                Toast.makeText(MainActivity.this, "Valor fora dos limites 0 < pH < 14",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                this.sensorData.setDesiredPh(enteredText);
                updateButtons();

                if ((value < 5) || (value > 9))
                {
                    Toast.makeText(MainActivity.this, "É recomendável um valor entre 5 e 9",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (NumberFormatException nF)
        {
            Toast.makeText(MainActivity.this, "Formato errado",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function that updates all the screen buttons with the values of sensorData
     */
    private void updateButtons()
    {
        String auxString = this.sensorData.getPh();
        binding.phButton.setText(auxString);
        double dAuxValue = Double.parseDouble(this.sensorData.getPh());
        if (dAuxValue > PH_TOO_BASIC)
        {
            binding.phButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                                                                        R.color.minigaia_darkerPink));
        }
        else if (dAuxValue < PH_TOO_ACID)
        {
            binding.phButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                                                                        R.color.minigaia_acidGreen));
        }
        else
        {
            binding.phButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                                                                        R.color.minigaia_pink));
        }

        auxString = this.sensorData.getDesiredPh();
        binding.desiredPhBtn.setText(auxString);
        auxString = this.sensorData.getHumidity() + " %";
        binding.humidityBtn.setText(auxString);
        auxString = this.sensorData.getTemperature() + " ºC";

        binding.temperatureButton.setText(auxString);
        dAuxValue = Double.parseDouble(this.sensorData.getTemperature());
        if (dAuxValue > TEMPERATURE_THRESHOLD)
        {
            binding.temperatureButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                    R.color.minigaia_orange));
        }
        else
        {
            binding.temperatureButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                    R.color.minigaia_cool_blue));
        }
        auxString = this.sensorData.getWaterLvl() + " L";
        binding.waterLvlBtn.setText(auxString);

        this.memory.saveData(this.sensorData, MainActivity.this);
    }

    private SensorData parseJsonData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String ph          = jsonObject.getString("ph");
            String desiredPh   = jsonObject.getString("desiredPh");
            String temperature = jsonObject.getString("temperature");
            String waterLvl    = jsonObject.getString("waterLvl");
            String humidity    = jsonObject.getString("humidity");

            return new SensorData(ph, desiredPh, temperature, waterLvl, humidity);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void connectToEsp()
    {
        List<String> pairedDevices = this.bluetooth.getPairedDevices();

        String correctDevice = pairedDevices.stream().filter(deviceName -> deviceName.contains("miniGaia")).findFirst().orElse(null);

        try
        {
            if (bluetooth.connect(correctDevice)){
                Toast.makeText(this, "miniGaia Conectado!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Falha ao conectar miniGaia!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Faça o pareamento do miniGaia", Toast.LENGTH_SHORT).show();
        }
    }
}

