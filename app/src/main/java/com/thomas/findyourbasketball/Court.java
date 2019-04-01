package com.thomas.findyourbasketball;

import com.google.android.gms.maps.model.LatLng;

public class Court {
    private String name;
    private double latitude;
    private double longitude;


    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getName() {
        return this.name;
    }

    public LatLng getLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    public boolean isNearby(double distance, LatLng globalLocation) {
        double boxRight = globalLocation.longitude + distance;
        double boxLeft = globalLocation.longitude - distance;
        boolean result = false;
        if (boxRight <= 180 && boxLeft >= -180) {
            result = (this.longitude <= (boxRight) && (this.longitude >= (boxLeft)));
        } else {
            if (boxRight > 180) {
                double extraDistance = (boxRight - 180);
                result = ((this.longitude <= (180)) || (this.longitude <= (-180 + extraDistance))) && (this.longitude >= (boxLeft));
            }
            if (boxLeft < -180) {
                double extraDistance = (boxLeft + 180);
                result = (this.longitude <= (boxRight)) && ((this.longitude >= (-180)) || (this.longitude >= (180 + extraDistance)));
            }
        }
        return result;
    }
}
