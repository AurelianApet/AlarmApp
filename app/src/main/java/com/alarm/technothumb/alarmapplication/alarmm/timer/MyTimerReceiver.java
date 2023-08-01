package com.alarm.technothumb.alarmapplication.alarmm.timer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alarm.technothumb.alarmapplication.HomeActivity;
import com.alarm.technothumb.alarmapplication.alarmm.DeskClock;
import com.alarm.technothumb.alarmapplication.alarmm.Log;
import com.alarm.technothumb.alarmapplication.alarmm.Utils;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Util;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MyTimerReceiver extends BroadcastReceiver {
    ArrayList<TimerObj> mTimers;
    private SharedPreferences mPrefs;

    boolean mVisible = true;
    final static int TIME_PERIOD_MS = 1000;
    final static int SPLIT = TIME_PERIOD_MS / 2;
    public static Boolean counterIsActive = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        mTimers = new ArrayList<TimerObj>();
        //android.util.Log.e("MyTimerReceiver", "MyTimerReceiver");


        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        TimerObj.getTimersFromSharedPrefs(mPrefs, mTimers);

        //android.util.Log.e("Size>>", mTimers.size() + "");


        // Setup for blinking
        boolean visible = Utils.getTimeNow() % TIME_PERIOD_MS < SPLIT;
        boolean toggle = mVisible != visible;
        mVisible = visible;

        if (mTimers.size() > 0) {
            long triggerTime = intent.getLongExtra(RepeatAlarmUtils.KEY_TRIGGER_TIME, 0);
            triggerTime += TimeUnit.SECONDS.toMillis(1);

            RepeatAlarmUtils repeatAlarmUtils = new RepeatAlarmUtils(context);
            repeatAlarmUtils.setRepeatAlarm(triggerTime);

        }

        for (int i = 0; i < mTimers.size(); i++) {
            TimerObj t = mTimers.get(i);

            // android.util.Log.e("mState", " " + t.mState + "  :: " + t.mTimeLeft);


            if (t.mState == TimerObj.STATE_RUNNING || t.mState == TimerObj.STATE_TIMESUP) {
                long timeLeft = t.updateTimeLeft(true);
                // android.util.Log.e("run","run");


                // if (t.mView != null) {
                if (timeLeft <= 0) {
                    timeLeft = 00;
                }
                //mTimers.get(i).mTimeLeft = timeLeft;
                //android.util.Log.e("timeLeft", i + " <> " + timeLeft + "");
                //TimerObj.putTimersInSharedPrefs(mPrefs, mTimers);

                // ((TimerListItem) (t.mView)).setTime(timeLeft, false);
                // Update button every 1/2 second
                        /*if (toggle) {
                            ImageButton leftButton = (ImageButton) t.mView.findViewById(R.id.timer_plus_one);
                            leftButton.setEnabled(canAddMinute(t));
                        }*/
            }
            // }
            if (t.mTimeLeft <= 0 && t.mState != TimerObj.STATE_DONE && t.mState != TimerObj.STATE_RESTART) {
                t.mState = TimerObj.STATE_TIMESUP;
                //t.mState = TimerObj.STATE_DONE;
                // TimerFragment.this.setTimerButtons(t);
                Log.e("mTimeLeft 0 ");
          /*      if (t.mView != null) {
                    //((TimerListItem) (t.mView)).timesUp();
                    android.util.Log.e("nullll", "nulllll");
                } else {
                    android.util.Log.e("visible", "visible");
                }
                */


                if (!Utils.IS_TIMER_VISIBLE) {
                    Intent i_alarm = new Intent(context, DeskClock.class);
                    i_alarm.putExtra("from", "receive");
                    context.startActivity(i_alarm);
                }

            }
            if (t.mState == TimerObj.STATE_STOPPED) {
                // ((TimerListItem) (t.mView)).setTextBlink(mVisible);
                android.util.Log.e("STATE_STOPPED", "STATE_STOPPED");
            }

        }
    }

    private boolean canAddMinute(TimerObj t) {
        return TimerObj.MAX_TIMER_LENGTH - t.mTimeLeft > TimerObj.MINUTE_IN_MILLIS ? true : false;
    }
}
