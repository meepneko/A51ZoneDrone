package com.example.a51zonedrone_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    controller_page_connectWifi activity;


    public WifiDirectBroadcastReceiver(WifiP2pManager m, WifiP2pManager.Channel c, controller_page_connectWifi a){
        mManager=m;
        mChannel=c;
        activity = a;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

         String action= intent.getAction();
         if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state= intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);

            if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"Wifi Enable",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Wifi Disable",Toast.LENGTH_SHORT).show();
            }
         }
         else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
                if(mManager!=null){
                  mManager.requestPeers(mChannel,activity.peerListListener);
                  Toast.makeText(context,"mManager!=null ",Toast.LENGTH_SHORT).show();
                }

         }
         else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

         }
         else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
//             DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                     .findFragmentById(R.id.frag_list);
//             fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                     WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
         }

    }

}
