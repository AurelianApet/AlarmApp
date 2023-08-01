package com.alarm.technothumb.alarmapplication.calenderr;

import android.text.format.DateUtils;

import java.util.Calendar;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class CalendarData {

    static final String[] s24Hours = {
            format24Hours(0), format24Hours(1), format24Hours(2), format24Hours(3), format24Hours(4), format24Hours(5),
            format24Hours(6), format24Hours(7), format24Hours(8), format24Hours(9), format24Hours(10), format24Hours(11),
            format24Hours(12), format24Hours(13), format24Hours(14), format24Hours(15), format24Hours(16), format24Hours(17),
            format24Hours(18), format24Hours(19), format24Hours(20), format24Hours(21), format24Hours(22), format24Hours(23),
            format24Hours(0)};
    private static final String am = DateUtils.getAMPMString(Calendar.AM).toUpperCase();
    private static final String pm = DateUtils.getAMPMString(Calendar.PM).toUpperCase();
    static final String[] s12Hours = {
            format12Hours(12, am), format12Hours(1, am), format12Hours(2, am), format12Hours(3, am), format12Hours(4, am),
            format12Hours(5, am), format12Hours(6, am), format12Hours(7, am), format12Hours(8, am), format12Hours(9, am),
            format12Hours(10, am), format12Hours(11, am), format12Hours(12, pm), format12Hours(1, pm), format12Hours(2, pm),
            format12Hours(3, pm), format12Hours(4, pm), format12Hours(5, pm), format12Hours(6, pm), format12Hours(7, pm),
            format12Hours(8, pm), format12Hours(9, pm), format12Hours(10, pm), format12Hours(11, pm), format12Hours(12, pm)};

    public CalendarData() {


    }

    private static String format12Hours(int hour, String amPm) {
        return String.format("%d %s", hour, amPm);
    }

    private static String format24Hours(int hour) {
        return String.format("%02d:%02d", hour, 0);
    }
}
