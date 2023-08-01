package com.alarm.technothumb.alarmapplication.calenderr;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class AboutPreferences extends PreferenceFragment {
    private static final String BUILD_VERSION = "build_version";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.about_preferences);

        final Activity activity = getActivity();
        try {
            final PackageInfo packageInfo =
                    activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            findPreference(BUILD_VERSION).setSummary(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            findPreference(BUILD_VERSION).setSummary("?");
        }
    }
}
