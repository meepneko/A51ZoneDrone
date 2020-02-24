package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class dronepage_on_flight extends AppCompatActivity {
    private int altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dronepage_on_flight);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Bundle b = getIntent().getExtras();
        altitude = b.getInt("altitude");

    }
}
