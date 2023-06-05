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

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class Bluetooth implements Runnable {
    // Activity context
    private MainActivity activity;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID para SPP (Serial Port Profile)

    private boolean isConnected;
    private String deviceAddress;
    Map<String, String> macAddresses;

    public Bluetooth(MainActivity activity) {
        this.activity = activity;
        bluetoothManager = (BluetoothManager) activity.getSystemService(activity.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // Checa se o dispositivo suporta Bluetooth
        if (bluetoothAdapter == null) {
            // return error message
        }
        // Solicita permissão para utilizar o Bluetooth caso não esteja habilitado
        while (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, 1);
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
        while (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
        }
        while (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
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
        deviceAddress = deviceName != null ? macAddresses.get(deviceName) : deviceAddress;

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, 1);
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
        if (socket==null) {
            return;
        }
        if (!socket.isConnected())
        {
            connect(null);
        }
        try {
            String message = jsonMessage.toString();
            outputStream.write(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private JSONObject receive() {
        if (socket == null)
        {
            return null;
        }
        if (!socket.isConnected())
        {
            connect(null);
        }
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

    @Override
    public void run() {
        try {
            while (true) {
                JSONObject jsonMessage = receive();
                if (jsonMessage != null && activity != null) {
                    activity.receive(jsonMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
