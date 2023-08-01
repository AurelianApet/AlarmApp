package com.alarm.technothumb.alarmapplication.calenderr.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.CalenderrActivity;
import com.alarm.technothumb.alarmapplication.calenderr.EventInfoActivity;
import com.alarm.technothumb.alarmapplication.calenderr.Utils;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class CalendarAppWidgetProvider extends AppWidgetProvider {
    static final String TAG = "CalendarAppWidgetProvider";
    static final boolean LOGD = false;

    // TODO Move these to Calendar.java
    static final String EXTRA_EVENT_IDS = "com.briswell.mycalendar.EXTRA_EVENT_IDS";

    /**
     * Build {@link ComponentName} describing this specific
     * {@link AppWidgetProvider}
     */
    static ComponentName getComponentName(Context context) {
        return new ComponentName(context, CalendarAppWidgetProvider.class);
    }


    static PendingIntent getUpdateIntent(Context context) {
        Intent intent = new Intent(Utils.getWidgetScheduledUpdateAction(context));
        intent.setDataAndType(CalendarContract.CONTENT_URI, Utils.APPWIDGET_DATA_TYPE);
        return PendingIntent.getBroadcast(context, 0 /* no requestCode */, intent,
                0 /* no flags */);
    }

    /**
     * Build a {@link PendingIntent} to launch the Calendar app. This should be used
     * in combination with {@link RemoteViews#setPendingIntentTemplate(int, PendingIntent)}.
     */
    static PendingIntent getLaunchPendingIntentTemplate(Context context) {
        Intent launchIntent = new Intent();
        launchIntent.setAction(Intent.ACTION_VIEW);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        launchIntent.setClass(context, CalenderrActivity.class);
        return PendingIntent.getActivity(context, 0 /* no requestCode */, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    static Intent getLaunchFillInIntent(Context context, long id, long start, long end,
                                        boolean allDay) {
        final Intent fillInIntent = new Intent();
        String dataString = "content://com.briswell.mycalendar/events";
        if (id != 0) {
            fillInIntent.putExtra(Utils.INTENT_KEY_DETAIL_VIEW, true);
            fillInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);

            dataString += "/" + id;
            // If we have an event id - start the event info activity
            fillInIntent.setClass(context, EventInfoActivity.class);
        } else {
            // If we do not have an event id - start AllInOne
            fillInIntent.setClass(context, CalenderrActivity.class);
        }
        Uri data = Uri.parse(dataString);
        fillInIntent.setData(data);
        fillInIntent.putExtra(EXTRA_EVENT_BEGIN_TIME, start);
        fillInIntent.putExtra(EXTRA_EVENT_END_TIME, end);
        fillInIntent.putExtra(EXTRA_EVENT_ALL_DAY, allDay);

        return fillInIntent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle calendar-specific updates ourselves because they might be
        // coming in without extras, which AppWidgetProvider then blocks.
        final String action = intent.getAction();
        if (LOGD)
            Log.d(TAG, "AppWidgetProvider got the intent: " + intent.toString());
        if (Utils.getWidgetUpdateAction(context).equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            performUpdate(context, appWidgetManager,
                    appWidgetManager.getAppWidgetIds(getComponentName(context)),
                    null /* no eventIds */);
        } else if (action.equals(Intent.ACTION_PROVIDER_CHANGED)
                || action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(Intent.ACTION_DATE_CHANGED)
                || action.equals(Utils.getWidgetScheduledUpdateAction(context))) {
            Intent service = new Intent(context, CalendarAppWidgetService.class);
            context.startService(service);
        } else {
            super.onReceive(context, intent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisabled(Context context) {
        // Unsubscribe from all AlarmManager updates
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingUpdate = getUpdateIntent(context);
        am.cancel(pendingUpdate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        performUpdate(context, appWidgetManager, appWidgetIds, null /* no eventIds */);
    }

    /**
     * Process and push out an update for the given appWidgetIds. This call
     * actually fires an intent to start {@link CalendarAppWidgetService} as a
     * background service which handles the actual update, to prevent ANR'ing
     * during database queries.
     *
     * @param context Context to use when starting {@link CalendarAppWidgetService}.
     * @param appWidgetIds List of specific appWidgetIds to update, or null for
     *            all.
     * @param changedEventIds Specific events known to be changed. If present,
     *            we use it to decide if an update is necessary.
     */
    private void performUpdate(Context context,
                               AppWidgetManager appWidgetManager, int[] appWidgetIds,
                               long[] changedEventIds) {
        // Launch over to service so it can perform update
        for (int appWidgetId : appWidgetIds) {
            if (LOGD) Log.d(TAG, "Building widget update...");
            Intent updateIntent = new Intent(context, CalendarAppWidgetService.class);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            if (changedEventIds != null) {
                updateIntent.putExtra(EXTRA_EVENT_IDS, changedEventIds);
            }
            updateIntent.setData(Uri.parse(updateIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            // Calendar header
            Time time = new Time(Utils.getTimeZone(context, null));
            time.setToNow();
            long millis = time.toMillis(true);
            final String dayOfWeek = DateUtils.getDayOfWeekString(time.weekDay + 1,
                    DateUtils.LENGTH_MEDIUM);
            final String date = Utils.formatDateRange(context, millis, millis,
                    DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_NO_YEAR);
            views.setTextViewText(R.id.day_of_week, dayOfWeek);
            views.setTextViewText(R.id.date, date);
            // Attach to list of events
            views.setRemoteAdapter(appWidgetId, R.id.events_list, updateIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.events_list);


            // Launch calendar app when the user taps on the header
            final Intent launchCalendarIntent = new Intent(Intent.ACTION_VIEW);
            launchCalendarIntent.setClass(context, CalenderrActivity.class);
            launchCalendarIntent
                    .setData(Uri.parse("content://com.alarm.technothumb.alarmapplication/time/" + millis));
            final PendingIntent launchCalendarPendingIntent = PendingIntent.getActivity(
                    context, 0 /* no requestCode */, launchCalendarIntent, 0 /* no flags */);
            views.setOnClickPendingIntent(R.id.header, launchCalendarPendingIntent);

            // Each list item will call setOnClickExtra() to let the list know
            // which item
            // is selected by a user.
            final PendingIntent updateEventIntent = getLaunchPendingIntentTemplate(context);
            views.setPendingIntentTemplate(R.id.events_list, updateEventIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

//    private static PendingIntent getNewEventPendingIntent(Context context) {
//        Intent newEventIntent = new Intent(Intent.ACTION_EDIT);
//        newEventIntent.setClass(context, EditEventActivity.class);
//        Builder builder = CalendarContract.CONTENT_URI.buildUpon();
//        builder.appendPath("events");
//        newEventIntent.setData(builder.build());
//        return PendingIntent.getActivity(context, 0, newEventIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//    }
}

