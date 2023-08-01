package com.alarm.technothumb.alarmapplication.alarmm.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.alarm.technothumb.alarmapplication.alarmm.Log;

/**
 * Created by Aquib on 2/3/2018.
 */

public class RepeatAlarmUtils {
    public static final String KEY_TRIGGER_TIME = "key_trigger_time";
    public static final int REQUEST_CODE = 12345;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Intent intent;
    private Context context;

    public RepeatAlarmUtils(Context context) {
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.intent = new Intent(context, MyTimerReceiver.class);
        this.pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.context = context;
    }

    public void setRepeatAlarm(long triggerTime) {

      //  android.util.Log.e("receive", "receive");
        intent.putExtra(KEY_TRIGGER_TIME, System.currentTimeMillis() + triggerTime);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime,
                    pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime,
                    pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime,
                    pendingIntent);
        }
    }

    public void cancelAlarm() {
        this.alarmManager.cancel(pendingIntent);
    }
}
