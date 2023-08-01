package com.alarm.technothumb.alarmapplication.medicinee.model;

import java.util.Comparator;

/**
 * Created by NIKUNJ on 24-01-2018.
 */

public class PillComparator implements Comparator<Pill> {

    @Override
    public int compare(Pill pill1, Pill pill2){

        String firstName = pill1.getPillName();
        String secondName = pill2.getPillName();
        return firstName.compareTo(secondName);
    }
}
