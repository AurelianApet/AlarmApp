package com.alarm.technothumb.alarmapplication.alarmm;

import android.widget.Toast;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class ToastMaster {

    private static Toast sToast = null;

    private ToastMaster() {

    }

    public static void setToast(Toast toast) {
        if (sToast != null)
            sToast.cancel();
        sToast = toast;
    }

    public static void cancelToast() {
        if (sToast != null)
            sToast.cancel();
        sToast = null;
    }

}

