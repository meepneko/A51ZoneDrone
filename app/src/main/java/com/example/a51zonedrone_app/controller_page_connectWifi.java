package com.example.a51zonedrone_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class controller_page_connectWifi extends AppCompatActivity {
    Button wifiBtn;
    Button discBtn;
    ListView mlistView;
    TextView mTextview;
    WifiManager mywifiManager;


    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceive;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] devicesArray;



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
                if (wifiBtn.getText() == "Wifi is OFF") {
                    mywifiManager.setWifiEnabled(true);
                    Log.d("Check", "mate");
                    wifiBtn.setText("Wifi is ON");

                } else {
                    mywifiManager.setWifiEnabled(false);
                    Log.d("Check", "me");
                    wifiBtn.setText("Wifi is OFF");
                }


            }
        });

        discBtn.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                       // discoverPeers();
                       // Toast.makeText(getApplicationContext(),"Searching",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(int reason) {
                       // Toast.makeText(getApplicationContext(),""+reason,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }
    private void initializer() {
        wifiBtn = (Button) findViewById(R.id.button_enableWifi);
        wifiBtn.setText("Wifi is OFF");
        discBtn =(Button) findViewById(R.id.button_discover);
        mlistView = (ListView) findViewById(R.id.WifiListView);
        mTextview =(TextView) findViewById(R.id.discoverStatus);
        mywifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager =(WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel=mManager.initialize(this,getMainLooper(),null);

        mReceive= new WifiDirectBroadcastReceiver(mManager,mChannel,this);

        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }


public void discoverPeers() {

    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            Toast.makeText(getApplicationContext(),"onSuccessdiscoverPeers()",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int reason) {
            Toast.makeText(getApplicationContext(),""+reason,Toast.LENGTH_SHORT).show();
        }
    }

    );
}

//   WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
//       @Override
//       public void onPeersAvailable(WifiP2pDeviceList peerList) {
//           if(!peerList.getDeviceList().equals(peers)){
//               peers.clear();
//               peers.addAll(peerList.getDeviceList());
//
//               deviceNameArray=new String[peerList.getDeviceList().size()];
//               devicesArray=new WifiP2pDevice[peerList.getDeviceList().size()];
//
//               int index=0;
//               for(WifiP2pDevice device: peerList.getDeviceList())
//               {
//                   deviceNameArray[index]=device.deviceName;
//                   devicesArray[index]=device;
//                   index++;
//               }
//
//               ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(),R.layout.activity_controller_page_connect_wifi,deviceNameArray);
//               mlistView.setAdapter(adapter);
//           }
//           if(peers.size()==0)
//           {
//               Toast.makeText(getApplicationContext(),"No devicess Found",Toast.LENGTH_SHORT).show();
//           }
//       }
//   };

    @Override
    protected void onResume() {
        super.onResume();
        mReceive=new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceive,mIntentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceive);
    }

    public void Discover(View view) {
      //  discoverPeers();
        Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();

    }



    PeerListListener peerListListener= new PeerListListener() {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
                gago(peersList);
            Log.d("TAG", "onPeersAvailable: ");
//            if(peers.size()==0){
//                Toast.makeText(getApplicationContext(),"no device found",Toast.LENGTH_SHORT).show();
//                return;
//            }
        }
    };
    private void gago(WifiP2pDeviceList peersList)
    {
        peers.clear();
        peers.addAll(peersList.getDeviceList());

        deviceNameArray= new String[peers.size()];
        devicesArray = new WifiP2pDevice[peers.size()];

        int index=0;
        for(WifiP2pDevice device : peersList.getDeviceList()){
            deviceNameArray[index] = device.deviceName;
            devicesArray[index] = device;
            Log.d("TAG", "gago: "+index);
            index++;

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1 , deviceNameArray);
        mlistView.setAdapter(adapter);
        Log.d("TAG", "gago: ");

    }
}



