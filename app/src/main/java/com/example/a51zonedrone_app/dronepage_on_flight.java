package com.example.a51zonedrone_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class dronepage_on_flight extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private int altitude;
    private TextView receive;
    ClientClass clientClass;
    SendReceive sendReceive;
    WifiManager wifiManager;
    WifiP2pManager mManager;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter = new IntentFilter();
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private boolean isWifiP2pEnabled = false;
    private boolean isWifiConnected = false;


    String[] deviceNameArray; //Used to show device name in ListView
    WifiP2pDevice[] deviceArray; //Used to connect a Device
    private ListView listView;
    WifiP2pManager.Channel mChannel;
    static final int MESSAGE_READ = 1;

    //For the map
    private ImageButton locBttn;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private dronepage_on_flightLocationCallback callback = new dronepage_on_flightLocationCallback(this);
    private static double latitude, longitude;
    private List<LatLong> receivedWaypoints;
    private boolean checker = false;
    private List<MarkerOptions> marker = new ArrayList<>();
    private MarkerOptions markerOptions = new MarkerOptions();
    private List<LatLng> directionPoint = new ArrayList<>();
    private LineOptions lineOptions = new LineOptions();
    private LineManager lineManager;


    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void setIsWifiConnected(boolean isWifiConnected) {
        this.isWifiConnected = isWifiConnected;
    }

    private class dronepage_on_flightLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<dronepage_on_flight> activityWeakReference;

        dronepage_on_flightLocationCallback(dronepage_on_flight activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            dronepage_on_flight activity = activityWeakReference.get();

            if (activity != null) {
                try {
                    Location location = result.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    UpdateUI(latitude, longitude);

                    if (location == null) {
                        return;
                    }
                } catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Please make sure you're connected to the internet and your location is on.", Toast.LENGTH_SHORT).show();
                }

                // Create a Toast which displays the new location's coordinates
//                Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
//                        String.valueOf(latitude), String.valueOf(longitude)),
//                        Toast.LENGTH_SHORT).show();

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            dronepage_on_flight activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_dronepage_on_flight);
        locBttn = findViewById(R.id.currLocationBttn);
        receive = findViewById(R.id.txt_receive);

        mapView = findViewById(R.id.mapView_drone);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        try {
            //receive.setText(String.valueOf(sendReceive.getReceive()));

            StringTokenizer tokens = new StringTokenizer(receive.getText().toString(), "[");
            altitude = Integer.parseInt(tokens.nextToken());

            String[] latlngParts = tokens.nextToken().split("\\],\\[");

            for (String ll: latlngParts) {
                String llReplaced = ll.replaceAll("\\[", "").replaceAll("\\]", "");
                String[] llReplacedParts = llReplaced.split(",");

                receivedWaypoints.add(new LatLong(Double.parseDouble(llReplacedParts[0]), Double.parseDouble(llReplacedParts[1]), Boolean.parseBoolean(llReplacedParts[2])));
            }

            receive.setText(receivedWaypoints.toString());

            Toast.makeText(getApplicationContext(), altitude + " " + receivedWaypoints.size(), Toast.LENGTH_SHORT).show();

            directionPoint.add(new LatLng(latitude, longitude));

            IconFactory iconFactory = IconFactory.getInstance(this);
            Icon icon = iconFactory.fromResource(R.drawable.uncheck_marker);

            int index = 0;
            for (LatLong i: receivedWaypoints){
                marker.add(index,new MarkerOptions().title(index + "")
                        .position(new LatLng(receivedWaypoints.get(index).getLatitude(), receivedWaypoints.get(index).getLongitude()))
                        .setIcon(icon));
                mapboxMap.addMarker(marker.get(index));

                directionPoint.add(new LatLng(receivedWaypoints.get(index).getLatitude(), receivedWaypoints.get(index).getLongitude()));
                index++;
            }
        } catch(Exception e){
            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


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

        locBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCamera(new LatLng(latitude, longitude));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/traffic-night-v2"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });

        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(-57.225365, -33.213144)));
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(-54.14164, -33.981818)));
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(-56.990533, -30.583266)));
    }

    public void UpdateUI(double lat, double lon){
        LatLng latLng = new LatLng(lat, lon);

        if(!checker){
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            mapboxMap.addMarker(markerOptions);
            checker = true;
        }
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void animateCamera(@NonNull LatLng location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_directEnable:
                if (mManager != null && mChannel != null) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                    //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    if(wifiManager.isWifiEnabled()){
                        wifiManager.setWifiEnabled(false);
                    }
                    else{
                        wifiManager.setWifiEnabled(true);
                    }
                } else {
                    //Log.e(TAG, "channel or manager is null");
                }
                return true;
            case R.id.action_directDiscover:
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    //Discovery started successfully
                    public void onSuccess() {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(dronepage_on_flight.this);
                        alertDialog.setTitle("Connect to an available device:");

                        View rowList = getLayoutInflater().inflate(R.layout.listview_list, null);
                        listView = rowList.findViewById(R.id.listView);
                        //adapter.notifyDataSetChanged();
                        alertDialog.setView(rowList);
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();

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
                                    }

                                    @Override
                                    public void onFailure(int reason) {
                                        Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

                    //Discovery not started
                    @Override
                    public void onFailure(int reason) {
                    }
                });
            default:
                return super.onOptionsItemSelected(item);
        }
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

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(dronepage_on_flight.this, android.R.layout.simple_list_item_1, deviceNameArray);
                if(adapter != null) listView.setAdapter(adapter);
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

            if(info.groupFormed){
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        mapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    receive.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    public class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private String receive;

        static final int MESSAGE_READ = 1;

        public String getReceive(){
            return receive;
        }


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
    }
}
