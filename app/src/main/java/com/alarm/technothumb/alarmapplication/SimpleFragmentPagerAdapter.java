package com.alarm.technothumb.alarmapplication;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alarm.technothumb.alarmapplication.alarm.MainFragment;
import com.alarm.technothumb.alarmapplication.stopwatcher.StopWatcherFragment;
import com.alarm.technothumb.alarmapplication.timer.TimerFragment;
import com.alarm.technothumb.alarmapplication.worldtime.WorldTimeFragment;

/**
 * Created by NIKUNJ on 17-01-2018.
 */

class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
//        return new MainFragment();
        if (position == 0) {
            fragment =  new MainFragment();
        }
        else if (position == 1) {
            fragment =  new WorldTimeFragment();
        }
        else if (position == 2) {
            fragment =  new StopWatcherFragment();
        }
        else if (position == 3) {
            fragment = new TimerFragment();
        }
//        else if (position == 4) {
//
//            fragment = new NoteFragment();
//        }
//
//        else if (position == 5) {
//
//            fragment = new CalenderFragmentt();
//        }
//        else if (position == 5) {
//
//            fragment = new MedicineeFragment();
//        }

//        else if (position == 6) {
//
//            fragment = new Medicine_Fragment();
//        }
//        else if(position == 7)
//        {
//            fragment = new CalenderFragment();
//        }

        return fragment;



    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 4;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position

        String title = null;

        if (position == 0)
        {
            title = mContext.getString(R.string.alarm);
        }
        else if (position == 1)
        {
            title = mContext.getString(R.string.world_clock);
        }
        else if (position == 2)
        {
            title = mContext.getString(R.string.stop_watcher);
        }
        else if (position == 3)
        {
            title = mContext.getString(R.string.timer);
        }
//        else if (position == 4)
//        {
//            title = mContext.getString(R.string.notes);
//        }
//        else if (position == 5)
//        {
//            title = mContext.getString(R.string.calender);
//        }
//        else if (position == 5)
//        {
//            title = mContext.getString(R.string.medicine);
//        }


//        else if (position == 6)
//        {
//            title = "Medication Schedule";
//        }
//
//        else if (position == 7)
//        {
//            title = "Anniversary with re-occurring functions";
//        }
        return title;

    }

}

