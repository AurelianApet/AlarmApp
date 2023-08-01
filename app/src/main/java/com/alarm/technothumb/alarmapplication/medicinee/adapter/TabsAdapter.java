package com.alarm.technothumb.alarmapplication.medicinee.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alarm.technothumb.alarmapplication.medicinee.HistoryFragment;
import com.alarm.technothumb.alarmapplication.medicinee.TodayFragment;
import com.alarm.technothumb.alarmapplication.medicinee.TomorrowFragment;

/**
 * Created by NIKUNJ on 24-01-2018.
 */

public class TabsAdapter extends FragmentPagerAdapter {

    private int TOTAL_TABS = 3;

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new HistoryFragment();
            case 1:
                return new TodayFragment();
            case 2:
                return new TomorrowFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TOTAL_TABS;
    }
}
