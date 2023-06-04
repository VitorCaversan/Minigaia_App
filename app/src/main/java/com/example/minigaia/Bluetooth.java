package com.example.minigaia;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;

import android.util.ArrayMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class Bluetooth {
    // Activity context
    private MainActivity context;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID para SPP (Serial Port Profile)

    private boolean isConnected;
    Map<String, String> macAddresses;

    public Bluetooth(MainActivity context) {
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // Checa se o dispositivo suporta Bluetooth
        if (bluetoothAdapter == null) {
            // return error message
        }
        // Solicita permissão para utilizar o Bluetooth caso não esteja habilitado
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH}, 1);
            return;
        }
        // Habilita o Bluetooth caso não esteja habilitado
        if (!bluetoothAdapter.isEnabled()) {
        }
        if (bluetoothAdapter.isEnabled()) {
//            updatePairedDevices();
        }
        socket = null;
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<String> getPairedDevices() {
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
        }
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
        {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            macAddresses = new ArrayMap<String, String>();
            List<String> devices = new ArrayList<String>();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    macAddresses.put(deviceName, deviceHardwareAddress);
                    devices.add(deviceName);
                }
            }
            return devices;
        }
        return null;
    }

    public boolean connect(String deviceName) {
        String deviceAddress = macAddresses.get(deviceName);

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH}, 1);
            return false;
        }

        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            isConnected = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        try {
            socket.close();
            isConnected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(JSONObject jsonMessage) {
        try {
            String message = jsonMessage.toString();
            outputStream.write(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public JSONObject receive() {
        try {
            byte[] buffer = new byte[1024];
            int bytes = inputStream.read(buffer);
            String message = new String(buffer, 0, bytes);
            return new JSONObject(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
