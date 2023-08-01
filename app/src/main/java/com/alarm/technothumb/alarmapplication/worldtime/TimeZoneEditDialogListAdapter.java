package com.alarm.technothumb.alarmapplication.worldtime;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIKUNJ on 17-01-2018.
 */

public class TimeZoneEditDialogListAdapter extends ArrayAdapter<WorldClockTimeZone> {
    private List<WorldClockTimeZone> originalDataValues;
    private List<WorldClockTimeZone> filteredDataValues;
    private Filter filter = null;

    public TimeZoneEditDialogListAdapter(Context context, List<WorldClockTimeZone> tzValues) {
        super(context, R.layout.timezone_edit_dialog_list, R.id.dialog_list_display_line1, tzValues);

        //original values
        this.originalDataValues = new ArrayList<WorldClockTimeZone>(tzValues);

        //filtered values - this is what is used in display
        this.filteredDataValues = new ArrayList<WorldClockTimeZone>(tzValues);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.timezone_edit_dialog_list_item,
                    null);
        }

        WorldClockTimeZone tz = getItem(position);//from underlying mObjects array

        // display label
        TextView displayLabel = (TextView) convertView.findViewById(R.id.dialog_list_display_line1);
        displayLabel.setText(tz.getId() + " " + tz.getRawOffsetDisplay());

        // offset
        TextView displayOffset = (TextView) convertView.findViewById(R.id.dialog_list_display_line2);
        displayOffset.setText(tz.getDisplayName());

        // image icon
        Resources res = getContext().getResources();
        ImageView displayIcon = (ImageView) convertView.findViewById(R.id.dialog_list_icon);
        //TODO - check if performance is good. Only visible list is rendered so maybe ok
        displayIcon.setImageResource(res.getIdentifier(tz.getFlagResourceName(), "drawable", getContext().getPackageName()));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new TimeZoneFilter();
        }
        return filter;
    }

    /**
     * Implement filtering on list for searchText
     *
     * @author rahul
     */
    private class TimeZoneFilter extends Filter {
        /**
         * Based on the searchText provided the adapter's data is updated
         * Using {@link WorldClockTimeZone#getSearchString()} that 'contains' the searchText
         */
        @Override
        protected FilterResults performFiltering(CharSequence searchText) {
            FilterResults results = new FilterResults();

            if (searchText == null || searchText.length() == 0) {
                //no search text provided
                List<WorldClockTimeZone> list = new ArrayList<WorldClockTimeZone>(originalDataValues);
                results.values = list;
                results.count = list.size();
            } else {
                String searchStringLower = searchText.toString().toLowerCase();
                List<WorldClockTimeZone> fullSearchList = new ArrayList<WorldClockTimeZone>(originalDataValues);
                final List<WorldClockTimeZone> newValues = new ArrayList<WorldClockTimeZone>();

                for (WorldClockTimeZone tz : fullSearchList) {
                    if (tz.getSearchString().contains(searchStringLower)) {
                        newValues.add(tz);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        /**
         * Called with the filtered results. These must then be updated in the adapter and notified for change
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredDataValues = (List<WorldClockTimeZone>) results.values;

            clear();//clear underlying mObjects array
            for (int i = 0; i < filteredDataValues.size(); i++) {
                //add to underlying mObjects array
                add(filteredDataValues.get(i));
            }
            notifyDataSetChanged();
        }
    }
}


