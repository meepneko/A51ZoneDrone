package com.example.a51zonedrone_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private controllerpage_waypoint mActivity;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private dronepage_on_flight activity;


    public WifiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, controllerpage_waypoint mActivity){
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, dronepage_on_flight activity){
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

         String action= intent.getAction();
         //Check to see if Wi-Fi is enabled and notify appropriate activity
         if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){

             //UI update to indicate wifi p2p status
             int state= intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);

            if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                //Toast.makeText(context,"Wifi is ON",Toast.LENGTH_SHORT).show();
                if(mActivity != null) mActivity.setIsWifiP2pEnabled(true);

                if(activity != null) activity.setIsWifiP2pEnabled(true);
            }else{
                //Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();
                if(mActivity != null) mActivity.setIsWifiP2pEnabled(false);

                if(activity != null) activity.setIsWifiP2pEnabled(false);
            }
         }
         //Call WiFiP2pManager.requestPeers() to get a list of current peers
         else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
                if(mManager!=null){
                  mManager.requestPeers(mChannel,mActivity.peerListListener);
                  //Toast.makeText(context,"mManager!=null ",Toast.LENGTH_SHORT).show();
                }

                if(manager != null){
                    manager.requestPeers(channel, activity.peerListListener);
                }

         }
         //Respond to new connection or disconnections
         else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
             if(mManager == null){
                 return;
             }

             if(manager == null) return;

             NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

             if(networkInfo.isConnected()){

                 if(mActivity != null){
                     mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
                     mActivity.setIsWifiConnected(true);
                 }

                 if(activity != null){
                     manager.requestConnectionInfo(channel, activity.connectionInfoListener);
                     activity.setIsWifiConnected(true);
                 }

             }
             else{
                 if(mActivity != null){
                     mActivity.setIsWifiConnected(false);
                 }

                 if(activity != null){
                     activity.setIsWifiConnected(false);
                 }
                 //mActivity.connectionStatus.setText("Device Disconnected");
             }

//             if(networkInfo.isAvailable()) {
//                 mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
//             }
         }
         //Respond to this device's Wi-Fi state changing
         else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
//             DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                     .findFragmentById(R.id.frag_list);
//             fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                     WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
         }

    }
}
