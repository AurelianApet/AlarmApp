package com.alarm.technothumb.alarmapplication.note;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 29-01-2018.
 */

public class SettingsFragment extends PreferenceFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
    }
}
