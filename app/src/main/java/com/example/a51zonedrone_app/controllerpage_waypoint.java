package com.example.a51zonedrone_app;
//
//import android.Manifest;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.pm.PackageManager;
//import android.content.res.Resources;
//import android.graphics.Color;
//import android.location.Location;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.DialogFragment;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptor;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MapStyleOptions;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.maps.android.SphericalUtil;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
//
//import static java.lang.Math.asin;
//import static java.lang.Math.atan2;
//import static java.lang.Math.cos;
//import static java.lang.Math.incrementExact;
//import static java.lang.Math.sin;
//import static java.lang.Math.sqrt;
//
//public class controllerpage_waypoint extends AppCompatActivity implements OnMapReadyCallback {
//
//    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;
//
//    //The Map
//    private GoogleMap mMap;
//    private double latitude, longitude;
//    private LatLng loc;
//
//    //GoogleApiClient
//    private FusedLocationProviderClient fusedLocationProviderClient;
//
//    //Views
//    private Button startBtn;
//    private TextView textView;
//
//    //Waypoints
//    private int radius = 200, rad = 10;
//    private List<LatLong> allPoints = new ArrayList<LatLong>();
//
//    //Others
//    private Marker currentLocationMarker;
//    private Location currentLocation;
//    private boolean firstTimeFlag = true;
//    private boolean bttn = false;
//    private int index = 0;
//    private int count = 0;
//    private List<LatLng> directionPoint = new ArrayList<>();
//    private AsyncTask<?, ?, ?> runningTask;
//
//    //Marker
//    private BitmapDescriptor bm;
//    private List<MarkerOptions> marker = new ArrayList<>();
//    private Marker myMarker;
//
//    //Routes
//    PolylineOptions rectLine = new PolylineOptions();
//
//    private final View.OnClickListener clickListener = view -> {
//        if (view.getId() == R.id.currentLocationImageButton && mMap != null && currentLocation != null)
//            animateCamera(currentLocation);
//    };
//
//    //Method for getting the location live
//    private final LocationCallback mLocationCallback = new LocationCallback() {
//
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//
//            if (locationResult.getLastLocation() == null)
//                return;
//            currentLocation = locationResult.getLastLocation();
//            for(Location loc : locationResult.getLocations()){
//                if (firstTimeFlag && mMap != null) {
//                    latitude = loc.getLatitude();
//                    longitude = loc.getLongitude();
//                    textView.setText(latitude + ", " + longitude);
//
//                    animateCamera(currentLocation);
//                    UpdateUI();
//                    firstTimeFlag = false;
//                }
//            }
//
//            showMarker(currentLocation);
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_controllerpage_waypoint);
//        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        supportMapFragment.getMapAsync(this);
//        findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);
//        startBtn = (Button) findViewById(R.id.startBtn);
//        textView = (TextView)findViewById(R.id.txt_LatLong);
//
//        textView.setText("");
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        this.mMap = googleMap;
//
//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = mMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            this, R.raw.map_style));
//
//            if (!success) {
//                Log.e("SUC", "Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e("ERR", "Can't find style. Error: ", e);
//        }
//
//        //Method when the user clicks the map to set waypoints
//        mMap.setOnMapClickListener(point -> {
//            LatLong latlong = new LatLong(point);
//            LatLng lat = new LatLng(point.latitude, point.longitude);
//
//            //Computing the distance between the inputted waypoint and the user's current location
//            double distance = convert(latitude, longitude, point.latitude, point.longitude);
//
//            //Will only activate when the user hasn't pressed the "Start" button
//            if(bttn == false){
//                //If the distance of the waypoint exceeds the 200-meters radial limit
//                if(distance > radius){
//                    Toast.makeText(getApplicationContext(), "Warning! Drone will be out of range.", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    //Create marker
//                    allPoints.add(latlong);
//                    allPoints.get(index).setLatlng(lat);
//                    bm = BitmapDescriptorFactory.fromResource(R.drawable.uncheck_marker);
//                    marker.add(index, new MarkerOptions().position(point).title(count + ": " + point.latitude + ", " + point.longitude).draggable(true).icon(bm));
//                    CircleOptions circle = new CircleOptions().center(point).radius(rad)
//                            .strokeWidth(3)
//                            .strokeColor(getResources().getColor(R.color.colorRad))
//                            .fillColor(getResources().getColor(R.color.colorRad));
//
//                    myMarker = mMap.addMarker(marker.get(index));
//                    mMap.addCircle(circle);
//
//                    count++;
//                    index++;
//                }
//            }
//
//        });
//
//        //When the user presses the "Start" button
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                double SameThreshold = 10;
//                LatLng latlng1 = new LatLng(latitude, longitude);
//                directionPoint.add(latlng1);
//                Handler handler = new Handler(Looper.getMainLooper());
//
//                if(bttn == false){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for(int i = 0 ; i < allPoints.size() ; i++)
//                            {
//                                LatLng latlng2 = new LatLng(allPoints.get(i).getLatlng().latitude, allPoints.get(i).getLatlng().longitude);
//                                double distanceBetween = SphericalUtil.computeDistanceBetween(latlng1, latlng2);
//
//                                directionPoint.add(latlng2);
//                                if (distanceBetween < SameThreshold){
//                                    //TODO: Himoag array ang markers unya usaba ni nga part sa code
//                                    bm = BitmapDescriptorFactory.fromResource(R.drawable.check_marker);
//                                    //MarkerOptions marker = new MarkerOptions().position(allPoints.get(i).getLatlng()).title(count + "").icon(bm);
//                                    marker.get(i).position(allPoints.get(i).getLatlng()).title(allPoints.get(i).getLatlng().latitude + ", " + allPoints.get(i).getLatlng().longitude).icon(bm);
//
//                                    try{ myMarker = mMap.addMarker(marker.get(i)); }
//                                    catch (Exception e){ }
//
//                                    allPoints.get(i).setPass(true);
//                                }
//
//                            }
//
//                            directionPoint.add(latlng1);
//                            for (int i = 0; i < directionPoint.size(); i++) {
//                                rectLine.add(directionPoint.get(i));
//                            }
//
//                            try { mMap.addPolyline(rectLine); }
//                            catch (Exception e){ }
//
//                            handler.postDelayed(this, 1000);
//                        }
//                    }).start();
//
//                    bttn = true;
//                    startBtn.setText("CANCEL");
//                }
//                else{
////                    bttn = false;
////                    startBtn.setText("START");
////                    textView.setText("NOT STARTED");
//                    alertDialog();
//                }
//            }
//        });
//    }
//
//    private void UpdateUI(){
//        //Setting the LatLong
//        LatLng latLng = new LatLng(latitude, longitude);
//
//        mMap.setMinZoomPreference(14.0f);
//        mMap.setMaxZoomPreference(100.0f);
//
//        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radius)
//                .strokeWidth(3)
//                .strokeColor(getResources().getColor(R.color.colorRange))
//                .fillColor(getResources().getColor(R.color.colorRange));
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
//        mMap.addCircle(circleOptions);
//
//        rectLine =  new PolylineOptions().width(20).color(Color.RED);
//    }
//
//    //Requesting on the user for permission. Once accepted, it will throw location updates
//    private void startCurrentLocationUpdates() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(3000);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(controllerpage_waypoint.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//                return;
//            }
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
//    }
//
//    private boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
//        if (ConnectionResult.SUCCESS == status)
//            return true;
//        else {
//            if (googleApiAvailability.isUserResolvableError(status))
//                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
//        }
//        return false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
//                Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
//            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                startCurrentLocationUpdates();
//        }
//    }
//
//    private void animateCamera(@NonNull Location location) {
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
//    }
//
//    @NonNull
//    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
//        return new CameraPosition.Builder().target(latLng).zoom(16).build();
//    }
//
//    private void showMarker(@NonNull Location currentLocation) {
//        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        if (currentLocationMarker == null)
//            currentLocationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng)
//                    .title(currentLocation.getLatitude() + ", " + currentLocation.getLongitude()));
//        else {
//            AnimatingMarkerHelper.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());
//        }
//    }
//
//    //Converting the radius of the Earth to meters
//    public double convert(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
//        double R = 6378.137; // Radius of earth in KM
//        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
//        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
//        double a = sin(dLat/2) * sin(dLat/2) +
//                cos(lat1 * Math.PI / 180) * cos(lat2 * Math.PI / 180) *
//                        sin(dLon/2) * sin(dLon/2);
//        double c = 2 * atan2(sqrt(a), sqrt(1-a));
//        double d = R * c;
//        return d * 1000; // meters
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (fusedLocationProviderClient != null)
//            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isGooglePlayServicesAvailable()) {
//            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//            startCurrentLocationUpdates();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        fusedLocationProviderClient = null;
//        mMap = null;
//    }
//
//    private void alertDialog() {
//        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
//        dialog.setMessage("Do you wish to cancel the operation?");
//        dialog.setTitle("Alert Box");
//        dialog.setPositiveButton("YES",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//                        mMap.clear();
//                        Toast.makeText(getApplicationContext(),"The drone will now return to the starting point",Toast.LENGTH_LONG).show();
//                        controllerpage_waypoint.super.finish();
//                    }
//                });
//        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(),"Continuing Edit...",Toast.LENGTH_SHORT).show();
//            }
//        });
//        AlertDialog alertDialog=dialog.create();
//        alertDialog.show();
//    }
//}




