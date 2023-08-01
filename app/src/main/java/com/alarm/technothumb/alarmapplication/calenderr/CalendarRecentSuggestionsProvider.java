package com.alarm.technothumb.alarmapplication.calenderr;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by NIKUNJ on 31-01-2018.
 */

public class CalendarRecentSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static int MODE = DATABASE_MODE_QUERIES;

    public CalendarRecentSuggestionsProvider() {
    }

    @Override
    public boolean onCreate() {
        setupSuggestions(Utils.getSearchAuthority(getContext()), MODE);
        return super.onCreate();
    }
}
