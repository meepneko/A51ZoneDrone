package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class dronepage_compability extends AppCompatActivity {
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private boolean checkGY, checkAC, checkMA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dronepage_waiting_instruction);
        sensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //List<Sensor> sensorList  = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        StringBuilder sensorText= new StringBuilder();

        //Gyroscope
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            // Success!
            checkGY = true;
            sensorText.append(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE).getName()).append(
                    System.getProperty("line.separator"));
        } else {
            // Failure!
            checkGY = false;
            sensorText.append("No Gyroscope found").append(System.getProperty("line.separator"));
        }

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

        //GPS
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


//        for (Sensor currentSensor : sensorList ) {
//            sensorText.append(currentSensor.getName()).append(
//                    System.getProperty("line.separator"));
//        }

        TextView sensorTextView = (TextView) findViewById(R.id.sensor_list);
        sensorTextView.setText(sensorText);

        Button confirmBtn = findViewById(R.id.btnConfirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkGY == true && checkAC == true && checkMA == true) {
                    Intent startIntent = new Intent(getApplicationContext(), dronepage_on_flight.class);
                    startActivity(startIntent);
                }
                else{
                    Toast.makeText(dronepage_compability.this, "Cannot fly the drone without the required sensors.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
