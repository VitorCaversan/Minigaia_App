package com.example.minigaia;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    public  BluetoothAdapter bluetoothAdapter;
    private SensorData sensorData;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    public BluetoothActivity(SensorData pSensorData)
    {
        this.sensorData = pSensorData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.enableBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Bluetooth was enabled successfully
                        // Continue with your Bluetooth operations
                    } else {
                        // Bluetooth was not enabled
                        // Handle the case when Bluetooth is not enabled
                    }
                });
    }

    public void syncToConnectedDevice() throws IOException
    {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (null == bluetoothAdapter)
        {
            Toast.makeText(this, "Bluetooth is not supported on this device or\nNo device is connected",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "Bluetooth permission needed",
                               Toast.LENGTH_SHORT).show();
            }
            else
            {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                String       deviceAddrs = null;
                String       name        = null;
                ParcelUuid[] uuid        = null;
                int          iterator    = 0;

                for (BluetoothDevice device : pairedDevices)
                {
                    deviceAddrs = device.getAddress();
                    name        = device.getName();
                    uuid        = device.getUuids();

                    // If an ESP has been found
                    if (name.contains("ESP"))
                    {
                        break;
                    }

                    iterator++;
                }

                if ((null != deviceAddrs) && (null != uuid) && (null != name))
                {
                    Toast.makeText(this, name + " " + deviceAddrs.toString(),
                                   Toast.LENGTH_SHORT).show();

                    BluetoothDevice device    = null;
                    UUID            finalUUID = null;

                    try {
                        device = bluetoothAdapter.getRemoteDevice(deviceAddrs);
                        finalUUID = uuid[iterator].getUuid();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(this, "Could not get remote device",
                                       Toast.LENGTH_SHORT).show();
                    }

                    BluetoothSocket socket = null;
                    if (null != device)
                    {
                        try {
                            socket = device.createRfcommSocketToServiceRecord(finalUUID);
                            socket.connect();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(this, "Could not create socket remote device",
                                           Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (null != socket)
                    {
                        try
                        {
                            OutputStream outputStream = socket.getOutputStream();
                            String message = "Javalicomaids"; // Replace with your desired message
                            byte[] messageBytes = message.getBytes();
                            outputStream.write(messageBytes);
                            outputStream.flush();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(this, "Could not send data to the remote device",
                                            Toast.LENGTH_SHORT).show();
                        }
                    }
                }


            }
        }
    }

    ////// UNUSED FUNCTIONS ///////
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
}
