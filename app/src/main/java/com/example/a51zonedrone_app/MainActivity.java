package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.a51zonedrone_app.Controller.controllerpage_setaltitude;
import com.example.a51zonedrone_app.Drone.dronepage_compatibility;


public class MainActivity extends AppCompatActivity {

    public boolean mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button controlBtn = (Button) findViewById(R.id.button_controller);
        Button droneBtn = (Button) findViewById(R.id.button_drone);

        droneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), dronepage_compatibility.class);
                startActivity(startIntent);
            }
        });
        controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), controllerpage_setaltitude.class);
                startActivity(startIntent);
                Log.d("check", "Mate main");
            }
        });
    }
}