package com.example.a51zonedrone_app;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.IntentFilter;
//import android.net.wifi.WifiManager;
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pDeviceList;
//import android.net.wifi.p2p.WifiP2pManager;
//import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class controller_page_connectWifi extends AppCompatActivity {
//    Button wifiBtn;
//    Button discBtn;
//    ListView mlistView;
//    TextView mTextview;
//    WifiManager mywifiManager;
//
//
//    WifiP2pManager mManager;
//    WifiP2pManager.Channel mChannel;
//    BroadcastReceiver mReceive;
//    IntentFilter mIntentFilter;
//
//    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
//    String[] deviceNameArray;
//    WifiP2pDevice[] devicesArray;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_controller_page_connect_wifi);
//        initializer();
//        wifiEnablerListener();
//
//
//
//    }
//
//    private void wifiEnablerListener() {
//        wifiBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (wifiBtn.getText() == "Wifi is OFF") {
//                    mywifiManager.setWifiEnabled(true);
//                    Log.d("Check", "mate");
//                    wifiBtn.setText("Wifi is ON");
//
//                } else {
//                    mywifiManager.setWifiEnabled(false);
//                    Log.d("Check", "me");
//                    wifiBtn.setText("Wifi is OFF");
//                }
//
//
//            }
//        });
//
//        discBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
//
//                    @Override
//                    public void onSuccess() {
//                       // discoverPeers();
//                       // Toast.makeText(getApplicationContext(),"Searching",Toast.LENGTH_LONG).show();
//
//                    }
//
//                    @Override
//                    public void onFailure(int reason) {
//                       // Toast.makeText(getApplicationContext(),""+reason,Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//
//
//
//    }
//    private void initializer() {
//        wifiBtn = (Button) findViewById(R.id.button_enableWifi);
//        wifiBtn.setText("Wifi is OFF");
//        discBtn =(Button) findViewById(R.id.button_discover);
//        mlistView = (ListView) findViewById(R.id.WifiListView);
//        mTextview =(TextView) findViewById(R.id.discoverStatus);
//        mywifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        mManager =(WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//        mChannel=mManager.initialize(this,getMainLooper(),null);
//
//        mReceive= new WifiDirectBroadcastReceiver(mManager,mChannel,this);
//
//        mIntentFilter=new IntentFilter();
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//    }
//
//
//public void discoverPeers() {
//
//    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
//        @Override
//        public void onSuccess() {
//            Toast.makeText(getApplicationContext(),"onSuccessdiscoverPeers()",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onFailure(int reason) {
//            Toast.makeText(getApplicationContext(),""+reason,Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    );
//}
//
////   WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
////       @Override
////       public void onPeersAvailable(WifiP2pDeviceList peerList) {
////           if(!peerList.getDeviceList().equals(peers)){
////               peers.clear();
////               peers.addAll(peerList.getDeviceList());
////
////               deviceNameArray=new String[peerList.getDeviceList().size()];
////               devicesArray=new WifiP2pDevice[peerList.getDeviceList().size()];
////
////               int index=0;
////               for(WifiP2pDevice device: peerList.getDeviceList())
////               {
////                   deviceNameArray[index]=device.deviceName;
////                   devicesArray[index]=device;
////                   index++;
////               }
////
////               ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(),R.layout.activity_controller_page_connect_wifi,deviceNameArray);
////               mlistView.setAdapter(adapter);
////           }
////           if(peers.size()==0)
////           {
////               Toast.makeText(getApplicationContext(),"No devicess Found",Toast.LENGTH_SHORT).show();
////           }
////       }
////   };
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mReceive=new WifiDirectBroadcastReceiver(mManager, mChannel, this);
//        registerReceiver(mReceive,mIntentFilter);
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(mReceive);
//    }
//
//    public void Discover(View view) {
//      //  discoverPeers();
//        Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
//
//    }
//
//
//
//    PeerListListener peerListListener= new PeerListListener() {
//
//        @Override
//        public void onPeersAvailable(WifiP2pDeviceList peersList) {
//                gago(peersList);
//            Log.d("TAG", "onPeersAvailable: ");
////            if(peers.size()==0){
////                Toast.makeText(getApplicationContext(),"no device found",Toast.LENGTH_SHORT).show();
////                return;
////            }
//        }
//    };
//    private void gago(WifiP2pDeviceList peersList)
//    {
//        peers.clear();
//        peers.addAll(peersList.getDeviceList());
//
//        deviceNameArray= new String[peers.size()];
//        devicesArray = new WifiP2pDevice[peers.size()];
//
//        int index=0;
//        for(WifiP2pDevice device : peersList.getDeviceList()){
//            deviceNameArray[index] = device.deviceName;
//            devicesArray[index] = device;
//            index++;
//
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1 , deviceNameArray);
//        mlistView.setAdapter(adapter);
//
//    }
//}
//
//
//

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.example.a51zonedrone_app.WifiDirectBroadcastReceiver;

