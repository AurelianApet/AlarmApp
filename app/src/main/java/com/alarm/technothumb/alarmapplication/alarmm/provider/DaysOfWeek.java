package com.alarm.technothumb.alarmapplication.alarmm.provider;

import android.content.Context;

import com.alarm.technothumb.alarmapplication.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public final class DaysOfWeek {
    // Number if days in the week.
    public static final int DAYS_IN_A_WEEK = 7;

    // Value when all days are set
    public static final int ALL_DAYS_SET = 0x7f;

    // Value when no days are set
    public static final int NO_DAYS_SET = 0;

    /**
     * Need to have monday start at index 0 to be backwards compatible. This converts
     * Calendar.DAY_OF_WEEK constants to our internal bit structure.
     */
    private static int convertDayToBitIndex(int day) {
        return (day + 5) % DAYS_IN_A_WEEK;
    }

    /**
     * Need to have monday start at index 0 to be backwards compatible. This converts
     * our bit structure to Calendar.DAY_OF_WEEK constant value.
     */
    private static int convertBitIndexToDay(int bitIndex) {
        return (bitIndex + 1) % DAYS_IN_A_WEEK + 1;
    }

    // Bitmask of all repeating days
    private int mBitSet;

    public DaysOfWeek(int bitSet) {
        mBitSet = bitSet;
    }

    public String toString(Context context, boolean showNever) {
        return toString(context, showNever, false);
    }

    public String toAccessibilityString(Context context) {
        return toString(context, false, true);
    }

    private String toString(Context context, boolean showNever, boolean forAccessibility) {
        StringBuilder ret = new StringBuilder();

        // no days
        if (mBitSet == NO_DAYS_SET) {
            return showNever ? context.getText(R.string.never).toString() : "";
        }

        // every day
        if (mBitSet == ALL_DAYS_SET) {
            return context.getText(R.string.every_day).toString();
        }

        // count selected days
        int dayCount = 0;
        int bitSet = mBitSet;
        while (bitSet > 0) {
            if ((bitSet & 1) == 1) dayCount++;
            bitSet >>= 1;
        }

        // short or long form?
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] dayList = (forAccessibility || dayCount <= 1) ?
                dfs.getWeekdays() :
                dfs.getShortWeekdays();

        // selected days
        for (int bitIndex = 0; bitIndex < DAYS_IN_A_WEEK; bitIndex++) {
            if ((mBitSet & (1 << bitIndex)) != 0) {
                ret.append(dayList[convertBitIndexToDay(bitIndex)]);
                dayCount -= 1;
                if (dayCount > 0) ret.append(context.getText(R.string.day_concat));
            }
        }
        return ret.toString();
    }

    /**
     * Enables or disable certain days of the week.
     *
     * @param daysOfWeek Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, etc.
     */
    public void setDaysOfWeek(boolean value, int ... daysOfWeek) {
        for (int day : daysOfWeek) {
            setBit(convertDayToBitIndex(day), value);
        }
    }

    private boolean isBitEnabled(int bitIndex) {
        return ((mBitSet & (1 << bitIndex)) > 0);
    }

    private void setBit(int bitIndex, boolean set) {
        if (set) {
            mBitSet |= (1 << bitIndex);
        } else {
            mBitSet &= ~(1 << bitIndex);
        }
    }

    public void setBitSet(int bitSet) {
        mBitSet = bitSet;
    }

    public int getBitSet() {
        return mBitSet;
    }

    public HashSet<Integer> getSetDays() {
        final HashSet<Integer> result = new HashSet<Integer>();
        for (int bitIndex = 0; bitIndex < DAYS_IN_A_WEEK; bitIndex++) {
            if (isBitEnabled(bitIndex)) {
                result.add(convertBitIndexToDay(bitIndex));
            }
        }
        return result;
    }

    public boolean isRepeating() {
        return mBitSet != NO_DAYS_SET;
    }


    public boolean isMondayEnabled() {
        return isBitEnabled(0);
    }

    public boolean isTuesdayEnabled() {
        return isBitEnabled(1);
    }

    public boolean isWednesdayEnabled() {
        return isBitEnabled(2);
    }

    public boolean isThursdayEnabled() {
        return isBitEnabled(3);
    }

    public boolean isFridayEnabled() {
        return isBitEnabled(4);
    }

    public boolean isSaturdayEnabled() {
        return isBitEnabled(5);
    }

    public boolean isSundayEnabled() {
        return isBitEnabled(6);
    }

    /**
     * Returns number of days from today until next alarm.
     *
     * @param current must be set to today
     */
    public int calculateDaysToNextAlarm(Calendar current) {
        if (!isRepeating()) {
            return -1;
        }

        int dayCount = 0;
        int currentDayBit = convertDayToBitIndex(current.get(Calendar.DAY_OF_WEEK));
        for (; dayCount < DAYS_IN_A_WEEK; dayCount++) {
            int nextAlarmBit = (currentDayBit + dayCount) % DAYS_IN_A_WEEK;
            if (isBitEnabled(nextAlarmBit)) {
                break;
            }
        }
        return dayCount;
    }

    public void clearAllDays() {
        mBitSet = NO_DAYS_SET;
    }

    @Override
    public String toString() {
        return "DaysOfWeek{" +
                "mBitSet=" + mBitSet +
                '}';
    }
}
