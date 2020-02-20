package com.example.a51zonedrone_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private controller_page_connectWifi mActivity;


    public WifiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, controller_page_connectWifi mActivity){
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

         String action= intent.getAction();
         //Check to see if Wi-Fi is enabled and notify appropriate activity
         if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state= intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);

            if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"Wifi is ON",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();
            }
         }
         //Call WiFiP2pManager.requestPeers() to get a list of current peers
         else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
                if(mManager!=null){
                  mManager.requestPeers(mChannel,mActivity.peerListListener);
                  //Toast.makeText(context,"mManager!=null ",Toast.LENGTH_SHORT).show();
                }

         }
         //Respond to new connection or disconnections
         else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
             if(mManager == null){
                 return;
             }

             NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

             if(networkInfo.isConnected()){
                 mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
             }
             else{
                 mActivity.connectionStatus.setText("Device Disconnected");
             }

             if(networkInfo.isAvailable()) {
                 mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
             }
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
