package com.alarm.technothumb.alarmapplication.calenderr.event;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.AbstractCalendarActivity;
import com.alarm.technothumb.alarmapplication.calenderr.CalendarController;
import com.alarm.technothumb.alarmapplication.calenderr.CalendarEventModel;
import com.alarm.technothumb.alarmapplication.calenderr.Utils;

import java.util.ArrayList;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

public class EditEventActivity extends AbstractCalendarActivity {
    public static final String EXTRA_EVENT_COLOR = "event_color";
    public static final String EXTRA_EVENT_REMINDERS = "reminders";
    private static final String TAG = "EditEventActivity";
    private static final boolean DEBUG = false;
    private static final String BUNDLE_KEY_EVENT_ID = "key_event_id";
    private static boolean mIsMultipane;

    private EditEventFragment mEditFragment;

    private ArrayList<CalendarEventModel.ReminderEntry> mReminders;

    private int mEventColor;

    private boolean mEventColorInitialized;

    private CalendarController.EventInfo mEventInfo;


    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.simple_frame_layout);


        mEventInfo = getEventInfoFromIntent(icicle);
        mReminders = getReminderEntriesFromIntent();
        mEventColorInitialized = getIntent().hasExtra(EXTRA_EVENT_COLOR);
        mEventColor = getIntent().getIntExtra(EXTRA_EVENT_COLOR, -1);


        mEditFragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.main_frame);

        mIsMultipane = Utils.getConfigBool(this, R.bool.multiple_pane_config);

        if (mIsMultipane) {
            getSupportActionBar().setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_TITLE,
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setTitle(
                    mEventInfo.id == -1 ? R.string.event_create : R.string.event_edit);
        }
        else {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME|
                            ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        if (mEditFragment == null) {
            Intent intent = null;
            if (mEventInfo.id == -1) {
                intent = getIntent();
            }

            mEditFragment = new EditEventFragment(mEventInfo, mReminders, mEventColorInitialized,
                    mEventColor, false, intent);

            mEditFragment.mShowModifyDialogOnLaunch = getIntent().getBooleanExtra(
                    CalendarController.EVENT_EDIT_ON_LAUNCH, false);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, mEditFragment);
            ft.show(mEditFragment);
            ft.commit();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<CalendarEventModel.ReminderEntry> getReminderEntriesFromIntent() {
        Intent intent = getIntent();
        return (ArrayList<CalendarEventModel.ReminderEntry>) intent.getSerializableExtra(EXTRA_EVENT_REMINDERS);
    }

    private CalendarController.EventInfo getEventInfoFromIntent(Bundle icicle) {
        CalendarController.EventInfo info = new CalendarController.EventInfo();
        long eventId = -1;
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            try {
                eventId = Long.parseLong(data.getLastPathSegment());
            } catch (NumberFormatException e) {
                if (DEBUG) {
                    Log.d(TAG, "Create new event");
                }
            }
        } else if (icicle != null && icicle.containsKey(BUNDLE_KEY_EVENT_ID)) {
            eventId = icicle.getLong(BUNDLE_KEY_EVENT_ID);
        }

        boolean allDay = intent.getBooleanExtra(EXTRA_EVENT_ALL_DAY, false);

        long begin = intent.getLongExtra(EXTRA_EVENT_BEGIN_TIME, -1);
        long end = intent.getLongExtra(EXTRA_EVENT_END_TIME, -1);
        if (end != -1) {
            info.endTime = new Time();
            if (allDay) {
                info.endTime.timezone = Time.TIMEZONE_UTC;
            }
            info.endTime.set(end);
        }
        if (begin != -1) {
            info.startTime = new Time();
            if (allDay) {
                info.startTime.timezone = Time.TIMEZONE_UTC;
            }
            info.startTime.set(begin);
        }
        info.id = eventId;
        info.eventTitle = intent.getStringExtra(CalendarContract.Events.TITLE);
        info.calendarId = intent.getLongExtra(CalendarContract.Events.CALENDAR_ID, -1);

        if (allDay) {
            info.extraLong = CalendarController.EXTRA_CREATE_ALL_DAY;
        } else {
            info.extraLong = 0;
        }
        return info;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.returnToCalendarHome(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