public class controller_page_connectWifi extends AppCompatActivity {

    Button btnOnOff, btnDiscover, btnSend;
    ListView listView;
    TextView read_msg_box, connectionStatus, txtConnected;
    EditText writeMsg;
    int seekbarval;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray; //Used to show device name in ListView
    WifiP2pDevice[] deviceArray; //Used to connect a Device

    static final int MESSAGE_READ = 1;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_page_connect_wifi);

        inititialWork();
        exqListener();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    read_msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    private void exqListener() {
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    btnOnOff.setText("ON");
                }
                else{
                    wifiManager.setWifiEnabled(true);
                    btnOnOff.setText("OFF");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    //Discovery started successfully
                    public void onSuccess() {
                        connectionStatus.setText("Discovery Started");
                    }

                    //Discovery not started
                    @Override
                    public void onFailure(int reason) {
                        connectionStatus.setText("Discovery Starting Failed");
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                //config.wps.setup = WpsInfo.PBC;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                        txtConnected.setText(seekbarval + "");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //String msg = writeMsg.getText().toString();
                    sendReceive.write(Integer.toString(seekbarval).getBytes());
                    //sendReceive.write(msg.getBytes());
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Please try reconnecting. Reason: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void inititialWork() {
        btnOnOff = (Button) findViewById(R.id.onOff);
        btnDiscover = (Button) findViewById(R.id.discover);
        btnSend = (Button) findViewById(R.id.sendButton);
        listView = (ListView) findViewById(R.id.peerListView);
        read_msg_box = (TextView) findViewById(R.id.readMsg);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        writeMsg = (EditText) findViewById(R.id.writeMsg);
        txtConnected = (TextView) findViewById(R.id.txtConnected);

        Intent intent = getIntent();
        seekbarval = intent.getIntExtra("seekBarvalue",0);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) btnOnOff.setText("ON");
        else btnOnOff.setText("OFF");

        //This class provides the API for managing Wi-Fi peer-to-peer connectivity
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        //A channel that connects the application to the Wi-Fi p2p framework
        //Most p2p operations require a Channel as an argument
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            if(!peersList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peersList.getDeviceList());

                deviceNameArray = new String[peersList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peersList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device : peersList.getDeviceList()){
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter(adapter);
            }

            if(peers.size() == 0){
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            if(info.groupFormed && info.isGroupOwner){
                connectionStatus.setText("Controller");
                Log.d("TAG", "CONNECTED");
                serverClass = new ServerClass();
                serverClass.start();
            }
            else if(info.groupFormed){
                connectionStatus.setText("Drone");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    //register the broadcast receiver
    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        Log.d("TAG", "RESUMED");
    }

    //unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        Log.d("TAG", "PAUSED");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public class ServerClass extends Thread{
         Socket socket;
         ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt){
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                if(outputStream == null){
                    outputStream = socket.getOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(socket != null){
                try {
                    bytes = inputStream.read(buffer);
                    if(bytes > 0){
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress){
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888),500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}