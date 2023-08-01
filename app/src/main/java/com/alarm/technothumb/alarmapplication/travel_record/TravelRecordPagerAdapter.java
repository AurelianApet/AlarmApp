package com.alarm.technothumb.alarmapplication.travel_record;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 12-02-2018.
 */

public class TravelRecordPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public TravelRecordPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
//        return new MainFragment();
        if (position == 0) {

            fragment =  new Map_mainFragment();
        }
        else if (position == 1) {
            fragment =  new AudioRecordFragment();
        }


        return fragment;



    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position

        String title = null;

        if (position == 0)
        {
            title = mContext.getString(R.string.camera);
        }
        else if (position == 1)
        {
            title = mContext.getString(R.string.audio);
        }
        return title;

    }

}


