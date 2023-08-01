package com.alarm.technothumb.alarmapplication.calenderr;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by NIKUNJ on 31-01-2018.
 */

public abstract class AbstractCalendarActivity extends AppCompatActivity {
    protected AsyncQueryService mService;

    public synchronized AsyncQueryService getAsyncQueryService() {
        if (mService == null) {
            mService = new AsyncQueryService(this);
        }
        return mService;
    }
}
