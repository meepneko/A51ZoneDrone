package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class controller_page_connectWifi extends AppCompatActivity {
    Button wifiBtn;
    WifiManager mywifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_page_connect_wifi);
        initializer();
        wifiEnablerListener();
    }

    private void wifiEnablerListener() {
        wifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiBtn.getText()=="Wifi is ON"){
                    mywifiManager.setWifiEnabled(true);
                    Log.d("Check", "mate");
                    wifiBtn.setText("Wifi is ON");
                }
                else{
                    mywifiManager.setWifiEnabled(false);
                    Log.d("Check", "me");
                    wifiBtn.setText("Wifi is OFF");
                }
            }
        });
    }

    private void initializer() {
        wifiBtn=(Button) findViewById(R.id.button_enableWifi);
        wifiBtn.setText("Wifi is OFF");
        mywifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }




}
