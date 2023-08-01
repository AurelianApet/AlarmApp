package com.alarm.technothumb.alarmapplication.calenderr.alerts;

import android.app.PendingIntent;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public interface AlarmManagerInterface {

    public void set(int type, long triggerAtMillis, PendingIntent operation);
}
