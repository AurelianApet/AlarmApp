package com.alarm.technothumb.alarmapplication.note;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 29-01-2018.
 */

public class NotificationService extends IntentService {
    public static final String NOTIFICATION_ID = "notification_id";

    public NotificationService (){
        super("Notification service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {




        int notification_id = intent.getIntExtra(NOTIFICATION_ID, -1);
        if (notification_id != -1) {
            //get the cursor on the notification
            Cursor cNotification = DbAccess.getNotification(getBaseContext(), notification_id);
            cNotification.moveToFirst();
            //get all the necessary attributes
            int note_id = cNotification.getInt(cNotification.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_NOTE));

            //get the corresponding note
            Cursor cNote = DbAccess.getNote(getBaseContext(), note_id);
            cNote.moveToFirst();

            //Gather the info for the notification itself
            int type = cNote.getInt(cNote.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_TYPE));
            String name = cNote.getString(cNote.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_NAME));
            Intent i = null;
            switch (type) {
                case DbContract.NoteEntry.TYPE_TEXT:
                    i = new Intent(getBaseContext(), TextNoteActivity.class);
                    i.putExtra(TextNoteActivity.EXTRA_ID, note_id);
                    break;
                case DbContract.NoteEntry.TYPE_AUDIO:
                    i = new Intent(getBaseContext(), AudioNoteActivity.class);
                    i.putExtra(AudioNoteActivity.EXTRA_ID, note_id);
                    break;
//                case DbContract.NoteEntry.TYPE_SKETCH:
//                    i = new Intent(getBaseContext(), SketchActivity.class);
//                    i.putExtra(SketchActivity.EXTRA_ID, note_id);
//                    break;
                case DbContract.NoteEntry.TYPE_CHECKLIST:
                    i = new Intent(getApplication(), ChecklistNoteActivity.class);
                    i.putExtra(ChecklistNoteActivity.EXTRA_ID, note_id);
                    break;
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                 String channelId = "channel-01";
                String channelName = "calendar_channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                mBuilder = new NotificationCompat.Builder(getBaseContext(), channelId);

                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                mNotifyMgr.createNotificationChannel(mChannel);


            }
            mBuilder.setSmallIcon(R.mipmap.ic_notification)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentTitle(name)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            // Sets an ID for the notification
            mNotifyMgr.notify(notification_id, mBuilder.build());
            cNote.close();
            cNotification.close();
            //Delete the database entry
            DbAccess.deleteNotification(getBaseContext(), notification_id);



        }


    }
}

