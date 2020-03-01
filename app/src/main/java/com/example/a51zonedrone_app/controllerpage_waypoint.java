package com.example.a51zonedrone_app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Classes needed to initialize the map
// Classes needed to handle location permissions
// Classes needed to add the location engine
// Classes needed to add the location component

/**
 * Use the Mapbox Core Library to listen to device location updates
 */
public class controllerpage_waypoint extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {
    // Variables needed to initialize a map
    private MapboxMap mapboxMap;
    private MapView mapView;

    // Variables needed to handle location permissions
    private PermissionsManager permissionsManager;

    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    // Variables needed to listen to location updates
    private controllerpage_waypointLocationCallback callback = new controllerpage_waypointLocationCallback(this);

    //Mini-markers
    List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

    //Waypoints
    private List<LatLng> directionPoint = new ArrayList<>();
    private List<LatLong> allPoints = new ArrayList<>();

    //LatLng
    private static double latitude, longitude;

    //Buttons
    private Button startBtn;
    private ImageButton currentLoc;
    double distanceBetween = 0;


    //Others
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String CIRCLE_LAYER_ID = "CIRCLE_LAYER_ID";
    private float radius = 200;
    private boolean bttn = false;
    private boolean checker = false;
    private int index = 0;
    private List<MarkerOptions> marker = new ArrayList<>();
    private MarkerOptions markerOptions = new MarkerOptions();
    private PolygonOptions poly = new PolygonOptions();
    private LineOptions lineOptions = new LineOptions();
    private LineManager lineManager;

    static final int MESSAGE_READ = 1;

    //For WiFi Direct
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    ServerClass serverClass;
    SendReceive sendReceive;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter = new IntentFilter();
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray; //Used to show device name in ListView
    WifiP2pDevice[] deviceArray; //Used to connect a Device
    private int seekbarval;
    private boolean isWifiP2pEnabled = false;
    private boolean isWifiConnected = false;
    private ListView listView;
    private dronepage_on_flight drone;

    int ctr = 0;


    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void setIsWifiConnected(boolean isWifiConnected) {
        this.isWifiConnected = isWifiConnected;
    }

    private class controllerpage_waypointLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<controllerpage_waypoint> activityWeakReference;

        controllerpage_waypointLocationCallback(controllerpage_waypoint activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            controllerpage_waypoint activity = activityWeakReference.get();

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
            controllerpage_waypoint activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_controllerpage_waypoint);


        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        seekbarval = intent.getIntExtra("seekBarvalue",0);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        startBtn = (Button)findViewById(R.id.startBtn);
        currentLoc = (ImageButton)findViewById(R.id.currentLocationImageButton);

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
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/traffic-night-v2"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);

