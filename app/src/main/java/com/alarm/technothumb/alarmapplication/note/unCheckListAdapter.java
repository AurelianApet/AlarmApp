package com.alarm.technothumb.alarmapplication.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.Log;

import java.util.List;

public class unCheckListAdapter extends ArrayAdapter<CheckListItem> {
    private Context context;

    public unCheckListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public unCheckListAdapter(Context context, int resource, List<CheckListItem> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.item_uncheck, null);
        }
        final CheckListItem item = getItem(position);

        if (item != null) {
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_checkbox);
            TextView textView = (TextView) v.findViewById(R.id.item_name);
            LinearLayout lnrMain = v.findViewById(R.id.lnrMain);
            checkBox.setChecked(item.isChecked());
            textView.setText(item.getName());
            // Should we set a custom font size?

            android.util.Log.e("Call","call");
            if (item.isBgChecked()) {
                lnrMain.setBackgroundColor(context.getResources().getColor(R.color.primary));
            } else {
                lnrMain.setBackgroundColor(context.getResources().getColor(R.color.primary_dark));
            }
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (sp.getBoolean(SettingsActivity.PREF_CUSTOM_FONT, false)) {
                textView.setTextSize(Float.parseFloat(sp.getString(SettingsActivity.PREF_CUTSOM_FONT_SIZE, "15")));
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {



                }
            });

        }

        return v;
    }


}
