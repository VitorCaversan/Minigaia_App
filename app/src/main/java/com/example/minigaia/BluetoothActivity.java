package com.example.minigaia;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothActivity extends AppCompatActivity {
    public BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
}
