package com.alarm.technothumb.alarmapplication.calenderr.selectcalendars;

import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.content.AsyncQueryHandler;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.Utils;

public class SelectSyncedCalendarsMultiAccountActivity extends ExpandableListActivity
        implements View.OnClickListener {

    private static final String TAG = "Calendar";
    private static final String EXPANDED_KEY = "is_expanded";
    private static final String ACCOUNT_UNIQUE_KEY = "ACCOUNT_KEY";
    private static final String[] PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE + " || " + CalendarContract.Calendars.ACCOUNT_NAME + " AS " +
                    ACCOUNT_UNIQUE_KEY,
    };
    private MatrixCursor mAccountsCursor = null;
    private ExpandableListView mList;
    private SelectSyncedCalendarsMultiAccountAdapter mAdapter;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.select_calendars_multi_accounts_fragment);

        mList = getExpandableListView();
        mList.setEmptyView(findViewById(R.id.loading));
        // Start a background sync to get the list of calendars from the server.
        Utils.startCalendarMetafeedSync(null);

        findViewById(R.id.btn_done).setOnClickListener(this);
        findViewById(R.id.btn_discard).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_done) {
            if (mAdapter != null) {
                mAdapter.doSaveAction();
            }
            finish();
        } else if (view.getId() == R.id.btn_discard) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.startRefreshStopDelay();
        }
        new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                mAccountsCursor = Utils.matrixCursorFromCursor(cursor);

                mAdapter = new SelectSyncedCalendarsMultiAccountAdapter(
                        findViewById(R.id.calendars).getContext(), mAccountsCursor,
                        SelectSyncedCalendarsMultiAccountActivity.this);
                mList.setAdapter(mAdapter);

                // TODO initialize from sharepref
                int count = mList.getCount();
                for(int i = 0; i < count; i++) {
                    mList.expandGroup(i);
                }
            }
        }.startQuery(0, null, CalendarContract.Calendars.CONTENT_URI, PROJECTION,
                "1) GROUP BY (" + ACCOUNT_UNIQUE_KEY, //Cheap hack to make WHERE a GROUP BY query
                null /* selectionArgs */,
                CalendarContract.Calendars.ACCOUNT_NAME /*sort order*/);
        //TODO change to something that supports group by queries.
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.cancelRefreshStopDelay();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.closeChildrenCursors();
        }
        if (mAccountsCursor != null && !mAccountsCursor.isClosed()) {
            mAccountsCursor.close();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean[] isExpanded;
        mList = getExpandableListView();
        if(mList != null) {
            int count = mList.getCount();
            isExpanded = new boolean[count];
            for(int i = 0; i < count; i++) {
                isExpanded[i] = mList.isGroupExpanded(i);
            }
        } else {
            isExpanded = null;
        }
        outState.putBooleanArray(EXPANDED_KEY, isExpanded);
        //TODO Store this to preferences instead so it remains on restart
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mList = getExpandableListView();
        boolean[] isExpanded = state.getBooleanArray(EXPANDED_KEY);
        if(mList != null && isExpanded != null && mList.getCount() >= isExpanded.length) {
            for(int i = 0; i < isExpanded.length; i++) {
                if(isExpanded[i] && !mList.isGroupExpanded(i)) {
                    mList.expandGroup(i);
                } else if(!isExpanded[i] && mList.isGroupExpanded(i)){
                    mList.collapseGroup(i);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar()
                .setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Utils.returnToCalendarHome(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

