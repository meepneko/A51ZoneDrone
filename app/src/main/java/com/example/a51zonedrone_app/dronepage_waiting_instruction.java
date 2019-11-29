package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class dronepage_waiting_instruction extends AppCompatActivity {
    private SensorManager mSensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dronepage_waiting_instruction);
        mSensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList  = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        StringBuilder sensorText= new StringBuilder();


        for (Sensor currentSensor : sensorList ) {
            sensorText.append(currentSensor.getName()).append(
                    System.getProperty("line.separator"));
        }

        TextView sensorTextView = (TextView) findViewById(R.id.sensor_list);
        sensorTextView.setText(sensorText);

    }
}
