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
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private ImageView imageView;


    static final int MESSAGE_READ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compatibility);
        onConnect = findViewById(R.id.onConnect);
        imageView = findViewById(R.id.imgView);

        sensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
//        ImageView imageView = new ImageView(getApplicationContext());
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        imageView.setImageResource(R.drawable.cancel);
//
//        linearLayout.addView(imageView);
//        setContentView(linearLayout);

        SpannableStringBuilder sensorText= new SpannableStringBuilder ();

        //Accelerometer
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // Success!
            checkAC = true;
            sensorText.append(" ", new ImageSpan(getApplicationContext(), R.drawable.tick), 0)
                    .append("\t\t\t")
                    .append("Accelerometer")
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"));
        } else {
            // Failure!
            checkAC = false;
            sensorText.append(" ", new ImageSpan(getApplicationContext(), R.drawable.cancel), 0)
                    .append("\t\t\t")
                    .append("Accelerometer")
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"));
        }

        //Magnetometer
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            // Success!
            checkMA = true;
            sensorText.append(" ", new ImageSpan(getApplicationContext(), R.drawable.tick), 0)
                    .append("\t\t\t")
                    .append("Magnetometer")
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"));
        } else {
            // Failure!
            checkMA = false;
            sensorText.append(" ", new ImageSpan(getApplicationContext(), R.drawable.cancel), 0)
                    .append("\t\t\t")
                    .append("Magnetometer")
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"));
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
}
