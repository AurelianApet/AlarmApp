package com.alarm.technothumb.alarmapplication.worldtime;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NIKUNJ on 17-01-2018.
 */

public class TimeZoneListAdapter extends ArrayAdapter<WorldClockTimeZone> {
    //TODO - externalize into preferences in the future
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE hh:mm a");
    private WorldClockTimeZone[] displayTimeZones;

    public TimeZoneListAdapter(Context context, WorldClockTimeZone[] tzValues) {
        super(context, R.layout.worldclock_main_list_item, R.id.list_display_label, tzValues);
        this.displayTimeZones=tzValues;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.worldclock_main_list_item, null);
        }

        WorldClockTimeZone tz = displayTimeZones[position];
        TextView time = (TextView)convertView.findViewById(R.id.list_time_label);
        DATE_FORMAT.setTimeZone(tz.getTimeZone());
        time.setText(DATE_FORMAT.format(new Date()));

        TextView displayName = (TextView)convertView.findViewById(R.id.list_display_label);
        displayName.setText(tz.getDisplayName());

        //image
        Resources res = getContext().getResources();
        ImageView displayIcon = (ImageView) convertView.findViewById(R.id.list_icon);
        //TODO - check if performance is good. Only visible list is rendered so maybe ok
        displayIcon.setImageResource(res.getIdentifier(tz.getFlagResourceName(), "drawable", getContext().getPackageName()));

        return convertView;
    }

}


