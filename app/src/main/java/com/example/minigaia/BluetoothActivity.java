package com.example.minigaia;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    public BluetoothAdapter bluetoothAdapter;
    private SensorData sensorData;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;


    Button listen, send, listDevices;
    ListView listView;
    TextView msg_box, status;

    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "Minigaia";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sensorData = new SensorData(10.0, 10.0, 10.0, 10.0, 10.0);

//        Intent intent = getIntent();
//        if (null != intent)
//        {
//            this.sensorData = (SensorData) intent.getSerializableExtra("sensorData");
//        }

        findViewByIdes();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
//            this.enableBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // Bluetooth was enabled successfully
//                        // Continue with your Bluetooth operations
//                    } else {
//                        // Bluetooth was not enabled
//                        // Handle the case when Bluetooth is not enabled
//                    }
//                });
        }

        implementListeners();
    }

    public void setSensorData(SensorData pSensorData)
    {
        this.sensorData = pSensorData;
    }

    private void implementListeners() {

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;

                if( bt.size()>0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index]= device;
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass=new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string= sensorData.getValuesAsString();
                sendReceive.write(string.getBytes());
            }
        });
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Searching");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    private void findViewByIdes() {
        listen=(Button) findViewById(R.id.listen);
        send=(Button) findViewById(R.id.send);
        listView=(ListView) findViewById(R.id.listview);
        msg_box =(TextView) findViewById(R.id.msg);
        status=(TextView) findViewById(R.id.status);
        listDevices=(Button) findViewById(R.id.listDevices);
    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        this.enableBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // Bluetooth was enabled successfully
//                        // Continue with your Bluetooth operations
//                    } else {
//                        // Bluetooth was not enabled
//                        // Handle the case when Bluetooth is not enabled
//                    }
//                });
//    }

//
//    ////////////////// UNUSED FUNCTIONS ////////////////////////
//    public void syncToConnectedDevice() throws IOException
//    {
//        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (null == bluetoothAdapter)
//        {
//            Toast.makeText(this, "Bluetooth is not supported on this device or\nNo device is connected",
//                    Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
////            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
////                // TODO: Consider calling
////                //    ActivityCompat#requestPermissions
////                // here to request the missing permissions, and then overriding
////                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                //                                          int[] grantResults)
////                // to handle the case where the user grants the permission. See the documentation
////                // for ActivityCompat#requestPermissions for more details.
////                Toast.makeText(this, "Bluetooth permission needed",
////                               Toast.LENGTH_SHORT).show();
////            }
////            else
////            {
//                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//                String       deviceAddrs = null;
//                String       name        = null;
//                ParcelUuid[] uuid        = null;
//                int          iterator    = 0;
//
//                for (BluetoothDevice device : pairedDevices)
//                {
//                    deviceAddrs = device.getAddress();
//                    name        = device.getName();
//                    uuid        = device.getUuids();
//
//                    // If an ESP has been found
//                    if (name.contains("ESP"))
//                    {
//                        break;
//                    }
//
//                    iterator++;
//                }
//
//                if ((null != deviceAddrs) && (null != uuid) && (null != name))
//                {
//                    Toast.makeText(this, name + " " + deviceAddrs.toString(),
//                                   Toast.LENGTH_SHORT).show();
//
//                    BluetoothDevice device    = null;
//                    UUID            finalUUID = null;
//
//                    try {
//                        device = bluetoothAdapter.getRemoteDevice(deviceAddrs);
//                        finalUUID = uuid[iterator].getUuid();
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Could not get remote device",
//                                       Toast.LENGTH_SHORT).show();
//                    }
//
//                    BluetoothSocket socket = null;
//                    if (null != device)
//                    {
//                        try {
//                            socket = device.createRfcommSocketToServiceRecord(finalUUID);
//                            socket.connect();
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Could not create socket remote device",
//                                           Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    if (null != socket)
//                    {
//                        try
//                        {
//                            OutputStream outputStream = socket.getOutputStream();
//                            String message = "1"; // Replace with your desired message
//                            byte[] messageBytes = message.getBytes();
//                            outputStream.write(messageBytes);
//                            outputStream.flush();
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Could not send data to the remote device",
//                                            Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//
////            }
//        }
//    }
//
//    public void startBluetoothActivity(View view)
//    {
//        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (null == bluetoothAdapter)
//        {
//            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            if (false == bluetoothAdapter.isEnabled())
//            {
//                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                this.enableBluetoothLauncher.launch(enableBluetoothIntent);
//            }
//
//            // Bluetooth is available and enabled
//            if (true == bluetoothAdapter.isEnabled())
//            {
//                if (ContextCompat.checkSelfPermission(this,
//                        "android.permission.ACCESS_FINE_LOCATION") !=
//                        PackageManager.PERMISSION_GRANTED)
//                {
//                    // Permission is not granted, request it
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{"android.permission.ACCESS_FINE_LOCATION"},
//                            REQUEST_BLUETOOTH_PERMISSION);
//                }
//                else
//                {
//                    startBluetoothDiscovery();
//                }
//            }
//        }
//    }
//
//    private void startBluetoothDiscovery() {
//        if (this.bluetoothAdapter.isDiscovering()) {
//            // Bluetooth discovery is already in progress, cancel it first
//            this.bluetoothAdapter.cancelDiscovery();
//        }
//        // Start Bluetooth discovery
//        this.bluetoothAdapter.startDiscovery();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
//    {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_BLUETOOTH_PERMISSION)
//        {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                // Permission is granted, proceed with Bluetooth functionality
//                startBluetoothDiscovery();
//            }
//            else
//            {
//                // Permission is denied, handle the case accordingly
//            }
//        }
//    }
}
