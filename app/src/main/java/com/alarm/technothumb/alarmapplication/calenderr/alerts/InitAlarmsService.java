package com.alarm.technothumb.alarmapplication.calenderr.alerts;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.util.Log;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class InitAlarmsService extends IntentService {
    private static final String TAG = "InitAlarmsService";
    private static final String SCHEDULE_ALARM_REMOVE_PATH = "schedule_alarms_remove";
    private static final Uri SCHEDULE_ALARM_REMOVE_URI = Uri.withAppendedPath(
            CalendarContract.CONTENT_URI, SCHEDULE_ALARM_REMOVE_PATH);

    // Delay for rescheduling the alarms must be great enough to minimize race
    // conditions with the provider's boot up actions.
    private static final long DELAY_MS = 30000;

    public InitAlarmsService() {
        super("InitAlarmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Delay to avoid race condition of in-progress alarm scheduling in provider.
        SystemClock.sleep(DELAY_MS);
        Log.d(TAG, "Clearing and rescheduling alarms.");
        try {
            getContentResolver().update(SCHEDULE_ALARM_REMOVE_URI, new ContentValues(), null,
                    null);
        } catch (IllegalArgumentException e) {
            // java.lang.IllegalArgumentException:
            //     Unknown URI content://com.android.calendar/schedule_alarms_remove

            // Until b/7742576 is resolved, just catch the exception so the app won't crash
            Log.e(TAG, "update failed: " + e.toString());
        }
    }
}

