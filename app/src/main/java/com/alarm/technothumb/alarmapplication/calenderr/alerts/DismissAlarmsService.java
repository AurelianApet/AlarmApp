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
import android.support.v4.app.TaskStackBuilder;

import com.alarm.technothumb.alarmapplication.calenderr.EventInfoActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class DismissAlarmsService extends IntentService {
    private static final String[] PROJECTION = new String[] {
            CalendarContract.CalendarAlerts.STATE,
    };
    private static final int COLUMN_INDEX_STATE = 0;

    public DismissAlarmsService() {
        super("DismissAlarmsService");
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
        boolean showEvent = intent.getBooleanExtra(AlertUtils.SHOW_EVENT_KEY, false);
        long[] eventIds = intent.getLongArrayExtra(AlertUtils.EVENT_IDS_KEY);
        long[] eventStarts = intent.getLongArrayExtra(AlertUtils.EVENT_STARTS_KEY);
        int notificationId = intent.getIntExtra(AlertUtils.NOTIFICATION_ID_KEY, -1);
        List<GlobalDismissManager.AlarmId> alarmIds = new LinkedList<GlobalDismissManager.AlarmId>();

        Uri uri = CalendarContract.CalendarAlerts.CONTENT_URI;
        String selection;

        // Dismiss a specific fired alarm if id is present, otherwise, dismiss all alarms
        if (eventId != -1) {
            alarmIds.add(new GlobalDismissManager.AlarmId(eventId, eventStart));
            selection = CalendarContract.CalendarAlerts.STATE + "=" + CalendarContract.CalendarAlerts.STATE_FIRED + " AND " +
                    CalendarContract.CalendarAlerts.EVENT_ID + "=" + eventId;
        } else if (eventIds != null && eventIds.length > 0 &&
                eventStarts != null && eventIds.length == eventStarts.length) {
            selection = buildMultipleEventsQuery(eventIds);
            for (int i = 0; i < eventIds.length; i++) {
                alarmIds.add(new GlobalDismissManager.AlarmId(eventIds[i], eventStarts[i]));
            }
        } else {
            // NOTE: I don't believe that this ever happens.
            selection = CalendarContract.CalendarAlerts.STATE + "=" + CalendarContract.CalendarAlerts.STATE_FIRED;
        }

        GlobalDismissManager.dismissGlobally(getApplicationContext(), alarmIds);

        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PROJECTION[COLUMN_INDEX_STATE], CalendarContract.CalendarAlerts.STATE_DISMISSED);
        resolver.update(uri, values, selection, null);

        // Remove from notification bar.
        if (notificationId != -1) {
            NotificationManager nm =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(notificationId);
        }

        if (showEvent) {
            // Show event on Calendar app by building an intent and task stack to start
            // EventInfoActivity with AllInOneActivity as the parent activity rooted to home.
            Intent i = AlertUtils.buildEventViewIntent(this, eventId, eventStart, eventEnd);

            TaskStackBuilder.create(this)
                    .addParentStack(EventInfoActivity.class).addNextIntent(i).startActivities();
        }
    }

    private String buildMultipleEventsQuery(long[] eventIds) {
        StringBuilder selection = new StringBuilder();
        selection.append(CalendarContract.CalendarAlerts.STATE);
        selection.append("=");
        selection.append(CalendarContract.CalendarAlerts.STATE_FIRED);
        if (eventIds.length > 0) {
            selection.append(" AND (");
            selection.append(CalendarContract.CalendarAlerts.EVENT_ID);
            selection.append("=");
            selection.append(eventIds[0]);
            for (int i = 1; i < eventIds.length; i++) {
                selection.append(" OR ");
                selection.append(CalendarContract.CalendarAlerts.EVENT_ID);
                selection.append("=");
                selection.append(eventIds[i]);
            }
            selection.append(")");
        }
        return selection.toString();
    }
}
