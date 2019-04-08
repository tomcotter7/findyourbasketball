package com.thomas.findyourbasketball;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Court {

    private String TAG = Court.class.getSimpleName();
    private String name;
    private double latitude;
    private double longitude;
    private Context context;

    public Court(String nameInput, double lat, double lng /**Context contextInput**/) {
        this.name = nameInput;
        this.latitude = new LatLng(lat,lng).latitude;
        this.longitude = new LatLng(lat,lng).longitude;
        //this.context = contextInput;
    }

    // A getter for the latitude.
    public double getLatitude() {
        return this.latitude;
    }

    // A getter for the latitude.
    public double getLongitude() {
        return this.longitude;
    }

    // A getter for the court name.
    public String getName() {
        return this.name;
    }

    // Return the location of the court in a LatLng object.
    public LatLng getLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    public boolean isNearby(double distance, LatLng globalLocation) {
        // Set up the longitudinal lines which the court must be inside.
        double boxLeft = globalLocation.longitude - distance;
        double boxRight = globalLocation.longitude + distance;
        boolean result = false;
        // If the boxLeft and boxRight lines do not cross the 180 degree longitudinal line.
        if (boxLeft >= -180 && boxRight <= 180) {
            result = (this.longitude >= (boxLeft) && this.longitude <= (boxRight));
        } else {
            // If the boxLeft crosses the 180 degree longitudinal line.
            if (boxLeft < -180) {
                double extraDistance = (boxLeft + 180);
                // Check up to the -180 degree line and also check anything past that line.
                result = ((this.longitude >= (-180)) || (this.longitude >= (180 + extraDistance)) && this.longitude <= (boxRight));
            }
            // If the boxRight crosses the 180 degree longitudinal line.
            if (boxRight > 180) {
                double extraDistance = (boxRight - 180);
                // Check up to the 180 degree line plus any extra distance after it.
                result = this.longitude >= (boxLeft) && (this.longitude <= (180)) || (this.longitude <= (-180 + extraDistance));
            }
        }
        return result;
    }

    /**public String getAddress() {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String postalCode = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(this.getLatitude(), this.getLongitude(), 1);
            if (addresses != null) {
                postalCode = addresses.get(0).getPostalCode();
            }

        } catch (IOException ioException) {
            Log.e(TAG, "Error +"+ ioException);
        }
        return postalCode;
    }**/

    public String toString(){
        return String.format(Locale.ENGLISH, "%s [%f,%f]",
                this.name,this.latitude,this.longitude);
    }

}
