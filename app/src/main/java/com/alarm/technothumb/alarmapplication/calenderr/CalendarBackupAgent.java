package com.alarm.technothumb.alarmapplication.calenderr;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class CalendarBackupAgent extends BackupAgentHelper
{
    static final String SHARED_KEY = "shared_pref";

    @Override
    public void onCreate() {
        addHelper(SHARED_KEY, new SharedPreferencesBackupHelper(this,
                GeneralPreferences.SHARED_PREFS_NAME));
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState)
            throws IOException {
        // See Utils.getRingTonePreference for more info
        final SharedPreferences.Editor editor = getSharedPreferences(
                GeneralPreferences.SHARED_PREFS_NAME_NO_BACKUP, Context.MODE_PRIVATE).edit();
        editor.putString(GeneralPreferences.KEY_ALERTS_RINGTONE,
                GeneralPreferences.DEFAULT_RINGTONE).commit();

        super.onRestore(data, appVersionCode, newState);
    }
}

