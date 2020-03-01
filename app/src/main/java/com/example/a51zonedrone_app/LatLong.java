package com.example.a51zonedrone_app;


import com.mapbox.mapboxsdk.geometry.LatLng;

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
        return latlng;
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

    @Override
    public String toString()
    {
        return ("["+this.latitude+","+this.longitude+","+this.pass+"]");
    }
}
