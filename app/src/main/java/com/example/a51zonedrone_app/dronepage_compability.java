package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class dronepage_compability extends AppCompatActivity {
    private SensorManager mSensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dronepage_compability);


        setContentView(R.layout.activity_dronepage_compability);
        mSensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList  = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        StringBuilder sensorText= new StringBuilder();


        for (Sensor currentSensor : sensorList ) {
            sensorText.append(currentSensor.getName()).append(
                    System.getProperty("line.separator"));
        }

        TextView sensorTextView = (TextView) findViewById(R.id.SensorList);
        sensorTextView.setText(sensorText);

        Button controlBtn=(Button) findViewById(R.id.Button_Confirm);
       // controlBtn.setEnabled(true);
        if(sensorList.size()>5) {
            controlBtn.setEnabled(true);
            controlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startIntent = new Intent(getApplicationContext(), controllerpage_setaltitude.class);
                    startActivity(startIntent);
                }
            });
        }

    }
}
