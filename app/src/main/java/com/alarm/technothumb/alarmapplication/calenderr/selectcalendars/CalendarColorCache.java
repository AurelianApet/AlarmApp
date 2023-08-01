package com.alarm.technothumb.alarmapplication.calenderr.selectcalendars;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

import com.alarm.technothumb.alarmapplication.calenderr.AsyncQueryService;

import java.util.HashSet;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class CalendarColorCache {

    private HashSet<String> mCache = new HashSet<String>();

    private static final String SEPARATOR = "::";

    private AsyncQueryService mService;
    private OnCalendarColorsLoadedListener mListener;

    private StringBuffer mStringBuffer = new StringBuffer();

    private static String[] PROJECTION = new String[] {CalendarContract.Colors.ACCOUNT_NAME, CalendarContract.Colors.ACCOUNT_TYPE };

    /**
     * Interface which provides callback after provider query of calendar colors.
     */
    public interface OnCalendarColorsLoadedListener {

        /**
         * Callback after the set of accounts with additional calendar colors are loaded.
         */
        void onCalendarColorsLoaded();
    }

    public CalendarColorCache(Context context, OnCalendarColorsLoadedListener listener) {
        mListener = listener;
        mService = new AsyncQueryService(context) {

            @Override
            public void onQueryComplete(int token, Object cookie, Cursor c) {
                if (c == null) {
                    return;
                }
                if (c.moveToFirst()) {
                    clear();
                    do {
                        insert(c.getString(0), c.getString(1));
                    } while (c.moveToNext());
                    mListener.onCalendarColorsLoaded();
                }
                if (c != null) {
                    c.close();
                }
            }
        };
        mService.startQuery(0, null, CalendarContract.Colors.CONTENT_URI, PROJECTION,
                CalendarContract.Colors.COLOR_TYPE + "=" + CalendarContract.Colors.TYPE_CALENDAR, null, null);
    }

    /**
     * Inserts a specified account into the set.
     */
    private void insert(String accountName, String accountType) {
        mCache.add(generateKey(accountName, accountType));
    }

    /**
     * Does a set lookup to determine if a specified account has more optional calendar colors.
     */
    public boolean hasColors(String accountName, String accountType) {
        return mCache.contains(generateKey(accountName, accountType));
    }

    /**
     * Clears the cached set.
     */
    private void clear() {
        mCache.clear();
    }

    /**
     * Generates a single key based on account name and account type for map lookup/insertion.
     */
    private String generateKey(String accountName, String accountType) {
        mStringBuffer.setLength(0);
        return mStringBuffer.append(accountName).append(SEPARATOR).append(accountType).toString();
    }
}
