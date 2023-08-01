package com.alarm.technothumb.alarmapplication.note;

import com.alarm.technothumb.alarmapplication.alarmm.Log;

/**
 * Created by NIKUNJ on 29-01-2018.
 */

public class CheckListItem {

    private boolean isChecked = false;
    private String name = "";
    private boolean bgChecked = false;

    public CheckListItem(boolean isChecked, String name) {
        this.isChecked = isChecked;
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBgChecked() {
        return bgChecked;
    }

    public void setBgChecked(boolean bgChecked) {
        this.bgChecked = bgChecked;
        android.util.Log.w("Adapter",bgChecked+"");
    }
}
