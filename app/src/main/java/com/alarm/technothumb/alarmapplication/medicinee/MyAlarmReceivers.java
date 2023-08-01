package com.alarm.technothumb.alarmapplication.medicinee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceivers extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String setRingtoneUri = intent.getStringExtra("uri");
        String pill_name = intent.getStringExtra("pill_name");
        Intent intent2 = new Intent(context, AlertActivity.class);
        intent2.putExtra("uri", setRingtoneUri);
        intent2.putExtra("pill_name", pill_name);
        context.startActivity(intent2);
    }
}
