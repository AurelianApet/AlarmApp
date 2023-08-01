package com.alarm.technothumb.alarmapplication.alarmm;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class Log {
    public final static String LOGTAG = "LightUpDroid";

    /** This must be false for production.  If true, turns on verbose logging, extra info, etc. */
    public static final boolean LOGV = true;

    public static void d(String logMe) {
        android.util.Log.d(LOGTAG, logMe);
    }

    public static void v(String logMe) {
        android.util.Log.v(LOGTAG, logMe);
    }

    public static void i(String logMe) {
        android.util.Log.i(LOGTAG, logMe);
    }

    public static void e(String logMe) {
        android.util.Log.e(LOGTAG, logMe);
    }

    public static void e(String logMe, Exception ex) {
        android.util.Log.e(LOGTAG, logMe, ex);
    }

    public static void w(String logMe) {
        android.util.Log.w(LOGTAG, logMe);
    }

    public static void wtf(String logMe) {
        android.util.Log.wtf(LOGTAG, logMe);
    }

}

