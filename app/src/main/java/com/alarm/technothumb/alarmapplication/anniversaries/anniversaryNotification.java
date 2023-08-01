package com.alarm.technothumb.alarmapplication.anniversaries;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alarm.technothumb.alarmapplication.R;

import com.alarm.technothumb.alarmapplication.note.AudioNoteActivity;
import com.alarm.technothumb.alarmapplication.note.ChecklistNoteActivity;
import com.alarm.technothumb.alarmapplication.note.DbAccess;
import com.alarm.technothumb.alarmapplication.note.DbContract;
import com.alarm.technothumb.alarmapplication.note.TextNoteActivity;

/**
 * Created by wdl-android on 30-04-2018.
 */

public class anniversaryNotification extends IntentService {
    public static final String NOTIFICATION_ID = "notification_id";

    public anniversaryNotification (){
        super("Notification service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int notification_id = intent.getIntExtra(NOTIFICATION_ID, -1);

        if (notification_id != -1) {
            //get the cursor on the notification


                Intent    i = new Intent(getBaseContext(), Anniversary.class);
                             //i.putExtra("Anni", note_id);


            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);

            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                String channelId = "channel-01";
                String channelName = "calander_channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                mBuilder = new NotificationCompat.Builder(getBaseContext(), channelId);

                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                mNotifyMgr.createNotificationChannel(mChannel);


            }
            mBuilder.setSmallIcon(R.mipmap.ic_notification)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                   .setContentTitle("Anniversary")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            // Sets an ID for the notification
            mNotifyMgr.notify(notification_id, mBuilder.build());





        }

    }

    }