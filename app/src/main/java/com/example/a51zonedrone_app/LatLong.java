package com.example.a51zonedrone_app;

import com.google.android.gms.maps.model.LatLng;

public class LatLong {
    private LatLng latlng;
    private boolean pass;
    public LatLong(LatLng ll)
    {
        pass = false;
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
}
