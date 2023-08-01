package com.alarm.technothumb.alarmapplication.calenderr.alerts;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CalendarContract;

import com.alarm.technothumb.alarmapplication.calenderr.Utils;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class SnoozeAlarmsService extends IntentService {
    private static final String[] PROJECTION = new String[] {
            CalendarContract.CalendarAlerts.STATE,
    };
    private static final int COLUMN_INDEX_STATE = 0;

    public SnoozeAlarmsService() {
        super("SnoozeAlarmsService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onHandleIntent(Intent intent) {

        long eventId = intent.getLongExtra(AlertUtils.EVENT_ID_KEY, -1);
        long eventStart = intent.getLongExtra(AlertUtils.EVENT_START_KEY, -1);
        long eventEnd = intent.getLongExtra(AlertUtils.EVENT_END_KEY, -1);
        long snoozeDelay = intent.getLongExtra(AlertUtils.SNOOZE_DELAY_KEY,
                Utils.getDefaultSnoozeDelayMs(this));

        // The ID reserved for the expired notification digest should never be passed in
        // here, so use that as a default.
        int notificationId = intent.getIntExtra(AlertUtils.NOTIFICATION_ID_KEY,
                AlertUtils.EXPIRED_GROUP_NOTIFICATION_ID);

        if (eventId != -1) {
            ContentResolver resolver = getContentResolver();

            // Remove notification
            if (notificationId != AlertUtils.EXPIRED_GROUP_NOTIFICATION_ID) {
                NotificationManager nm =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(notificationId);
            }

            // Dismiss current alarm
            Uri uri = CalendarContract.CalendarAlerts.CONTENT_URI;
            String selection = CalendarContract.CalendarAlerts.STATE + "=" + CalendarContract.CalendarAlerts.STATE_FIRED + " AND " +
                    CalendarContract.CalendarAlerts.EVENT_ID + "=" + eventId;
            ContentValues dismissValues = new ContentValues();
            dismissValues.put(PROJECTION[COLUMN_INDEX_STATE], CalendarContract.CalendarAlerts.STATE_DISMISSED);
            resolver.update(uri, dismissValues, selection, null);

            // Add a new alarm
            long alarmTime = System.currentTimeMillis() + snoozeDelay;
            ContentValues values = AlertUtils.makeContentValues(eventId, eventStart, eventEnd,
                    alarmTime, 0);
            resolver.insert(uri, values);
            AlertUtils.scheduleAlarm(SnoozeAlarmsService.this, AlertUtils.createAlarmManager(this),
                    alarmTime);
        }
        AlertService.updateAlertNotification(this);
        stopSelf();
    }
}

