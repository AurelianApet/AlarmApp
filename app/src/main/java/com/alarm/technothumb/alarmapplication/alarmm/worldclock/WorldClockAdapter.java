package com.alarm.technothumb.alarmapplication.alarmm.worldclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.AnalogClock;
import com.alarm.technothumb.alarmapplication.alarmm.SettingsActivity;
import com.alarm.technothumb.alarmapplication.alarmm.Utils;
import com.alarm.technothumb.alarmapplication.alarmm.widget.TextClock;

import java.text.Collator;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class WorldClockAdapter extends BaseAdapter {
    protected Object [] mCitiesList;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private String mClockStyle;
    private final Collator mCollator = Collator.getInstance();
    protected HashMap<String, CityObj> mCitiesDb = new HashMap<String, CityObj>();
    protected int mClocksPerRow;

    public WorldClockAdapter(Context context) {
        super();
        mContext = context;
        loadData(context);
        loadCitiesDb(context);
        mInflater = LayoutInflater.from(context);
        mClocksPerRow = context.getResources().getInteger(R.integer.world_clocks_per_row);
    }

    public void reloadData(Context context) {
        loadData(context);
        notifyDataSetChanged();
    }

    public void loadData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mClockStyle = prefs.getString(SettingsActivity.KEY_CLOCK_STYLE,
                mContext.getResources().getString(R.string.default_clock_style));
        mCitiesList = Cities.readCitiesFromSharedPrefs(prefs).values().toArray();
        sortList();
        mCitiesList = addHomeCity();
    }

    public void loadCitiesDb(Context context) {
        mCitiesDb.clear();
        // Read the cities DB so that the names and timezones will be taken from the DB
        // and not from the selected list so that change of locale or changes in the DB will
        // be reflected.
        CityObj[] cities = Utils.loadCitiesFromXml(context);
        if (cities != null) {
            for (int i = 0; i < cities.length; i ++) {
                mCitiesDb.put(cities[i].mCityId, cities [i]);
            }
        }
    }

    /**
     * Adds the home city as the first item of the adapter if the feature is on and the device time
     * zone is different from the home time zone that was set by the user.
     * return the list of cities.
     */
    private Object[] addHomeCity() {
        if (needHomeCity()) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String homeTZ = sharedPref.getString(SettingsActivity.KEY_HOME_TZ, "");
            CityObj c = new CityObj(
                    mContext.getResources().getString(R.string.home_label), homeTZ, null);
            Object[] temp = new Object[mCitiesList.length + 1];
            temp[0] = c;
            for (int i = 0; i < mCitiesList.length; i++) {
                temp[i + 1] = mCitiesList[i];
            }
            return temp;
        } else {
            return mCitiesList;
        }
    }

    public void updateHomeLabel(Context context) {
        // Update the "home" label if the home time zone clock is shown
        if (needHomeCity() && mCitiesList.length > 0) {
            ((CityObj) mCitiesList[0]).mCityName =
                    context.getResources().getString(R.string.home_label);
        }
    }

    public boolean needHomeCity() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (sharedPref.getBoolean(SettingsActivity.KEY_AUTO_HOME_CLOCK, false)) {
            String homeTZ = sharedPref.getString(
                    SettingsActivity.KEY_HOME_TZ, TimeZone.getDefault().getID());
            final Date now = new Date();
            return TimeZone.getTimeZone(homeTZ).getOffset(now.getTime())
                    != TimeZone.getDefault().getOffset(now.getTime());
        } else {
            return false;
        }
    }

    public boolean hasHomeCity() {
        return (mCitiesList != null) && mCitiesList.length > 0
                && ((CityObj) mCitiesList[0]).mCityId == null;
    }

    private void sortList() {
        final Date now = new Date();

        // Sort by the Offset from GMT taking DST into account
        // and if the same sort by City Name
        Arrays.sort(mCitiesList, new Comparator<Object>() {
            private int safeCityNameCompare(CityObj city1, CityObj city2) {
                if (city1.mCityName == null && city2.mCityName == null) {
                    return 0;
                } else if (city1.mCityName == null) {
                    return -1;
                } else if (city2.mCityName == null) {
                    return 1;
                } else {
                    return mCollator.compare(city1.mCityName, city2.mCityName);
                }
            }

            @Override
            public int compare(Object object1, Object object2) {
                CityObj city1 = (CityObj) object1;
                CityObj city2 = (CityObj) object2;
                if (city1.mTimeZone == null && city2.mTimeZone == null) {
                    return safeCityNameCompare(city1, city2);
                } else if (city1.mTimeZone == null) {
                    return -1;
                } else if (city2.mTimeZone == null) {
                    return 1;
                }

                int gmOffset1 = TimeZone.getTimeZone(city1.mTimeZone).getOffset(now.getTime());
                int gmOffset2 = TimeZone.getTimeZone(city2.mTimeZone).getOffset(now.getTime());
                if (gmOffset1 == gmOffset2) {
                    return safeCityNameCompare(city1, city2);
                } else {
                    return gmOffset1 - gmOffset2;
                }
            }
        });
    }

    @Override
    public int getCount() {
        if (mClocksPerRow == 1) {
            // In the special case where we have only 1 clock per view.
            return mCitiesList.length;
        }

        // Otherwise, each item in the list holds 1 or 2 clocks
        return (mCitiesList.length  + 1)/2;
    }

    @Override
    public Object getItem(int p) {
        return null;
    }

    @Override
    public long getItemId(int p) {
        return p;
    }

    @Override
    public boolean isEnabled(int p) {
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Index in cities list
        int index = position * mClocksPerRow;
        if (index < 0 || index >= mCitiesList.length) {
            return null;
        }

        if (view == null) {
            view = mInflater.inflate(R.layout.world_clock_list_item, parent, false);
        }
        // The world clock list item can hold two world clocks
        View rightClock = view.findViewById(R.id.city_right);
        updateView(view.findViewById(R.id.city_left), (CityObj)mCitiesList[index]);
        if (rightClock != null) {
            // rightClock may be null (landscape phone layout has only one clock per row) so only
            // process it if it exists.
            if (index + 1 < mCitiesList.length) {
                rightClock.setVisibility(View.VISIBLE);
                updateView(rightClock, (CityObj)mCitiesList[index + 1]);
            } else {
                // To make sure the spacing is right , make sure that the right clock style is
                // selected even if the clock is invisible.
                View dclock = rightClock.findViewById(R.id.digital_clock);
                View aclock = rightClock.findViewById(R.id.analog_clock);
                if (mClockStyle.equals("analog")) {
                    dclock.setVisibility(View.GONE);
                    aclock.setVisibility(View.INVISIBLE);
                } else {
                    dclock.setVisibility(View.INVISIBLE);
                    aclock.setVisibility(View.GONE);
                }
                // If there's only the one item, center it. If there are other items in the list,
                // keep it side-aligned.
                if (getCount() == 1) {
                    rightClock.setVisibility(View.GONE);
                } else {
                    rightClock.setVisibility(View.INVISIBLE);
                }
            }
        }

        return view;
    }

    private void updateView(View clock, CityObj cityObj) {
        View nameLayout= clock.findViewById(R.id.city_name_layout);
        TextView name = (TextView)(nameLayout.findViewById(R.id.city_name));
        TextView dayOfWeek = (TextView)(nameLayout.findViewById(R.id.city_day));
        TextClock dclock = (TextClock)(clock.findViewById(R.id.digital_clock));
        AnalogClock aclock = (AnalogClock)(clock.findViewById(R.id.analog_clock));
        View separator = clock.findViewById(R.id.separator);

        if (mClockStyle.equals("analog")) {
            dclock.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
            aclock.setVisibility(View.VISIBLE);
            aclock.setTimeZone(cityObj.mTimeZone);
            aclock.enableSeconds(false);
        } else {
            dclock.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
            aclock.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 17) {
                dclock.setTimeZone(cityObj.mTimeZone);
            }
            Utils.setTimeFormat(dclock,
                    (int)mContext.getResources().getDimension(R.dimen.label_font_size));
        }
        CityObj cityInDb = mCitiesDb.get(cityObj.mCityId);
        // Home city or city not in DB , use data from the save selected cities list
        name.setText(Utils.getCityName(cityObj, cityInDb));

        final Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getDefault());
        int myDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        // Get timezone from cities DB if available
        String cityTZ = (cityInDb != null) ? cityInDb.mTimeZone : cityObj.mTimeZone;
        now.setTimeZone(TimeZone.getTimeZone(cityTZ));
        int cityDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        if (myDayOfWeek != cityDayOfWeek) {
            dayOfWeek.setText(mContext.getString(R.string.world_day_of_week_label,
                    now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())));
            dayOfWeek.setVisibility(View.VISIBLE);
        } else {
            dayOfWeek.setVisibility(View.GONE);
        }
    }
}
