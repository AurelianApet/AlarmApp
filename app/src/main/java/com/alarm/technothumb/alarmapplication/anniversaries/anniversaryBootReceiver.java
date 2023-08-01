package com.alarm.technothumb.alarmapplication.anniversaries;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.alarm.technothumb.alarmapplication.note.DbAccess;
import com.alarm.technothumb.alarmapplication.note.DbContract;
import com.alarm.technothumb.alarmapplication.note.NotificationService;

/**
 * Created by wdl-android on 30-04-2018.
 */

public class anniversaryBootReceiver extends BroadcastReceiver {
    public anniversaryBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Cursor c = DbAccess.getAllAnniNotifications(context);

        while (c.moveToNext()) {
            int notification_id = c.getInt(c.getColumnIndexOrThrow(dbHelper.AnniversaryID));

            Log.e("notificationID", notification_id+"");


            Intent i = new Intent(context, anniversaryNotification.class);
            i.putExtra(NotificationService.NOTIFICATION_ID, notification_id);

        }
    }
}
