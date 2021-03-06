package com.example.a51zonedrone_app.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.a51zonedrone_app.R;

public class controllerpage_setaltitude extends AppCompatActivity {
   private TextView Tvalt;
   private Button Btnnext;
   private SeekBar mySeekbar;
   private int seekBarvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controllerpage_setaltitude);
        getSupportActionBar().hide();
        initialize();

        Tvalt.setText("Altitude: "+ seekBarvalue +"m");

        Btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(controllerpage_setaltitude.this, controllerpage_waypoint.class);
                intent.putExtra("seekBarvalue", seekBarvalue);
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        mySeekbar= findViewById(R.id.seekBar);
        seekBarvalue = mySeekbar.getProgress() * 2;
        mySeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        Tvalt=findViewById(R.id.Altitude_textView);
        Btnnext = findViewById(R.id.button_next);
    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            seekBarvalue = progress * 2;
            Tvalt.setText("Altitude: "+ seekBarvalue +"m");
            Log.d("Check", "Altitude "+progress);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

}
