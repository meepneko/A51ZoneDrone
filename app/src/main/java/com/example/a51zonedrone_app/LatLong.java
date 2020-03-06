package com.example.a51zonedrone_app;


import com.mapbox.mapboxsdk.geometry.LatLng;

import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

public class LatLong {
    private LatLng latlng;
    private double latitude, longitude;
    private boolean pass;
    public LatLong(LatLng ll)
    {
        latitude = ll.getLatitude();
        longitude = ll.getLongitude();
        pass = false;
    }

    public LatLong(double lat, double lon, boolean p){
        latitude = lat;
        longitude = lon;
        pass = p;
    }

    // Getter latlng
    public LatLng getLatlng() {
        return new LatLng(latitude,longitude);
    }

    // Setter latlng
    public void setLatlng(LatLng ll) {
        latlng = ll;
        latitude = ll.getLatitude();
        longitude = ll.getLongitude();
    }
    // Getter pass
    public boolean getPass() {
        return pass;
    }

    // Setter pass
    public void setPass(boolean p) {
        this.pass = p;
    }

    public void setLatitude(double lat){
        latitude = lat;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double lon){
        longitude = lon;
    }

    public double getLongitude(){
        return longitude;
    }

    public static double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
        // Haversine's formula
        int bearing = 0;
        double dLng = fromLng - toLng;
        double y = Math.cos(toLat)*Math.sin(dLng);
        double x = (Math.cos(fromLat)*Math.sin(toLat))-
                (Math.sin(fromLat)*Math.cos(toLat)*Math.cos(dLng));
        bearing = (int) Math.toDegrees(Math.atan2(y,x));
        bearing = ((bearing%360)+540)%360;
        return bearing;
    }

    public static double computeAngleBetween(LatLng from, LatLng to) {
        // Haversine's formula
        int bearing = 0;
        double dLng = from.getLongitude() - to.getLongitude();
        double y = Math.cos(to.getLatitude())*Math.sin(dLng);
        double x = (Math.cos(from.getLatitude())*Math.sin(to.getLatitude()))-
                (Math.sin(from.getLatitude())*Math.cos(to.getLatitude())*Math.cos(dLng));
        bearing = (int) Math.toDegrees(Math.atan2(y,x));
        bearing = ((bearing%360)+540)%360;
        return bearing;
    }

    @Override
    public String toString()
    {
        return ("["+this.latitude+","+this.longitude+","+this.pass+"]");
    }
}
