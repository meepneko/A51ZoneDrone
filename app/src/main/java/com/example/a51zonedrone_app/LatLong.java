package com.example.a51zonedrone_app;


import com.mapbox.mapboxsdk.geometry.LatLng;

public class LatLong {
    private LatLng latlng;
    private double latitude, longitude;
    private boolean pass;
    public LatLong(LatLng ll)
    {
        pass = false;
    }

    public LatLong() {

    }

    // Getter latlng
    public LatLng getLatlng() {
        return latlng;
    }

    // Setter latlng
    public void setLatlng(LatLng ll) {
        latlng = ll;
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
}
