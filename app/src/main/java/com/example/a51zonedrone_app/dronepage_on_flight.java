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
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class dronepage_on_flight extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener,
        SensorEventListener, ArduinoListener {

    //altitude
    private float altitude = 0;
    private float target_altitude = 0;
    private float alt_prev_error;
    private float alt_pid = 0;
    private float alt_error = 0;

    //yaw
    private int whereToYawn = 0;

    private TextView receive;
    private TextView tv;
//    ClientClass clientClass;
//    SendReceive sendReceive;
    WifiManager wifiManager;
    WifiP2pManager mManager;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter = new IntentFilter();
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private boolean isWifiP2pEnabled = false;
    private boolean isWifiConnected = false;
    private String receivedString;
    static Handler handler;

    // System sensor manager instance.
    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private float[] or = new float[3];

    //Instance of Arduino
    //Uno R3 VID 0x2341&PID 0x0043
    //Nano VID 0x1A86
    private Arduino arduino;

    //Averaging the sensors
    private int count;
    private float or1,or2,or3;
    private float aveOr1,aveOr2,aveOr3;
    private int limit;

    //PID of sensors
    float elapsedTime, time, timePrev;
    private int pwm1,pwm2,pwm3,pwm4;
    private float desire_angle;
    private float kp;//3.55
    private float ki;//0.005
    private float kd;//2.05
    private float throttle;

    //PID
    private float roll_previous_error,pitch_previous_error,yaw_previous_error;

    String pwms;

    private int i = 0;

    private int wifiDirect = 0;

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
    private List<LatLong> receivedWaypoints = new ArrayList<>();
    private boolean checker = false;
    private List<MarkerOptions> marker = new ArrayList<>();
    private MarkerOptions markerOptions = new MarkerOptions();
    private List<LatLng> directionPoint = new ArrayList<>();
    private LineOptions lineOptions = new LineOptions();
    private LineManager lineManager;
    static public Server_Side_Thread serverSideThread;
    static public Client_Thread clientThread;
    static public SendReceiveThread sendReceiveThread;


    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void setIsWifiConnected(boolean isWifiConnected) {
        this.isWifiConnected = isWifiConnected;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
        if(wifiDirect == 1) {
            int i = 0;
            StringTokenizer tokens = new StringTokenizer(receivedString, ":");
            target_altitude = Integer.parseInt(tokens.nextToken());

            String[] latlngParts = tokens.nextToken().split("\\],\\[");
            String llReplaced = latlngParts[0].replaceAll("\\[", "").replaceAll("\\]", "");
            String[] llReplacedParts = llReplaced.split(", ");
            for (String ll : llReplacedParts) {
                String[] tok = llReplacedParts[i].split(",");
                i++;
                receivedWaypoints.add(new LatLong(Double.parseDouble(tok[0]), Double.parseDouble(tok[1]), Boolean.parseBoolean(tok[2])));
            }

            directionPoint.add(new LatLng(latitude, longitude));

            IconFactory iconFactory = IconFactory.getInstance(this);
            Icon icon = iconFactory.fromResource(R.drawable.uncheck_marker);

            for (int index = 0; index < receivedWaypoints.size(); index++) {
                marker.add(index, new MarkerOptions().title(index + "")
                        .position(new LatLng(receivedWaypoints.get(index).getLatitude(), receivedWaypoints.get(index).getLongitude()))
                        .setIcon(icon));
                mapboxMap.addMarker(marker.get(index));

                directionPoint.add(new LatLng(receivedWaypoints.get(index).getLatitude(), receivedWaypoints.get(index).getLongitude()));
            }

            directionPoint.add(new LatLng(latitude, longitude));

            lineOptions.withLatLngs(directionPoint).withLineColor("#FFFA8D").withLineWidth(3.0f);
            lineManager.create(lineOptions);
            wifiDirect = 2;
        }
        updateOrientationAngles();
    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which iwww.wis needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.
        /*First calculate the error between the desired angle and
         *the real measured angle*/
        or = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        if (count < limit) {
            //ROLL 4.70 to -4.70
            //PITCH 2.34 to -2.34
            or1 = (float)or[0];
            or2 = (float)or[1];
            or3 = (float)or[2];
            aveOr1 += or1;
            aveOr2 += or2;
            aveOr3 += or3;
            count++;
        } else {
            aveOr1 /= limit;
            aveOr2 /= limit;
            aveOr3 /= limit;
            aveOr1 = Math.round(map(aveOr1,0,Math.PI,0,180)+180);
            aveOr2 = Math.round(map(aveOr2,-Math.PI/2,Math.PI/2,-90,90));
            aveOr3 =Math.round(map(aveOr3,-Math.PI,Math.PI,-180,180));
            altPID(time,altitude,target_altitude);
            int[] allPWM = PWM(time, aveOr2, aveOr3,aveOr1,whereToYawn);
            pwm1 = allPWM[0];
            pwm2 = allPWM[1];
            pwm3 = allPWM[2];
            pwm4 = allPWM[3];
                /*First calculate the error between the desired angle and
                  the real measured angle*/
            aveOr1 = 0;
            aveOr2 = 0;
            aveOr3 = 0;
            if (wifiDirect == 2) {
                pwms = Integer.toString(pwm1) + Integer.toString(pwm2) + Integer.toString(pwm3) + Integer.toString(pwm4);
                arduino.send(pwms.getBytes());
            }
            count = 0;
        }
        // "mOrientationAngles" now has up-to-date information.
    }
    private void altPID(float time,float altitude,float target_alt){
        kp = (float) 10.80;
        ki = (float) 0.048;
        kd = (float) 2.5;
        float alt_p=0,alt_i=0,alt_d=0;
        float timePrev = time;
        float time1 = Calendar.getInstance().getTimeInMillis();

        elapsedTime = (time1 - timePrev)/1000000000;
        elapsedTime = (elapsedTime/10000);

        alt_error = target_alt - altitude;
        if(alt_error<.5&&alt_error>-.5)
        {
            alt_error = 0;
        }
        alt_p = kp*alt_error;
        if(-3 < alt_error && alt_error < 3){alt_i += ki*alt_error;}
        alt_d = kd*((alt_error - alt_prev_error)/elapsedTime);
        alt_prev_error = alt_error;
        alt_pid= (int) (alt_p+alt_i+alt_d);

        if(alt_pid < -1000)
        {
            alt_pid=-1000;
        }
        if(alt_pid > 1000)
        {
            alt_pid=1000;
        }
    }
    private int[] PWM(float time,float pitch,float roll,float yaw,int whereToYawn)
    {
        kp = (float) 3.55;
        ki = (float) 0.005;
        kd = (float) 2.05;
        float pitch_pid_i = 0,roll_pid_i = 0,yaw_pid_i = 0;
        float timePrev = time;
        float time1 = Calendar.getInstance().getTimeInMillis();

        //float elapsedTime = (time1 - timePrev)/1000;
        float elapsedTime = (time1 - timePrev)/1000000000;
        elapsedTime = elapsedTime/10000;

        float pitch_error = 0;
        //float pitch_error = 0;
        float roll_error = roll - desire_angle;
        //float roll_error = 0;
        //kailangan og desire angle
        float yaw_error = yaw - whereToYawn;
        if(pitch<5&&pitch>-5)
        {
            pitch_error = 0;
        }
        if(roll<5&&roll>-5)
        {
            roll_error = 0;
        }
        if(yaw_error<5 && yaw_error>-5)
        {
            yaw_error = 0;
            pitch_error= pitch - 20;
        }
        else {
            pitch_error = pitch - desire_angle;
        }

        //PID_PROPRTIONAL
        float pitch_pid_p = kp*pitch_error;
        float roll_pid_p = kp*roll_error;
        float yaw_pid_p = kp*yaw_error;

        //PID_INTEGRAL
        if(-10 < pitch_error && pitch_error < 10)
        {
            pitch_pid_i += ki*pitch_error;
        }
        if(-10 < roll_error && roll_error < 10)
        {
            roll_pid_i += ki*roll_error;
        }
        if(-10 < yaw_error && yaw_error < 10)
        {
            yaw_pid_i += ki*yaw_error;
        }
        if(-10 < yaw_error && yaw_error < 10)
        {
            yaw_pid_i += ki*yaw_error;
        }

        //PID_DIFFERENTIAL
        float pitch_pid_d = kd*((pitch_error - pitch_previous_error)/elapsedTime);
        float roll_pid_d = kd*((roll_error - roll_previous_error)/elapsedTime);
        float yaw_pid_d = kd*((yaw_error - yaw_previous_error)/elapsedTime);

        //all PID summation
        float pitch_PID = pitch_pid_p + pitch_pid_i + pitch_pid_d;
        float roll_PID = roll_pid_p + roll_pid_i + roll_pid_d;
        float yaw_PID = yaw_pid_p + yaw_pid_i + yaw_pid_d;

        if(pitch_PID < -1000)
        {
            pitch_PID = -1000;
        }
        else if(pitch_PID > 1000)
        {
            pitch_PID = 1000;
        }
        if(roll_PID < -1000)
        {
            roll_PID = -1000;
        }
        else if(roll_PID > 1000)
        {
            roll_PID = 1000;
        }
        if(yaw_PID < -1000)
        {
            yaw_PID = -1000;
        }
        else if(yaw_PID > 1000)
        {
            yaw_PID = 1000;
        }
        float pwm1 = throttle + pitch_PID - roll_PID /*- yaw_PID+alt_pid*/;
        float pwm2 = throttle - pitch_PID - roll_PID /*+ yaw_PID+alt_pid*/;
        float pwm3 = throttle - pitch_PID + roll_PID /*- yaw_PID+alt_pid*/;
        float pwm4 = throttle + pitch_PID + roll_PID /*+ yaw_PID+alt_pid*/;
        if(pwm1 < 1000)
        {
            pwm1 = 1000;
        }
        if(pwm1 > 2000)
        {
            pwm1 = 2000;
        }
        if(pwm2 < 1000)
        {
            pwm2 = 1000;
        }
        if(pwm2 > 2000)
        {
            pwm2 = 2000;
        }
        if(pwm3 < 1000)
        {
            pwm3 = 1000;
        }
        if(pwm3 > 2000)
        {
            pwm3 = 2000;
        }
        if(pwm4 < 1000)
        {
            pwm4 = 1000;
        }
        if(pwm4 > 2000)
        {
            pwm4 = 2000;
        }
        pwm1 = (float) map(pwm1,1000,2000,1000,1987);
        pwm2 = (float) map(pwm2,1000,2000,1000,1977);
        pwm3 = (float) map(pwm3,1000,2000,1000,1994);
        yaw_previous_error = yaw_error;
        pitch_previous_error = pitch_error;
        roll_previous_error = roll_error;
        return (new int[]{(int) pwm1,(int)pwm2,(int)pwm3,(int)pwm4});
    }

    private double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        display("Arduino attached!");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        display("Arduino detached");
    }

    @Override
    public void onArduinoMessage(byte[] bytes)
    {
        altitude=Float.parseFloat(new String(bytes));
        try {
            display("> "+new String(bytes,"UTF-8") +"PID:"+alt_pid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }}

    public void display(String message) {

        runOnUiThread(() -> tv.setText(message + "\n"));
    }

    @Override
    public void onArduinoOpened() {
        Toast.makeText(this,"onArduino ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUsbPermissionDenied() {
        // Permission denied, display popup then
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
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

        //Instantiate Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        //Instantiate Arduino
        arduino = new Arduino(this);
        arduino.addVendorId(0x2341);
        arduino.addVendorId (0x1A86);
        arduino.setBaudRate(9600);

        elapsedTime = timePrev = 0;
        aveOr1 = aveOr2 = aveOr3 = 0;
        or1 = or2 = or3 = 0;
        count = 0;
        limit = 3;

        //PID
        //pitch_pid_d = pitch_pid_i = pitch_pid_d = roll_pid_p =roll_pid_i = roll_pid_d = 0;
        //roll_PID = roll_error = roll_previous_error = pitch_PID = pitch_error = pitch_previous_error = 0;
        time = 0;
        kp = (float) 3.55;
        ki = (float) 0.005;
        kd = (float) 2.05;
        pwm1 = pwm2 = pwm3 = pwm4 = 1000;
        desire_angle = 0;
        roll_previous_error = 0;
        pitch_previous_error = 0;
        yaw_previous_error = 0;
        throttle = 1200;
        pwms = "1000100010001000";

        setContentView(R.layout.activity_dronepage_on_flight);
        locBttn = findViewById(R.id.currLocationBttn);
        receive = findViewById(R.id.txt_receive);
        tv = findViewById(R.id.tv);

        mapView = findViewById(R.id.mapView_drone);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        //This class provides the API for managing Wi-Fi peer-to-peer connectivity
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        //A channel that connects the application to the Wi-Fi p2p framework
        //Most p2p operations require a Channel as an argument
        mChannel = mManager.initialize(this, getMainLooper(), null);

        //mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    byte[] readBuff = (byte[]) msg.obj;
                    String temp = new String(readBuff, 0, msg.arg1);
                    receivedString = temp;
                    receive.setText(receivedString);
                    if(wifiDirect == 0 && temp != null) {
                        wifiDirect = 1;
                    }
                    else if(wifiDirect == 2 && receivedString == "stop")
                    {
                        wifiDirect = 3;
                        receive.setText(receivedString);
                    }
                    temp = null;
                }
                return true;
            }
        });

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

                        lineManager = new LineManager(mapView, mapboxMap, style);
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
//        if(wifiDirect)
//        {
//            double SameThreshold = 17.5;
//            for (int i = ctr; i < allPoints.size(); i++) {
//                LatLng latlng2 = new LatLng(allPoints.get(i).getLatlng().getLatitude(), allPoints.get(i).getLatlng().getLongitude());
//                distanceBetween = latLng.distanceTo(latlng2);
//                Toast.makeText(getApplicationContext(),"!"+distanceBetween,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(),"@"+LatLong.computeAngleBetween(latLng,latlng2),Toast.LENGTH_SHORT).show();
//
//                if (distanceBetween < SameThreshold) {
//                    mapboxMap.removeMarker(marker.get(i).getMarker());
//                    //TODO: Himoag array ang markers unya usaba ni nga part sa code
//                    allPoints.get(i).setPass(true);
//                    IconFactory iconFactory = IconFactory.getInstance(controllerpage_waypoint.this);
//                    Icon icon = iconFactory.fromResource(R.drawable.check_marker);
//                    marker.set(i, new MarkerOptions().title(i + ", Status: " + allPoints.get(i).getPass()));
//                    marker.get(i).setIcon(icon).position(allPoints.get(i).getLatlng());
//                    mapboxMap.addMarker(marker.get(i));
//                    ctr = i;
//                    break;
//                }
//            }
//        }
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
            InetAddress inetAddress = info.groupOwnerAddress;
            if (info.groupFormed && info.isGroupOwner) {
                //constate.setText("Host");
                Log.d("Reached", " Here 1");
                serverSideThread = new Server_Side_Thread();
                serverSideThread.start();
                //Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                //startActivity(intent);
            } else if (info.groupFormed) {
                //constate.setText("Client");
                Log.d("Reached", " Here 2");
                clientThread = new Client_Thread(inetAddress);
                clientThread.start();
                //Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                //startActivity(intent);
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);
        mapView.onResume();
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            }
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sensorManager.registerListener((SensorEventListener) this, magneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        mapView.onPause();
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        arduino.setArduinoListener(this);
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
        controllerpage_waypoint.sendReceiveThread = null;
        controllerpage_waypoint.clientThread = null;
        controllerpage_waypoint.serverSideThread = null;
        mapView.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(@NonNull Message msg) {
//            switch(msg.what){
//                case MESSAGE_READ:
//                    byte[] readBuff = (byte[]) msg.obj;
//                    String tempMsg = new String(readBuff, 0, msg.arg1);
//                    receivedString = tempMsg;
//                    receive.setText(receivedString);
//                    if(tempMsg != null) {
//                        wifiDirect = 1;
//                    }
//                    else if(wifiDirect == 2 && tempMsg != null)
//                    {
//                        wifiDirect = 3;
//                    }
//                    tempMsg = null;
//                    break;
//            }
//            return true;
//        }
//    });

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

//    public class ClientClass extends Thread{
//        Socket socket;
//        String hostAdd;
//
//        public ClientClass(InetAddress hostAddress){
//            hostAdd = hostAddress.getHostAddress();
//            socket = new Socket();
//        }
//
//        @Override
//        public void run() {
//            try {
//                socket.connect(new InetSocketAddress(hostAdd, 8888),500);
//                sendReceive = new SendReceive(socket);
//                sendReceive.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public class SendReceive extends Thread{
//        private Socket socket;
//        private InputStream inputStream;
//        private OutputStream outputStream;
//        private String receive;
//
//        static final int MESSAGE_READ = 1;
//
//        public String getReceive(){
//            return receive;
//        }
//
//
//        public SendReceive(Socket skt){
//            socket = skt;
//            try {
//                inputStream = socket.getInputStream();
//                outputStream = socket.getOutputStream();
//
//                if(outputStream == null){
//                    outputStream = socket.getOutputStream();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            byte[] buffer = new byte[1024];
//            int bytes;
//
//            while(socket != null){
//                try {
//                    bytes = inputStream.read(buffer);
//                    if(bytes > 0){
//                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
