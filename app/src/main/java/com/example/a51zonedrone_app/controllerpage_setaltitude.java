package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class controllerpage_setaltitude extends AppCompatActivity {
   TextView Tvalt;
   Button Btnnext;
    SeekBar mySeekbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controllerpage_setaltitude);
        initialize();

        int progress= mySeekbar.getProgress();
        Tvalt.setText("Altitude: "+progress +"m");
    }

    private void initialize() {
        mySeekbar= findViewById(R.id.seekBar);
        mySeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        Tvalt=findViewById(R.id.Altitude_textView);
    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            Tvalt.setText("Altitude: "+progress +"m");
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
