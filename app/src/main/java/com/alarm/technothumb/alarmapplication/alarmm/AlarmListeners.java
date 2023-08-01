package com.alarm.technothumb.alarmapplication.alarmm;

import android.view.View;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class AlarmListeners {

    public static class DigitalClockClickListener implements View.OnClickListener {

        private int alarmId;

        public DigitalClockClickListener(int alarmId) {
            this.alarmId = alarmId;
        }

        @Override
        public void onClick(View view) {

        }
    }

}
