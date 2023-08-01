package com.alarm.technothumb.alarmapplication.travel_record.listeners;

import android.location.Location;
import android.util.Log;

import com.alarm.technothumb.alarmapplication.travel_record.entities.MyLocationManager;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Util;

/**
 * Created by NIKUNJ on 08-02-2018.
 */

public class NetworkLocationListener implements
        com.google.android.gms.location.LocationListener {

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
}
