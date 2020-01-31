package com.example.a51zonedrone_app;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.incrementExact;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class controllerpage_waypoint extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;

    //The Map
    private GoogleMap mMap;
    private double latitude, longitude;

    //GoogleApiClient
    private FusedLocationProviderClient fusedLocationProviderClient;

    //Views
    private Button startBtn;
    private TextView textView;

    //Waypoints
    private int radius = 200, rad = 10;
    private List<LatLong> allPoints = new ArrayList<LatLong>();

    //Others
    private Marker currentLocationMarker;
    private Location currentLocation;
    private boolean firstTimeFlag = true;
    private boolean bttn = false;
    private int index = 0;
    int count = 0;
    private List<LatLng> directionPoint = new ArrayList<>();

    //Marker
    BitmapDescriptor bm;
    MarkerOptions marker = new MarkerOptions();
    Marker myMarker;

    //Routes
    PolylineOptions rectLine = new PolylineOptions();

    private final View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.currentLocationImageButton && mMap != null && currentLocation != null)
            animateCamera(currentLocation);
    };


    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && mMap != null) {
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();

                animateCamera(currentLocation);
                UpdateUI();
                firstTimeFlag = false;
            }

            showMarker(currentLocation);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controllerpage_waypoint);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);
        startBtn = (Button) findViewById(R.id.startBtn);
        textView = (TextView)findViewById(R.id.txt_LatLong);

        textView.setText("NOT STARTED");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        //mMap.setMyLocationEnabled(true);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("SUC", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("ERR", "Can't find style. Error: ", e);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                LatLong latlong = new LatLong(point);
                LatLng lat = new LatLng(point.latitude, point.longitude);
                double distance = convert(latitude, longitude, point.latitude, point.longitude);
                if(bttn == false){
                    if(distance > radius){
                        Toast.makeText(getApplicationContext(), "Warning! Drone will be out of range.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // create marker
                        allPoints.add(latlong);
                        allPoints.get(index).setLatlng(lat);
                        bm = BitmapDescriptorFactory.fromResource(R.drawable.uncheck_marker);
                        marker = new MarkerOptions().position(point).title(count + "").draggable(true).icon(bm);
                        CircleOptions circle = new CircleOptions().center(point).radius(rad)
                                .strokeWidth(3)
                                .strokeColor(getResources().getColor(R.color.colorRad))
                                .fillColor(getResources().getColor(R.color.colorRad));
                        count++;
                        index++;
                        myMarker = mMap.addMarker(marker);
                        mMap.addCircle(circle);
                    }
                }

            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                double SameThreshold = 10;
                LatLng latlng1 = new LatLng(latitude, longitude);
                directionPoint.add(latlng1);
                Handler handler = new Handler(Looper.getMainLooper());

                if(bttn == false){
                    final Runnable r = new Runnable() {
                        public void run() {
                            for(int i = 0 ; i < allPoints.size() ; i++)
                            {
                                LatLng latlng2 = new LatLng(allPoints.get(i).getLatlng().latitude, allPoints.get(i).getLatlng().longitude);
                                double distanceBetween = SphericalUtil.computeDistanceBetween(latlng1, latlng2);

                                directionPoint.add(latlng2);

                                while(!allPoints.get(i).getPass()) {
                                    if (distanceBetween < SameThreshold){
                                        Toast.makeText(getApplicationContext(), "NISUD", Toast.LENGTH_SHORT).show();
                                        bm = BitmapDescriptorFactory.fromResource(R.drawable.check_marker);
                                        //MarkerOptions marker = new MarkerOptions().position(allPoints.get(i).getLatlng()).title(count + "").icon(bm);
                                        marker = new MarkerOptions().position(allPoints.get(i).getLatlng()).title(allPoints.get(i).getLatlng().latitude + ", " + allPoints.get(i).getLatlng().longitude).icon(bm);
                                        myMarker = mMap.addMarker(marker);
                                        allPoints.get(i).setPass(true);
                                    }
                                    else{
                                        break;
                                    }
                                }
                            }

                            directionPoint.add(latlng1);
                            for (int i = 0; i < directionPoint.size(); i++) {
                                rectLine.add(directionPoint.get(i));
                            }

                            try {
                                mMap.addPolyline(rectLine);
                            }
                            catch (Exception e){
                            }

                            handler.postDelayed(this, 1000);
                        }
                    };

                    handler.postDelayed(r, 1000);

                    bttn = true;
                    startBtn.setText("CANCEL");
                    textView.setText("STARTED");
                }
                else{
                    bttn = false;
                    startBtn.setText("START");
                    textView.setText("NOT STARTED");
                    //alertDialog();
                }
            }
        });
    }

    private void UpdateUI(){
        //Setting the LatLong
        LatLng latLng = new LatLng(latitude, longitude);
        //Toast.makeText(getApplicationContext(), latitude + ", " + longitude, Toast.LENGTH_SHORT).show();

        //textView.setText(latitude + ", " + longitude);0
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(100.0f);

        //BitmapDescriptor bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        //.icon(bitmapDescriptorFactory);
        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radius)
                .strokeWidth(3)
                .strokeColor(getResources().getColor(R.color.colorRange))
                .fillColor(getResources().getColor(R.color.colorRange));
        //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
       //mMap.addMarker(markerOptions);
        mMap.addCircle(circleOptions);

        rectLine =  new PolylineOptions().width(8).color(Color.RED);
        //PolylineOptions polylineOptions = new PolylineOptions().add(latLng, (LatLng) allPoints.get(0));

    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(controllerpage_waypoint.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCurrentLocationUpdates();
        }
    }

    private void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (currentLocationMarker == null)
            currentLocationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng));
        else {
            AnimatingMarkerHelper.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());
        }
    }

    //Converting the radius of the Earth to meters
    public double convert(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = sin(dLat/2) * sin(dLat/2) +
                cos(lat1 * Math.PI / 180) * cos(lat2 * Math.PI / 180) *
                        sin(dLon/2) * sin(dLon/2);
        double c = 2 * atan2(sqrt(a), sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient = null;
        mMap = null;
    }

    private void alertDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Do you wish to cancel the operation?");
        dialog.setTitle("Alert Box");
        dialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        mMap.clear();
                        Toast.makeText(getApplicationContext(),"The drone will now return to the starting point",Toast.LENGTH_LONG).show();
                    }
                });
        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"cancel is clicked",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
}