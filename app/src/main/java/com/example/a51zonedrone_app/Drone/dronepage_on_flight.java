package com.example.a51zonedrone_app.Drone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a51zonedrone_app.Controller.controllerpage_waypoint;
import com.example.a51zonedrone_app.LatLong;
import com.example.a51zonedrone_app.R;
import com.example.a51zonedrone_app.WiFiDirect.Client_Thread;
import com.example.a51zonedrone_app.WiFiDirect.SendReceiveThread;
import com.example.a51zonedrone_app.WiFiDirect.Server_Side_Thread;
import com.example.a51zonedrone_app.WiFiDirect.WifiDirectBroadcastReceiver;
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

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
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

    //yaw
    private int whereToYawn = 0;

    //TextView
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv6;
    private TextView tv5;
    private TextView tv7;
    private TextView receive;
    private TextView tv;
    private ScrollView sv;


    //EditText
    private EditText pitch_kp;
    private EditText pitch_ki;
    private EditText pitch_kd;
    private EditText yaw_kp;
    private EditText yaw_ki;
    private EditText yaw_kd;
    private EditText roll_kp;
    private EditText roll_ki;
    private EditText roll_kd;
    private EditText alt_kp;
    private EditText alt_ki;
    private EditText alt_kd;
    private EditText throttle;

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
    public static Handler handler;
    public static String connectedDeviceName="";

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
    private float pitch,roll,yawn;
    private int limit;

    //PID of sensors
    float elapsedTime, time, timePrev;
    private int pwm1,pwm2,pwm3,pwm4;