                        lineManager = new LineManager(mapView, mapboxMap, style);
                    }
                });

        mapboxMap.addOnMapClickListener((LatLng point) -> {
            LatLong lat = new LatLong(point);
            LatLng lat2 = new LatLng(point.getLatitude(), point.getLongitude());

            //Computing the distance between the inputted waypoint and the user's current location
            double distance = convert(latitude, longitude, point.getLatitude(), point.getLongitude());

            if(bttn == false){
                if(distance > radius){
                    Toast.makeText(getApplicationContext(), "Warning! Drone will be out of range.", Toast.LENGTH_SHORT).show();
                }
                else{
                    allPoints.add(lat);
                    allPoints.get(index).setLatlng(lat2);

                    allPoints.get(index).setPass(false);

                    IconFactory iconFactory = IconFactory.getInstance(controllerpage_waypoint.this);
                    Icon icon = iconFactory.fromResource(R.drawable.uncheck_marker);
                    marker.add(index,new MarkerOptions().title(index + ", Status: " + allPoints.get(index).getPass()).position(lat2).setIcon(icon));

                    mapboxMap.addMarker(marker.get(index));
                    index++;
                }
            }
            return true;
        });

        currentLoc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                animateCamera(new LatLng(latitude, longitude));
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                LatLng latlng1 = new LatLng(latitude, longitude);
                directionPoint.add(latlng1);
                Handler handler = new Handler(Looper.getMainLooper());


                if (!isWifiP2pEnabled) {
                    Toast.makeText(getApplicationContext(), "Enable WiFi Direct from the action bar button above or system settings", Toast.LENGTH_LONG).show();
                }
                else {
                    if(!isWifiConnected){
                        Toast.makeText(getApplicationContext(), "Make sure you're connected to another device.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (bttn == false) {
                            for (int i = 0; i < allPoints.size(); i++) {
                                LatLng latlng2 = new LatLng(allPoints.get(i).getLatlng().getLatitude(), allPoints.get(i).getLatlng().getLongitude());
                                directionPoint.add(latlng2);
                            }
                            directionPoint.add(latlng1);

                            lineOptions.withLatLngs(directionPoint).withLineColor("#EE3B3B").withLineWidth(3.0f);
                            lineManager.create(lineOptions);

                            bttn = true;
                            startBtn.setText("CANCEL");
                        } else {
                            alertDialog();
                        }

                        try {
                            String msg = seekbarval + ":" + allPoints.toString();
                            Log.d("TAG3","" + msg);
                            sendReceive.write(msg.getBytes());
                        } catch(Exception e){
                            Toast.makeText(getApplicationContext(), "Please try reconnecting. Reason: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        });
    }

    public void UpdateUI(double lat, double lon){
        LatLng latLng = new LatLng(lat, lon);

        if(checker == false){
            markerOptions.position(latLng);
            mapboxMap.addMarker(markerOptions);
            mapboxMap.addPolygon(poly.addAll(polygonCircleForCoordinate(new LatLng(latitude, longitude), radius)).fillColor(Color.parseColor("#12121212")));
            checker = true;
        }
        if(bttn)
        {

            double SameThreshold = 17.5;
            for (int i = ctr; i < allPoints.size(); i++) {
                LatLng latlng2 = new LatLng(allPoints.get(i).getLatlng().getLatitude(), allPoints.get(i).getLatlng().getLongitude());
                distanceBetween = latLng.distanceTo(latlng2);

                if (distanceBetween < SameThreshold) {
                    mapboxMap.removeMarker(marker.get(i).getMarker());
                    //TODO: Himoag array ang markers unya usaba ni nga part sa code
                    allPoints.get(i).setPass(true);
                    IconFactory iconFactory = IconFactory.getInstance(controllerpage_waypoint.this);
                    Icon icon = iconFactory.fromResource(R.drawable.check_marker);
                    marker.set(i, new MarkerOptions().title(i + ", Status: " + allPoints.get(i).getPass()));
                    marker.get(i).setIcon(icon).position(allPoints.get(i).getLatlng());
                    mapboxMap.addMarker(marker.get(i));
                    ctr = i;
                    break;
                }
            }
        }
    }

    private ArrayList<LatLng> polygonCircleForCoordinate(LatLng location, double radius){
        int degreesBetweenPoints = 8; //45 sides
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = radius / 6371000.0; // earth radius in meters
        double centerLatRadians = location.getLatitude() * Math.PI / 180;
        double centerLonRadians = location.getLongitude() * Math.PI / 180;
        ArrayList<LatLng> polygons = new ArrayList<>(); //array to hold all the points
        for (int index = 0; index < numberOfPoints; index++) {
            double degrees = index * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(Math.sin(centerLatRadians) * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians) * Math.cos(centerLatRadians),
                    Math.cos(distRadians) - Math.sin(centerLatRadians) * Math.sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            polygons.add(point);
        }
        return polygons;
    }

    public double convert(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
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

    private void alertDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Do you wish to cancel the operation?");
        dialog.setTitle("Alert Box");
        dialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        mapboxMap.clear();
                        Toast.makeText(getApplicationContext(),"The drone will now return to the starting point",Toast.LENGTH_LONG).show();
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int reason) {
                                Toast.makeText(getApplicationContext(), "Still Connected", Toast.LENGTH_SHORT).show();
                            }
                        });
                        controllerpage_waypoint.super.finish();
                    }
                });
        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Continuing Edit...",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

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
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(controllerpage_waypoint.this);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                if(adapter != null && listView != null) listView.setAdapter(adapter);
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
                serverClass = new ServerClass();
                serverClass.run();
            }
        }
    };

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

    public class SendReceive extends Thread{
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

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
