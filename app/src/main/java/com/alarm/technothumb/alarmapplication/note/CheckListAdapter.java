package com.alarm.technothumb.alarmapplication.note;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.Log;

import java.util.List;

/**
 * Created by NIKUNJ on 29-01-2018.
 */

public class CheckListAdapter extends ArrayAdapter<CheckListItem> {
    public CheckListAdapter(Context context, int resource) {
        super(context, resource);
    }

    Context context;
    public CheckListAdapter(Context context, int resource, List<CheckListItem> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.item_checklist, null);
        }
        CheckListItem item = getItem(position);

        if (item != null) {

            CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_checkbox);
            TextView textView = (TextView) v.findViewById(R.id.item_name);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            checkBox.setChecked(item.isChecked());
            textView.setText(item.getName());
            // Should we set a custom font size?

            android.util.Log.e("isBgChecked",item.isBgChecked()+"");


            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (sp.getBoolean(SettingsActivity.PREF_CUSTOM_FONT, false)) {
                textView.setTextSize(Float.parseFloat(sp.getString(SettingsActivity.PREF_CUTSOM_FONT_SIZE, "15")));
            }
        }

        return v;
    }


}