//    private float desire_angle;
//    private float kp;//3.55
//    private float ki;//0.005
//    private float kd;//2.05
//    private float throttle;
    private boolean developerOptions = false;


    //PID
    private float roll_previous_error,pitch_previous_error,yaw_previous_error,alt_prev_error;

    String pwms;

    private int i = 0;

    private int wifiDirect = 0;
    private boolean clicked = false;

    String[] deviceNameArray; //Used to show device name in ListView
    WifiP2pDevice[] deviceArray; //Used to connect a Device
    static WifiP2pDevice wifiP2pDevice;
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
        LatLng latLng = new LatLng(latitude, longitude);
        if(wifiDirect == 1) {
            int count = 0;
            StringTokenizer tokens = new StringTokenizer(receivedString, ":");
            target_altitude = Integer.parseInt(tokens.nextToken());

            String[] latlngParts = tokens.nextToken().split("\\],\\[");
            String llReplaced = latlngParts[0].replaceAll("\\[", "").replaceAll("\\]", "");
            String[] llReplacedParts = llReplaced.split(", ");
            for (String ll : llReplacedParts) {
                String[] tok = llReplacedParts[count].split(",");
                count++;
                receivedWaypoints.add(new LatLong(Double.parseDouble(tok[0]), Double.parseDouble(tok[1]), false));
            }

            directionPoint.add(latLng);

            markerOptions.position(latLng);
            mapboxMap.addMarker(markerOptions);

            IconFactory iconFactory = IconFactory.getInstance(this);
            Icon icon = iconFactory.fromResource(R.drawable.uncheck_marker);

            for (int index = 0; index < receivedWaypoints.size(); index++) {
                marker.add(index, new MarkerOptions().title(index + "")
                        .position(new LatLng(receivedWaypoints.get(index).getLatitude(), receivedWaypoints.get(index).getLongitude()))
                        .setIcon(icon));
                mapboxMap.addMarker(marker.get(index));

                directionPoint.add(new LatLng(receivedWaypoints.get(index).getLatitude(), receivedWaypoints.get(index).getLongitude()));
            }

            directionPoint.add(latLng);

            lineOptions.withLatLngs(directionPoint).withLineColor("#FFFA8D").withLineWidth(3.0f);
            lineManager.create(lineOptions);
            wifiDirect = 2;
        }
        if (wifiDirect ==  2 || clicked ) {
            double distanceBetween = 0;
            //10 meters
            double SameThreshold = 10;
            LatLng latlng2 = new LatLng(receivedWaypoints.get(i).getLatlng().getLatitude(), receivedWaypoints.get(i).getLatlng().getLongitude());
            distanceBetween = latLng.distanceTo(latlng2);
            whereToYawn = (int)LatLong.computeAngleBetween(latLng,latlng2);

            if (distanceBetween < SameThreshold) {
                mapboxMap.removeMarker(marker.get(i).getMarker());
                //TODO: Himoag array ang markers unya usaba ni nga part sa code
                receivedWaypoints.get(i).setPass(true);
                IconFactory iconFactory = IconFactory.getInstance(dronepage_on_flight.this);
                Icon icon = iconFactory.fromResource(R.drawable.check_marker);
                receivedWaypoints.get(i).setPass(true);
                marker.set(i, new MarkerOptions().title(i + ", Status: " + receivedWaypoints.get(i).getPass()));
                marker.get(i).setIcon(icon).position(receivedWaypoints.get(i).getLatlng());
                mapboxMap.addMarker(marker.get(i));
                i++;
            }
        }
        updateOrientationAngles();
    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which iwww.wis needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date informatio /*First calculate the error between the desired angle and/*the real measured angle*/
        or = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        if (count < limit) {
            //ROLL 4.70 to -4.70
            //PITCH 2.34 to -2.34
            or1 = (float)or[0];
            or2 = (float)or[1];
            or3 = (float)or[2];
            yawn += or1;
            pitch += or2;
            roll += or3;
            count++;
        } else {
            yawn /= limit;
            pitch /= limit;
            roll /= limit;
            yawn = Math.round(map(yawn,0,Math.PI,0,180)+180);
            pitch = Math.round(map(pitch,-Math.PI/2,Math.PI/2,-90,90));
            roll =Math.round(map(roll,-Math.PI,Math.PI,-180,180));
            int[] allPWM = PWM(time, pitch, roll,yawn);
            if(wifiDirect ==  2|| clicked) {
                pwm1 = allPWM[0];
                pwm2 = allPWM[1];
                pwm3 = allPWM[2];
                pwm4 = allPWM[3];
            }
            tv1.setText("Front Left PWM = "+pwm1);
            tv2.setText("Back Left PWM = "+pwm2);
            tv3.setText("Back Right PWM = "+pwm3);
            tv4.setText("Front Right PWM = "+pwm4);
            tv5.setText(roll+":Roll");
            tv6.setText(pitch+":Pitch");
            tv7.setText(yawn+":Azimuth");
                /*First calculate the error between the desired angle and
                  the real measured angle*/
            yawn = 0;
            pitch = 0;
            roll = 0;
            if (wifiDirect ==  2 || clicked) {
                pwms = Integer.toString(pwm1) + Integer.toString(pwm2) + Integer.toString(pwm3) + Integer.toString(pwm4);
                arduino.send(pwms.getBytes());
            }
            count = 0;
        }
        // "mOrientationAngles" now has up-to-date information.
    }

    private int[] PWM(float time,float pitch,float roll,float yaw)
    {
        float p_kp = Float.parseFloat(pitch_kp.getText().toString());
        float p_ki = Float.parseFloat(pitch_ki.getText().toString());
        float p_kd = Float.parseFloat(pitch_kd.getText().toString());
        float r_kp = Float.parseFloat(roll_kp.getText().toString());
        float r_ki = Float.parseFloat(roll_ki.getText().toString());
        float r_kd = Float.parseFloat(roll_kd.getText().toString());
        float y_kp = Float.parseFloat(yaw_kp.getText().toString());
        float y_ki = Float.parseFloat(yaw_ki.getText().toString());
        float y_kd = Float.parseFloat(yaw_kd.getText().toString());
        float a_kp = Float.parseFloat(alt_kp.getText().toString());
        float a_ki = Float.parseFloat(alt_ki.getText().toString());
        float a_kd = Float.parseFloat(alt_kd.getText().toString());
        float thrust = Float.parseFloat(throttle.getText().toString());

        float desired_angle_pitch = 0;

        float pitch_pid_i = 0,roll_pid_i = 0,yaw_pid_i = 0,alt_pid_i = 0;
        float timePrev = time;
        float time1 = Calendar.getInstance().getTimeInMillis();

        elapsedTime = (time1 - timePrev)/1000000000;
        elapsedTime = elapsedTime/10000;



        //altitude error
        float alt_error = target_altitude - altitude;

        //pitch error (depend on the altitude and the yawning but default the desired angle = 0)
        float pitch_error = 0;

        //roll error (desidred_angle = 0)
        float roll_error = roll;

        //where to yawn error
        float yaw_error = 0;
        //float roll_error = 0;

        //Commands
        boolean alt_ok = false,yaw_ok = false;
        if(alt_error<.3&&alt_error>-.3)
        {
            alt_error = 0;
            alt_ok = true;
        }
        else
        {
            alt_ok = false;
        }
        if(/*alt_ok*/true)
        {
            yaw_error = yaw - whereToYawn;
            if(yaw_error<10 && yaw_error>-10)
            {
                Log.d("wew","yawn error = 0");
                yaw_error = 0;
                yaw_ok = true;
            }
            else
            {
                yaw_ok = false;
            }
        }
        if(yaw_ok /*&& alt_ok*/)
        {
            desired_angle_pitch = 25;
        }
        else if(!yaw_ok)
        {
            desired_angle_pitch = 0;
        }

        if(roll_error<5&&roll_error>-5)
        {
            roll_error = 0;
        }
        pitch_error = pitch - desired_angle_pitch;
        if(pitch_error<5&&pitch_error>-5)
        {
            pitch_error = 0;
        }
        //End Command

        //PID_PROPRTIONAL
        float alt_pid_p = a_kp * alt_error;
        float pitch_pid_p = p_kp * pitch_error;
        float roll_pid_p = r_kp * roll_error;
        float yaw_pid_p = y_kp * yaw_error;

        //PID_INTEGRAL
        if(-10 < pitch_error && pitch_error < 10)
        {
            pitch_pid_i += p_ki*pitch_error;
        }
        if(-10 < roll_error && roll_error < 10)
        {
            roll_pid_i += r_ki*roll_error;
        }
        if(-50 < yaw_error && yaw_error < 50)
        {
            yaw_pid_i += y_ki*yaw_error;
        }
        if(-3 < alt_error && alt_error < 3)
        {
            alt_pid_i += a_ki*alt_error;
        }

        //PID_DIFFERENTIAL
        float pitch_pid_d = p_kd*((pitch_error - pitch_previous_error)/elapsedTime);
        float roll_pid_d = r_kd*((roll_error - roll_previous_error)/elapsedTime);
        float yaw_pid_d = y_kd*((yaw_error - yaw_previous_error)/elapsedTime);
        float alt_pid_d = a_kd*((alt_error - alt_prev_error)/elapsedTime);

        //all PID summation
        float pitch_PID = pitch_pid_p + pitch_pid_i + pitch_pid_d;
        float roll_PID = roll_pid_p + roll_pid_i + roll_pid_d;
        float yaw_PID = yaw_pid_p + yaw_pid_i + yaw_pid_d;
        float alt_PID = alt_pid_p + alt_pid_i + alt_pid_d;

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
        if(yaw_PID < -1000)
        {
            yaw_PID = -1000;
        }
        else if(yaw_PID > 1000)
        {
            yaw_PID = 1000;
        }
        if(alt_PID < -1000)
        {
            alt_PID = -1000;
        }
        else if(alt_PID > 1000)
        {
            alt_PID = 1000;
        }
        float pwm1 = thrust + pitch_PID - roll_PID - yaw_PID + alt_PID;
        float pwm2 = thrust - pitch_PID - roll_PID + yaw_PID + alt_PID;
        float pwm3 = thrust - pitch_PID + roll_PID - yaw_PID + alt_PID;
        float pwm4 = thrust + pitch_PID + roll_PID + yaw_PID + alt_PID;
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
        if(pwm3 < 1050)
        {
            pwm3 = 1050;
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
        alt_prev_error = alt_error;
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
        altitude= Float.parseFloat(new String(bytes));
       try {
            display("Alt:"+new String(bytes,"UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

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

    public void Clicked(View view) {
        clicked = !clicked;
        target_altitude = 1;
        receivedWaypoints.add(new LatLong(10.294612, 123.880889,false));
        receivedWaypoints.add(new LatLong(10.294321, 123.880438,false));
        receivedWaypoints.add(new LatLong(10.294677,123.880500,false));
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
                    Toast.makeText(getApplicationContext(), /*"Please make sure you're connected to the internet and your location is on."*/
                            e.getMessage() + "", Toast.LENGTH_LONG).show();
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

        //Instantiate TextView


        //Instantiate Arduino
        arduino = new Arduino(this);
        arduino.addVendorId(0x2341);
        arduino.addVendorId (0x1A86);
        arduino.setBaudRate(9600);

        elapsedTime = timePrev = 0;
        yawn = pitch = roll = 0;
        or1 = or2 = or3 = 0;
        count = 0;
        limit = 3;

        //PID
        //pitch_pid_d = pitch_pid_i = pitch_pid_d = roll_pid_p =roll_pid_i = roll_pid_d = 0;
        //roll_PID = roll_error = roll_previous_error = pitch_PID = pitch_error = pitch_previous_error = 0;
        time = 0;
//        kp = (float) 3.55;
//        ki = (float) 0.005;
//        kd = (float) 2.05;
        pwm1 = pwm2 = pwm3 = pwm4 = 1000;
//        desire_angle = 0;
        roll_previous_error = 0;
        pitch_previous_error = 0;
        yaw_previous_error = 0;
//        throttle = 1400;
        pwms = "1000100010001000";

        setContentView(R.layout.activity_dronepage_on_flight);
        locBttn = findViewById(R.id.currLocationBttn);
        receive = findViewById(R.id.txt_receive);
        tv = (TextView)findViewById(R.id.tv);
        tv1 = (TextView) findViewById(R.id.TV1);
        tv2 = (TextView) findViewById(R.id.TV3);
        tv3 = (TextView) findViewById(R.id.TV4);
        tv4 = (TextView) findViewById(R.id.TV2);
        tv5 = (TextView) findViewById(R.id.TV5);
        tv6 = (TextView) findViewById(R.id.TV6);
        tv7 = (TextView) findViewById(R.id.TV7);
        tv.setText("ALTITUDE = NULL");

        //EditText Instantiate
        pitch_kp = (EditText) findViewById(R.id.pitch_kp);
        pitch_ki = (EditText) findViewById(R.id.pitch_ki);
        pitch_kd = (EditText) findViewById(R.id.pitch_kd);
        yaw_kp = (EditText) findViewById(R.id.yawn_kp);
        yaw_ki = (EditText) findViewById(R.id.yawn_ki);
        yaw_kd = (EditText) findViewById(R.id.yawn_kd);
        roll_kp = (EditText) findViewById(R.id.roll_kp);
        roll_ki = (EditText) findViewById(R.id.roll_ki);
        roll_kd = (EditText) findViewById(R.id.roll_kd);
        alt_kp = (EditText) findViewById(R.id.alt_kp);
        alt_ki = (EditText) findViewById(R.id.alt_ki);
        alt_kd = (EditText) findViewById(R.id.alt_kd);
        throttle = (EditText) findViewById(R.id.throttle);
        sv = (ScrollView) findViewById(R.id.scrollView);

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
                        pwms = "1000100010001000";
                        pwm1 = 1000;
                        pwm2 = 1000;
                        pwm3 = 1000;
                        pwm4 = 1000;
                        arduino.send(pwms.getBytes());
                        wifiDirect = 0;
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

//        if(!checker){
//            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
//            mapboxMap.addMarker(markerOptions);
//            checker = true;
//
//        }


//        if(isWifiP2pEnabled){
//            if(isWifiConnected){
//                String m = "nisend";
//                if (dronepage_on_flight.sendReceiveThread == null) {
//                    makeConnection(dronepage_on_flight.wifiP2pDevice);
//                } else {
//                    sendReceiveThread.write(m.getBytes());
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
                if(!wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(true);
                }
                else{
                    wifiManager.setWifiEnabled(false);
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
                                wifiP2pDevice = deviceArray[i];
                                makeConnection(wifiP2pDevice);
                            }
                        });
                    }

                    //Discovery not started
                    @Override
                    public void onFailure(int reason) {
                    }
                });
            case R.id.action_beADeveloper:
                if(!developerOptions)
                {
                    sv.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Enable Editing",Toast.LENGTH_SHORT).show();
                    developerOptions = true;
                }
                else
                {
                    sv.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Disable Editing",Toast.LENGTH_SHORT).show();
                    developerOptions = false;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void makeConnection(final WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pDevice.deviceAddress;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Connection failed " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
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
                if(adapter != null && listView != null) listView.setAdapter(adapter);
            }

            if(peers.size() == 0){
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    public WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
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
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
