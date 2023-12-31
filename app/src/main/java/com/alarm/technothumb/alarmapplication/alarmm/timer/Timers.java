package com.alarm.technothumb.alarmapplication.alarmm.timer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class Timers {
    // Logging shared by TimerReceiver and TimerAlertFullScreen
    public static final boolean LOGGING = true;

    // Private actions processed by the receiver
    public static final String START_TIMER = "start_timer";
    public static final String DELETE_TIMER = "delete_timer";
    public static final String TIMES_UP = "times_up";
    public static final String TIMER_RESET = "timer_reset";
    public static final String TIMER_STOP = "timer_stop";
    public static final String TIMER_DONE = "timer_done";
    public static final String TIMER_UPDATE = "timer_update";

    public static final String TIMER_INTENT_EXTRA = "timer.intent.extra";

    public static final String NOTIF_IN_USE_SHOW = "notif_in_use_show";
    public static final String NOTIF_IN_USE_CANCEL = "notif_in_use_cancel";
    public static final String NOTIF_APP_OPEN = "notif_app_open";
    public static final String FROM_NOTIFICATION = "from_notification";
    public static final String NOTIF_TIMES_UP_STOP = "notif_times_up_stop";
    public static final String NOTIF_TIMES_UP_PLUS_ONE = "notif_times_up_plus_one";
    public static final String NOTIF_TIMES_UP_SHOW = "notif_times_up_show";
    public static final String NOTIF_TIMES_UP_CANCEL = "notif_times_up_cancel";
    public static final String FROM_ALERT = "from_alert";

    public static final String TIMESUP_MODE = "times_up";

    public static TimerObj findTimer(ArrayList<TimerObj> timers, int timerId) {
        Iterator<TimerObj> i = timers.iterator();
        while(i.hasNext()) {
            TimerObj t = i.next();
            if (t.mTimerId == timerId) {
                return t;
            }
        }
        return null;
    }
    public static TimerObj findExpiredTimer(ArrayList<TimerObj> timers) {
        Iterator<TimerObj> i = timers.iterator();
        while(i.hasNext()) {
            TimerObj t = i.next();
            if (t.mState == TimerObj.STATE_TIMESUP) {
                return t;
            }
        }
        return null;
    }

    public static ArrayList<TimerObj> timersInUse(ArrayList<TimerObj> timers) {
        ArrayList<TimerObj> result = (ArrayList<TimerObj>) timers.clone();
        Iterator<TimerObj> it = result.iterator();
        while(it.hasNext()) {
            TimerObj timer = it.next();
            if (!timer.isInUse()) {
                it.remove();
            }
        }
        return result;
    }

    public static ArrayList<TimerObj> timersInTimesUp(ArrayList<TimerObj> timers) {
        ArrayList<TimerObj> result = (ArrayList<TimerObj>) timers.clone();
        Iterator<TimerObj> it = result.iterator();
        while(it.hasNext()) {
            TimerObj timer = it.next();
            if (timer.mState != TimerObj.STATE_TIMESUP) {
                it.remove();
            }
        }
        return result;
    }
}
