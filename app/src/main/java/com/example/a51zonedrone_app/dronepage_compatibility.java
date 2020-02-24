package com.example.a51zonedrone_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class dronepage_compatibility extends AppCompatActivity {
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private boolean checkGY, checkAC, checkMA;
    private TextView onConnect;


    static final int MESSAGE_READ = 1;

    ClientClass clientClass;
    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compatibility);
        onConnect = findViewById(R.id.onConnect);

        sensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        StringBuilder sensorText= new StringBuilder();

        //Accelerometer
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // Success!
            checkAC = true;
            sensorText.append(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getName()).append(
                    System.getProperty("line.separator"));
        } else {
            // Failure!
            checkAC = false;
            sensorText.append("No Accelerometer found").append(System.getProperty("line.separator"));
        }

        //Magnetometer
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            // Success!
            checkMA = true;
            sensorText.append(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD).getName()).append(
                    System.getProperty("line.separator"));
        } else {
            // Failure!
            checkMA = false;
            sensorText.append("No Magnetometer found").append(System.getProperty("line.separator"));
        }

        TextView sensorTextView = (TextView) findViewById(R.id.sensor_list);
        sensorTextView.setText(sensorText);

        Button confirmBtn = findViewById(R.id.btnConfirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAC == true && checkMA == true) {
                    Intent startIntent = new Intent(getApplicationContext(), dronepage_on_flight.class);
                    startActivity(startIntent);
                }
                else{
                    Toast.makeText(dronepage_compatibility.this, "Cannot fly the drone without the required sensors.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    break;
            }
            return true;
        }
    });

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;
            if(info.groupFormed){
                onConnect.setText("Drone");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt){
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                if(outputStream == null){
                    outputStream = socket.getOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(socket != null){
                try {
                    bytes = inputStream.read(buffer);
                    if(bytes > 0){
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress){
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888),500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
