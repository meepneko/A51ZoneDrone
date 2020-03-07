package com.example.a51zonedrone_app;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.NetworkInfo;
//import android.net.wifi.p2p.WifiP2pConfig;
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pInfo;
//import android.net.wifi.p2p.WifiP2pManager;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.net.InetAddress;
//
//import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
//
//public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
//
//    private WifiP2pManager mManager;
//    private WifiP2pManager.Channel mChannel;
//    private Context mActivity;
//
//
//    public WifiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, Context mActivity){
//        super();
//        this.mManager = mManager;
//        this.mChannel = mChannel;
//        this.mActivity = mActivity;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//         String action= intent.getAction();
//
//        if(mActivity instanceof controllerpage_waypoint){
//            //Check to see if Wi-Fi is enabled and notify appropriate activity
//            if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
//
//                //UI update to indicate wifi p2p status
//                int state= intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
//
//                if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
//                    //Toast.makeText(context,"Wifi is ON",Toast.LENGTH_SHORT).show();
//                    if(mActivity != null) ((controllerpage_waypoint) mActivity).setIsWifiP2pEnabled(true);
//                }else{
//                    //Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();
//                    if(mActivity != null) ((controllerpage_waypoint) mActivity).setIsWifiP2pEnabled(false);
//                }
//            }
//            //Call WiFiP2pManager.requestPeers() to get a list of current peers
//            else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
//                if(mManager!=null){
//                    mManager.requestPeers(mChannel, ((controllerpage_waypoint) mActivity).peerListListener);
//                    //Toast.makeText(context,"mManager!=null ",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//            //Respond to new connection or disconnections
//            else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
//                if(mManager == null){
//                    return;
//                }
//
//                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//
//                if(networkInfo.isConnected()){
//
//                    if(mActivity != null){
//                        mManager.requestConnectionInfo(mChannel, ((controllerpage_waypoint) mActivity).connectionInfoListener);
//                        ((controllerpage_waypoint) mActivity).setIsWifiConnected(true);
//                    }
//                }
//                else{
//                    if(mActivity != null){
//                        ((controllerpage_waypoint) mActivity).setIsWifiConnected(false);
//                    }
//                }
//            }
//            //Respond to this device's Wi-Fi state changing
//            else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
////             DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
////                     .findFragmentById(R.id.frag_list);
////             fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
////                     WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
//            }
//        }
//
//        else if(mActivity instanceof dronepage_on_flight){
//            //Check to see if Wi-Fi is enabled and notify appropriate activity
//            if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
//
//                //UI update to indicate wifi p2p status
//                int state= intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
//
//                if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
//                    //Toast.makeText(context,"Wifi is ON",Toast.LENGTH_SHORT).show();
//                    if(mActivity != null) ((dronepage_on_flight) mActivity).setIsWifiP2pEnabled(true);
//                }else{
//                    //Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();
//                    if(mActivity != null) ((dronepage_on_flight) mActivity).setIsWifiP2pEnabled(false);
//                }
//            }
//            //Call WiFiP2pManager.requestPeers() to get a list of current peers
//            else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
//                if(mManager!=null){
//                    mManager.requestPeers(mChannel, ((dronepage_on_flight) mActivity).peerListListener);
//                    //Toast.makeText(context,"mManager!=null ",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//            //Respond to new connection or disconnections
//            else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
//                if(mManager == null){
//                    return;
//                }
//
//                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//
//                if(networkInfo.isConnected()){
//
//                    if(mActivity != null){
//                        mManager.requestConnectionInfo(mChannel, ((dronepage_on_flight) mActivity).connectionInfoListener);
//                        ((dronepage_on_flight) mActivity).setIsWifiConnected(true);
//                    }
//                }
//                else{
//                    if(mActivity != null){
//                        ((dronepage_on_flight) mActivity).setIsWifiConnected(false);
//                    }
//                }
//            }
//            //Respond to this device's Wi-Fi state changing
//            else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
////             DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
////                     .findFragmentById(R.id.frag_list);
////             fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
////                     WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
//            }
//        }
//
//    }
//}
//
////import android.content.BroadcastReceiver;
////import android.content.Context;
////import android.content.Intent;
////import android.content.IntentFilter;
////import android.net.NetworkInfo;
////import android.net.wifi.p2p.WifiP2pDevice;
////import android.net.wifi.p2p.WifiP2pDeviceList;
////import android.net.wifi.p2p.WifiP2pInfo;
////import android.net.wifi.p2p.WifiP2pManager;
////import android.util.Log;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
////
////    public static IntentFilter getIntentFilter() {
////        IntentFilter intentFilter = new IntentFilter();
////        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
////        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
////        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
////        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
////        return intentFilter;
////    }
////
////    private static final String TAG = "DirectBroadcastReceiver";
////
////    private WifiP2pManager mWifiP2pManager;
////
////    private WifiP2pManager.Channel mChannel;
////
////    private DirectActionListener mDirectActionListener;
////
////    public WifiDirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, DirectActionListener directActionListener) {
////        mWifiP2pManager = wifiP2pManager;
////        mChannel = channel;
////        mDirectActionListener = directActionListener;
////    }
////
////    @Override
////    public void onReceive(Context context, Intent intent) {
////        String action = intent.getAction();
////        if (action != null) {
////            switch (action) {
////                // Used to indicate whether Wifi P2P is available
////                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: {
////                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -100);
////                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
////                        mDirectActionListener.wifiP2pEnabled(true);
////                    } else {
////                        mDirectActionListener.wifiP2pEnabled(false);
////                        List<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<>();
////                        mDirectActionListener.onPeersAvailable(wifiP2pDeviceList);
////                    }
////                    break;
////                }
////                // Peer node list changed
////                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: {
////                    mWifiP2pManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
////                        @Override
////                        public void onPeersAvailable(WifiP2pDeviceList peers) {
////                            mDirectActionListener.onPeersAvailable(peers.getDeviceList());
////                        }
////                    });
////                    break;
////                }
////                // Wifi P2P connection status changed
////                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: {
////                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
////                    if (networkInfo != null && networkInfo.isConnected()) {
////                        mWifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
////                            @Override
////                            public void onConnectionInfoAvailable(WifiP2pInfo info) {
////                                mDirectActionListener.onConnectionInfoAvailable(info);
////                            }
////                        });
////                        Log.e(TAG, "Connected");
////                    } else {
////                        mDirectActionListener.onDisconnection();
////                        Log.e(TAG, "Disconnected");
////                    }
////                    break;
////                }
////                //The device information of this device has changed
////                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: {
////                    WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
////                    mDirectActionListener.onSelfDeviceAvailable(wifiP2pDevice);
////                    break;
////                }
////            }
////        }
////    }
////}

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    Context mainActivity;

    public WifiDirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, Context mainActivity) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.mainActivity=mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getAction();

        if(mainActivity instanceof controllerpage_waypoint){
            if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
            {
                if(wifiP2pManager==null)
                {
                    return;
                }
                try{
                    WifiP2pGroup wifiP2pGroup=intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                    if(wifiP2pGroup!=null&&wifiP2pGroup.getOwner()!=null&wifiP2pGroup.getOwner().deviceName!=null)
                        controllerpage_waypoint.connectedDeviceName=wifiP2pGroup.getOwner().deviceName;}
                catch (Exception e)
                {

                }
                NetworkInfo networkInfo=intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected())
                {
                    wifiP2pManager.requestConnectionInfo(channel, ((controllerpage_waypoint) mainActivity).connectionInfoListener);
                    ((controllerpage_waypoint) mainActivity).setIsWifiConnected(true);
                }
                else {
                    //mainActivity.constate.setText("DEVICE DISCONNECTED");
                    ((controllerpage_waypoint) mainActivity).setIsWifiConnected(false);
                }
            }
            if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
            {
                if(wifiP2pManager!=null)
                {
                    wifiP2pManager.requestPeers(channel, ((controllerpage_waypoint) mainActivity).peerListListener);
                }
            }
            if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
            {
                int state=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
                if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                {
                    Toast.makeText(context,"WIFI IS ON",Toast.LENGTH_SHORT).show();
                    ((controllerpage_waypoint) mainActivity).setIsWifiP2pEnabled(true);
                }
                else {
                    Toast.makeText(context, "WIFI IS OFF", Toast.LENGTH_SHORT).show();
                    ((controllerpage_waypoint) mainActivity).setIsWifiP2pEnabled(true);
                }
            }
            if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
            {

            }
        }

        else if(mainActivity instanceof dronepage_on_flight){
            if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
            {
                if(wifiP2pManager==null)
                {
                    return;
                }
                try{
                    WifiP2pGroup wifiP2pGroup=intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                    if(wifiP2pGroup!=null&&wifiP2pGroup.getOwner()!=null&wifiP2pGroup.getOwner().deviceName!=null)
                        controllerpage_waypoint.connectedDeviceName=wifiP2pGroup.getOwner().deviceName;}
                catch (Exception e)
                {

                }
                NetworkInfo networkInfo=intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected())
                {
                    wifiP2pManager.requestConnectionInfo(channel, ((dronepage_on_flight) mainActivity).connectionInfoListener);
                    ((dronepage_on_flight) mainActivity).setIsWifiConnected(true);
                }
                else {
                    ((dronepage_on_flight) mainActivity).setIsWifiConnected(false);
                }
            }
            if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
            {
                if(wifiP2pManager!=null)
                {
                    wifiP2pManager.requestPeers(channel, ((dronepage_on_flight) mainActivity).peerListListener);
                }
            }
            if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
            {
                int state=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
                if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                {
                    Toast.makeText(context,"WIFI IS ON",Toast.LENGTH_SHORT).show();
                    ((dronepage_on_flight) mainActivity).setIsWifiP2pEnabled(true);
                }
                else {
                    Toast.makeText(context, "WIFI IS OFF", Toast.LENGTH_SHORT).show();
                    ((dronepage_on_flight) mainActivity).setIsWifiP2pEnabled(true);
                }
            }
            if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
            {

            }
        }
    }
}