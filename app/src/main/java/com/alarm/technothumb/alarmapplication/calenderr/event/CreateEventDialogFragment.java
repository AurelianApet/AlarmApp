package com.alarm.technothumb.alarmapplication.calenderr.event;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.AsyncQueryService;
import com.alarm.technothumb.alarmapplication.calenderr.CalendarController;
import com.alarm.technothumb.alarmapplication.calenderr.CalendarEventModel;
import com.alarm.technothumb.alarmapplication.calenderr.GeneralPreferences;
import com.alarm.technothumb.alarmapplication.calenderr.Utils;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class CreateEventDialogFragment extends DialogFragment implements TextWatcher {

    private static final String TAG = "CreateEventDialogFragment";

    private static final int TOKEN_CALENDARS = 1 << 3;

    private static final String KEY_DATE_STRING = "date_string";
    private static final String KEY_DATE_IN_MILLIS = "date_in_millis";
    private static final String EVENT_DATE_FORMAT = "%a, %b %d, %Y";

    private AlertDialog mAlertDialog;

    private CalendarQueryService mService;

    private EditText mEventTitle;
    private View mColor;

    private TextView mCalendarName;
    private TextView mAccountName;
    private TextView mDate;
    private Button mButtonAddEvent;

    private CalendarController mController;
    private EditEventHelper mEditEventHelper;

    private String mDateString;
    private long mDateInMillis;

    private CalendarEventModel mModel;
    private long mCalendarId = -1;
    private String mCalendarOwner;

    public CreateEventDialogFragment() {
        // Empty constructor required for DialogFragment.
    }

    @SuppressLint("ValidFragment")
    public CreateEventDialogFragment(Time day) {
        setDay(day);
    }

    public void setDay(Time day) {
        mDateString = day.format(EVENT_DATE_FORMAT);
        mDateInMillis = day.toMillis(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDateString = savedInstanceState.getString(KEY_DATE_STRING);
            mDateInMillis = savedInstanceState.getLong(KEY_DATE_IN_MILLIS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final LayoutInflater layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.create_event_dialog, null);

        mColor = view.findViewById(R.id.color);
        mCalendarName = (TextView) view.findViewById(R.id.calendar_name);
        mAccountName = (TextView) view.findViewById(R.id.account_name);

        mEventTitle = (EditText) view.findViewById(R.id.event_title);
        mEventTitle.addTextChangedListener(this);

        mDate = (TextView) view.findViewById(R.id.event_day);
        if (mDateString != null) {
            mDate.setText(mDateString);
        }

        mAlertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.new_event_dialog_label)
                .setView(view)
                .setPositiveButton(R.string.create_event_dialog_save,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                createAllDayEvent();
                                dismiss();
                            }
                        })
                .setNeutralButton(R.string.edit_label,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mController.sendEventRelatedEventWithExtraWithTitleWithCalendarId(this,
                                        CalendarController.EventType.CREATE_EVENT, -1, mDateInMillis,
                                        mDateInMillis + DateUtils.DAY_IN_MILLIS, 0, 0,
                                        CalendarController.EXTRA_CREATE_ALL_DAY, -1,
                                        mEventTitle.getText().toString(),
                                        mCalendarId);
                                dismiss();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        return mAlertDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mButtonAddEvent == null) {
            mButtonAddEvent = mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            mButtonAddEvent.setEnabled(mEventTitle.getText().toString().length() > 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DATE_STRING, mDateString);
        outState.putLong(KEY_DATE_IN_MILLIS, mDateInMillis);
    }

    @Override
    public void onActivityCreated(Bundle args) {
        super.onActivityCreated(args);
        final Context context = getActivity();
        mController = CalendarController.getInstance(getActivity());
        mEditEventHelper = new EditEventHelper(context);
        mModel = new CalendarEventModel(context);
        mService = new CalendarQueryService(context);
        mService.startQuery(TOKEN_CALENDARS, null, CalendarContract.Calendars.CONTENT_URI,
                EditEventHelper.CALENDARS_PROJECTION,
                EditEventHelper.CALENDARS_WHERE_WRITEABLE_VISIBLE, null,
                null);
    }

    private void createAllDayEvent() {
        mModel.mStart = mDateInMillis;
        mModel.mEnd = mDateInMillis + DateUtils.DAY_IN_MILLIS;
        mModel.mTitle = mEventTitle.getText().toString();
        mModel.mAllDay = true;
        mModel.mCalendarId = mCalendarId;
        mModel.mOwnerAccount = mCalendarOwner;

        if (mEditEventHelper.saveEvent(mModel, null, 0)) {
            Toast.makeText(getActivity(), R.string.creating_event, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Do nothing.
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mButtonAddEvent != null) {
            mButtonAddEvent.setEnabled(s.length() > 0);
        }
    }

    // Find the calendar position in the cursor that matches calendar in
    // preference
    @SuppressLint("LongLogTag")
    private void setDefaultCalendarView(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            // Create an error message for the user that, when clicked,
            // will exit this activity without saving the event.
            dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.no_syncable_calendars).setIconAttribute(
                    android.R.attr.alertDialogIcon).setMessage(R.string.no_calendars_found)
                    .setPositiveButton(R.string.add_account, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Activity activity = getActivity();
                            if (activity != null) {
                                Intent nextIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                                final String[] array = {"com.briswell.mycalendar"};
                                nextIntent.putExtra(Settings.EXTRA_AUTHORITIES, array);
                                nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(nextIntent);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.show();
            return;
        }


        String defaultCalendar = null;
        final Activity activity = getActivity();
        if (activity != null) {
            defaultCalendar = Utils.getSharedPreference(activity,
                    GeneralPreferences.KEY_DEFAULT_CALENDAR, (String) null);
        } else {
            Log.e(TAG, "Activity is null, cannot load default calendar");
        }

        int calendarOwnerIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.OWNER_ACCOUNT);
        int accountNameIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME);
        int accountTypeIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE);

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String calendarOwner = cursor.getString(calendarOwnerIndex);
            if (defaultCalendar == null) {
                // There is no stored default upon the first time running.  Use a primary
                // calendar in this case.
                if (calendarOwner != null &&
                        calendarOwner.equals(cursor.getString(accountNameIndex)) &&
                        !CalendarContract.ACCOUNT_TYPE_LOCAL.equals(
                                cursor.getString(accountTypeIndex))) {
                    setCalendarFields(cursor);
                    return;
                }
            } else if (defaultCalendar.equals(calendarOwner)) {
                // Found the default calendar.
                setCalendarFields(cursor);
                return;
            }
        }
        cursor.moveToFirst();
        setCalendarFields(cursor);
    }

    private void setCalendarFields(Cursor cursor) {
        int calendarIdIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID);
        int colorIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR);
        int calendarNameIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
        int accountNameIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME);
        int calendarOwnerIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.OWNER_ACCOUNT);

        mCalendarId = cursor.getLong(calendarIdIndex);
        mCalendarOwner = cursor.getString(calendarOwnerIndex);
        mColor.setBackgroundColor(Utils.getDisplayColorFromColor(cursor
                .getInt(colorIndex)));
        String accountName = cursor.getString(accountNameIndex);
        String calendarName = cursor.getString(calendarNameIndex);
        mCalendarName.setText(calendarName);
        if (calendarName.equals(accountName)) {
            mAccountName.setVisibility(View.GONE);
        } else {
            mAccountName.setVisibility(View.VISIBLE);
            mAccountName.setText(accountName);
        }
    }

    private class CalendarQueryService extends AsyncQueryService {

        /**
         * @param context
         */
        public CalendarQueryService(Context context) {
            super(context);
        }

        @Override
        public void onQueryComplete(int token, Object cookie, Cursor cursor) {
            setDefaultCalendarView(cursor);
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

