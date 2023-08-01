package com.alarm.technothumb.alarmapplication.travel_record.utils;

import android.Manifest;

/**
 * Created by NIKUNJ on 08-02-2018.
 */

public class Constants {



    // Util Constants
    public static final String PREFS_NAME = "androidLocationManager";

    public static final String LOCATION_PERMISSIONS_FLAG = "hasLocationPermissions";

    public static final int BUFFER_SIZE = 1024;

    public static final int NEARBY_POINTS = 5;

    // PERMISSIONs Constants
    public static final int LOCATION_PERMISSIONS_CODE = 0;

    public static final String[] LOCATION_PERMISSION_CODES = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    // Locations Constantes
    public static final float DEFAULT_MAP_ZOOM = 15.5f;


}
