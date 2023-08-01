package com.alarm.technothumb.alarmapplication.medicine;

import android.app.Application;
import android.content.Context;

/**
 * Created by NIKUNJ on 19-01-2018.
 */

public class MedicineApp extends Application {

    private static Context mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = getApplicationContext();
        }
    }

    public static Context getInstance() {
        return mInstance;
    }
}
