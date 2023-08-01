package com.alarm.technothumb.alarmapplication.calenderr.selectcalendars;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.AsyncQueryService;
import com.alarm.technothumb.alarmapplication.calenderr.CalendarController;
import com.alarm.technothumb.alarmapplication.calenderr.Utils;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class SelectVisibleCalendarsFragment extends Fragment
        implements AdapterView.OnItemClickListener, CalendarController.EventHandler,
        CalendarColorCache.OnCalendarColorsLoadedListener {

    private static final String TAG = "Calendar";
    private static final String IS_PRIMARY = "\"primary\"";
    private static final String SELECTION = CalendarContract.Calendars.SYNC_EVENTS + "=?";
    private static final String[] SELECTION_ARGS = new String[]{"1"};

    private static final String[] PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.OWNER_ACCOUNT,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS,
            "(" + CalendarContract.Calendars.ACCOUNT_NAME + "=" + CalendarContract.Calendars.OWNER_ACCOUNT + ") AS " + IS_PRIMARY,
    };
    private static int mUpdateToken;
    private static int mQueryToken;
    private static int mCalendarItemLayout = R.layout.mini_calendar_item;

    private View mView = null;
    private CalendarController mController;
    private ListView mList;
    private SelectCalendarsSimpleAdapter mAdapter;
    private Activity mContext;
    private AsyncQueryService mService;
    private Cursor mCursor;

    public SelectVisibleCalendarsFragment() {
    }

    @SuppressLint("ValidFragment")
    public SelectVisibleCalendarsFragment(int itemLayout) {
        mCalendarItemLayout = itemLayout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mController = CalendarController.getInstance(activity);
        mController.registerEventHandler(R.layout.select_calendars_fragment, this);
        mService = new AsyncQueryService(activity) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                mAdapter.changeCursor(cursor);
                mCursor = cursor;
            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mController.deregisterEventHandler(R.layout.select_calendars_fragment);
        if (mCursor != null) {
            mAdapter.changeCursor(null);
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.select_calendars_fragment, null);
        mList = (ListView) mView.findViewById(R.id.list);

        // Hide the Calendars to Sync button on tablets for now.
        // Long terms stick it in the list of calendars
        if (Utils.getConfigBool(getActivity(), R.bool.multiple_pane_config)) {
            // Don't show dividers on tablets
            mList.setDivider(null);
            View v = mView.findViewById(R.id.manage_sync_set);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SelectCalendarsSimpleAdapter(mContext, mCalendarItemLayout, null,
                getFragmentManager());
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter == null || mAdapter.getCount() <= position) {
            return;
        }
        toggleVisibility(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        mQueryToken = mService.getNextToken();
        mService.startQuery(mQueryToken, null, CalendarContract.Calendars.CONTENT_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, CalendarContract.Calendars.ACCOUNT_NAME);
    }

    /*
     * Write back the changes that have been made.
     */
    public void toggleVisibility(int position) {
        mUpdateToken = mService.getNextToken();
        Uri uri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, mAdapter.getItemId(position));
        ContentValues values = new ContentValues();
        // Toggle the current setting
        int visibility = mAdapter.getVisible(position) ^ 1;
        values.put(CalendarContract.Calendars.VISIBLE, visibility);
        mService.startUpdate(mUpdateToken, null, uri, values, null, null, 0);
        mAdapter.setVisible(position, visibility);
    }

    @Override
    public void eventsChanged() {
        if (mService != null) {
            mService.cancelOperation(mQueryToken);
            mQueryToken = mService.getNextToken();

            mService.startQuery(mQueryToken, null, CalendarContract.Calendars.CONTENT_URI, PROJECTION, SELECTION,
                    SELECTION_ARGS, CalendarContract.Calendars.ACCOUNT_NAME);
            Log.e(">>>>>>>>>>>>>>>", CalendarContract.Calendars.CONTENT_URI.toString() + "\n" + CalendarContract.Calendars.ACCOUNT_NAME
                    + "\n");
        }
    }

    @Override
    public long getSupportedEventTypes() {
        return CalendarController.EventType.EVENTS_CHANGED;
    }

    @Override
    public void handleEvent(CalendarController.EventInfo event) {
        eventsChanged();
    }

    @Override
    public void onCalendarColorsLoaded() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
