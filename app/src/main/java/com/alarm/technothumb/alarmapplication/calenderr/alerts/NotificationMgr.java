package com.alarm.technothumb.alarmapplication.calenderr.alerts;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public abstract class NotificationMgr {
    public abstract void notify(int id, AlertService.NotificationWrapper notification);
    public abstract void cancel(int id);

    /**
     * Don't actually use the notification framework's cancelAll since the SyncAdapter
     * might post notifications and we don't want to affect those.
     */
    public void cancelAll() {
        cancelAllBetween(0, AlertService.MAX_NOTIFICATIONS);
    }

    /**
     * Cancels IDs between the specified bounds, inclusively.
     */
    public void cancelAllBetween(int from, int to) {
        for (int i = from; i <= to; i++) {
            cancel(i);
        }
    }
}

