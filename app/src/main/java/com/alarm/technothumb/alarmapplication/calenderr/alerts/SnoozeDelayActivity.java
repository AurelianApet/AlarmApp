package com.alarm.technothumb.alarmapplication.calenderr.alerts;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TimePicker;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.Utils;

public class SnoozeDelayActivity extends Activity implements
        TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener {
    private static final int DIALOG_DELAY = 1;

    @Override
    protected void onResume() {
        super.onResume();
        showDialog(DIALOG_DELAY);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DELAY) {
            TimePickerDialog d = new TimePickerDialog(this, this, 0, 0, true);
            d.setTitle(R.string.snooze_delay_dialog_title);
            d.setCancelable(true);
            d.setOnCancelListener(this);
            return d;
        }

        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog d) {
        if (id == DIALOG_DELAY) {
            TimePickerDialog tpd = (TimePickerDialog) d;
            int delayMinutes = (int) (Utils.getDefaultSnoozeDelayMs(this) / (60L * 1000L));
            int hours = delayMinutes / 60;
            int minutes = delayMinutes % 60;

            tpd.updateTime(hours, minutes);
        }
        super.onPrepareDialog(id, d);
    }

    @Override
    public void onCancel(DialogInterface d) {
        finish();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        long delay = (hour * 60 + minute) * 60L * 1000L;
        Intent intent = getIntent();
        intent.setClass(this, SnoozeAlarmsService.class);
        intent.putExtra(AlertUtils.SNOOZE_DELAY_KEY, delay);
        startService(intent);
        finish();
    }
}