import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.os.Bundle;
// Classes needed to initialize the map
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
// Classes needed to handle location permissions
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// Classes needed to add the location engine
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import java.lang.ref.WeakReference;
// Classes needed to add the location component
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

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
    private PolylineOptions polyl = new PolylineOptions();
    private LineOptions lineOptions = new LineOptions();
    private LineManager lineManager;

    //For WiFi Direct
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray; //Used to show device name in ListView
    WifiP2pDevice[] deviceArray; //Used to connect a Device
    private int seekbarval;
    private boolean isWifiP2pEnabled = false;
    private boolean isWifiConnected = false;
    static final int MESSAGE_READ = 1;

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
                Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
                        String.valueOf(latitude), String.valueOf(longitude)),
                        Toast.LENGTH_SHORT).show();

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

//                    symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                            Point.fromLngLat(point.getLongitude(), point.getLatitude())));
//
//                    mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/traffic-night-v2")
//                            // Add the SymbolLayer icon image to the map style
//                            .withImage(ICON_ID, BitmapFactory.decodeResource(
//                                    controllerpage_waypoint.this.getResources(), R.drawable.uncheck_marker))
//                            .withSource(new GeoJsonSource(SOURCE_ID,
//                                    FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
//                            // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
//                            // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
//                            // the coordinate point. This is offset is not always needed and is dependent on the image
//                            // that you use for the SymbolLayer icon.
//                            .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
//                                    .withProperties(PropertyFactory.iconImage(ICON_ID),
//                                            iconAllowOverlap(true),
//                                            iconOffset(new Float[] {0f, -9f}))
//                            ), new Style.OnStyleLoaded() {
//                        @Override
//                        public void onStyleLoaded(@NonNull Style style) {
//                            // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.
//                        }
//                    });

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
                double SameThreshold = 100;
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
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    handler.postDelayed(this, 1000);
//                                }
//                            }).start();

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
//                            bttn = false;
//                            startBtn.setText("START");
                            alertDialog();
                        }
                    }
                }
            }
        });
    }

    public void UpdateUI(double lat, double lon){
        LatLng latLng = new LatLng(lat, lon);

//        poly.addAll(polygonCircleForCoordinate(latLng, radius)).fillColor(Color.parseColor("#12121212"));
//        mapboxMap.addPolygon(poly);

        //mapboxMap.removePolygon(poly.getPolygon());

        //mapboxMap.addPolyline(polyl.addAll(directionPoint).width(5).color(Color.RED));

        if(checker == false){
            markerOptions.position(latLng);
            mapboxMap.addMarker(markerOptions);
            mapboxMap.addPolygon(poly.addAll(polygonCircleForCoordinate(new LatLng(latitude, longitude), radius)).fillColor(Color.parseColor("#12121212")));
            checker = true;
        }
        if(bttn)
        {
            double SameThreshold = 100;
            for (int i = 0; i < allPoints.size(); i++) {
                //Toast.makeText(getApplicationContext(), "NISUD" + i, Toast.LENGTH_SHORT).show();
                Log.d("TAG1","NISUD" + i);
                LatLng latlng2 = new LatLng(allPoints.get(i).getLatlng().getLatitude(), allPoints.get(i).getLatlng().getLongitude());
                double distanceBetween = latLng.distanceTo(latlng2);
                if (distanceBetween > SameThreshold) {
                    Log.d("TAG2","wew" + i);
                    //TODO: Himoag array ang markers unya usaba ni nga part sa code


                    allPoints.get(i).setPass(true);
                    IconFactory iconFactory = IconFactory.getInstance(controllerpage_waypoint.this);
                    Icon icon = iconFactory.fromResource(R.drawable.check_marker);
                    marker.get(i).setIcon(icon);
                    marker.set(i, new MarkerOptions().title(i + ", Status: " + allPoints.get(i).getPass()).setIcon(icon));
//                            bm = BitmapDescriptorFactory.fromResource(R.drawable.check_marker);
//                            //MarkerOptions marker = new MarkerOptions().position(allPoints.get(i).getLatlng()).title(count + "").icon(bm);
//                            marker.get(i).position(allPoints.get(i).getLatlng()).title(allPoints.get(i).getLatlng().latitude + ", " + allPoints.get(i).getLatlng().longitude).icon(bm);
//
//                            try{ myMarker = mMap.addMarker(marker.get(i)); }
//                            catch (Exception e){ }
//
//                            allPoints.get(i).setPass(true);
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

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            if(info.groupFormed && info.isGroupOwner){
                Log.d("TAG", "CONNECTED");
                serverClass = new ServerClass();
                serverClass.start();
            }
            else if(info.groupFormed){
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

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
                //listView.setAdapter(adapter);
            }

            if(peers.size() == 0){
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                return;
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
                        //handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
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
