package com.alarm.technothumb.alarmapplication.travel_record.listeners;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.alarm.technothumb.alarmapplication.travel_record.entities.MyLocationManager;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Util;

/**
 * Created by NIKUNJ on 08-02-2018.
 */

public class SatelliteLocationListener implements LocationListener {

    /*
     * (non-Javadoc)
     * @see android.location.LocationListener#onLocationChanged(android.location
     * .Location)
     */
    @Override
    public final void onLocationChanged(final Location newLocation) {
        MyLocationManager mLocationManager = Util.getLocationManager();
        if (newLocation != null) {
            Log.i("SAT LISTENER", "LAT: " + newLocation.getLatitude()
                    + " ;LOG: " + newLocation.getLongitude());
            mLocationManager.setLatitude(newLocation.getLatitude());
            mLocationManager.setLongitude(newLocation.getLongitude());
            mLocationManager.setAltitude(newLocation.getAltitude());
            mLocationManager.setLastKnownLocation();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.location.LocationListener#onStatusChanged(java.lang.String,
     * int, android.os.Bundle)
     */
    @Override
    public final void onStatusChanged(final String provider,
                                      final int status, final Bundle extras) {
        Log.i("SAT LISTENER", "onStatusChanged not implemented");
    }

    /*
     * (non-Javadoc)
     * @see
     * android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public final void onProviderEnabled(final String provider) {
        Log.i("SAT LISTENER", "onProviderEnabled not implemented");
    }

    /*
     * (non-Javadoc)
     * @see
     * android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public final void onProviderDisabled(final String provider) {
        Log.i("SAT LISTENER", "onProviderDisabled not implemented");
    }
}
