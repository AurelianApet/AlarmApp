package com.alarm.technothumb.alarmapplication.alarmm.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public final class Alarm implements Parcelable, ClockContract.AlarmsColumns {
    /**
     * Alarms start with an invalid id and timestamp when it hasn't been saved to the database.
     */
    public static final long INVALID_ID = -1;
    public static final long INVALID_TIMESTAMP = -1;

    /**
     * The default sort order for this table
     */
    private static final String DEFAULT_SORT_ORDER =
            HOUR + ", " +
                    MINUTES + " ASC" + ", " +
                    _ID + " DESC";

    private static final String[] QUERY_COLUMNS = {
            _ID,
            HOUR,
            MINUTES,
            DAYS_OF_WEEK,
            ENABLED,
            VIBRATE,
            LABEL,
            RINGTONE,
            DELETE_AFTER_USE,
            LIGHTUPPI_ID,
            TIMESTAMP
    };

    /**
     * These save calls to cursor.getColumnIndexOrThrow()
     * THEY MUST BE KEPT IN SYNC WITH ABOVE QUERY COLUMNS
     */
    private static final int ID_INDEX = 0;
    private static final int HOUR_INDEX = 1;
    private static final int MINUTES_INDEX = 2;
    private static final int DAYS_OF_WEEK_INDEX = 3;
    private static final int ENABLED_INDEX = 4;
    private static final int VIBRATE_INDEX = 5;
    private static final int LABEL_INDEX = 6;
    private static final int RINGTONE_INDEX = 7;
    private static final int DELETE_AFTER_USE_INDEX = 8;
    private static final int LIGHTUPPI_ID_INDEX = 9;
    private static final int TIMESTAMP_INDEX = 10;

    private static final int COLUMN_COUNT = TIMESTAMP_INDEX + 1;

    public static ContentValues createContentValues(Alarm alarm) {
        ContentValues values = new ContentValues(COLUMN_COUNT);
        if (alarm.id != INVALID_ID) {
            values.put(_ID, alarm.id);
        }

        values.put(ENABLED, alarm.enabled ? 1 : 0);
        values.put(HOUR, alarm.hour);
        values.put(MINUTES, alarm.minutes);
        values.put(DAYS_OF_WEEK, alarm.daysOfWeek.getBitSet());
        values.put(VIBRATE, alarm.vibrate ? 1 : 0);
        values.put(LABEL, alarm.label);
        values.put(DELETE_AFTER_USE, alarm.deleteAfterUse);
        values.put(LIGHTUPPI_ID, alarm.lightuppiId);
        values.put(TIMESTAMP, alarm.timestamp);
        if (alarm.alert == null) {
            // We want to put null, so default alarm changes
            values.putNull(RINGTONE);
        } else {
            values.put(RINGTONE, alarm.alert.toString());
        }

        return values;
    }

    public static Intent createIntent(String action, long alarmId) {
        return new Intent(action).setData(getUri(alarmId));
    }

    public static Intent createIntent(Context context, Class<?> cls, long alarmId) {
        return new Intent(context, cls).setData(getUri(alarmId));
    }

    public static Uri getUri(long alarmId) {
        return ContentUris.withAppendedId(CONTENT_URI, alarmId);
    }

    public static long getId(Uri contentUri) {
        return ContentUris.parseId(contentUri);
    }

    /**
     * Get alarm cursor loader for all alarms.
     *
     * @param context to query the database.
     * @return cursor loader with all the alarms.
     */
    public static CursorLoader getAlarmsCursorLoader(Context context) {
        return new CursorLoader(context, ClockContract.AlarmsColumns.CONTENT_URI,
                QUERY_COLUMNS, null, null, DEFAULT_SORT_ORDER);
    }

    /**
     * Get alarm by id.
     *
     * @param contentResolver to perform the query on.
     * @param alarmId for the desired alarm.
     * @return alarm if found, null otherwise
     */
    public static Alarm getAlarm(ContentResolver contentResolver, long alarmId) {
        Cursor cursor = contentResolver.query(getUri(alarmId), QUERY_COLUMNS, null, null, null);
        Alarm result = null;
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                result = new Alarm(cursor);
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    /**
     * Get alarm by LightUpPi ID.
     *
     * @param contentResolver to perform the query on.
     * @param lightuppiId ID for the LightUpPi alarm to find.
     * @return alarm if found, null otherwise
     */
    public static Alarm getAlarmLightuppiId(ContentResolver contentResolver, long lightuppiId) {
        Cursor cursor = contentResolver.query(
                CONTENT_URI, QUERY_COLUMNS, Alarm.LIGHTUPPI_ID + "=" + lightuppiId, null, null);
        if (cursor == null) {
            return null;
        }

        Alarm result = null;
        try {
            if (cursor.moveToFirst()) {
                result = new Alarm(cursor);
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    /**
     * Get all alarms given conditions.
     *
     * @param contentResolver to perform the query on.
     * @param selection A filter declaring which rows to return, formatted as an
     *         SQL WHERE clause (excluding the WHERE itself). Passing null will
     *         return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *         replaced by the values from selectionArgs, in the order that they
     *         appear in the selection. The values will be bound as Strings.
     * @return list of alarms matching where clause or empty list if none found.
     */
    public static List<Alarm> getAlarms(ContentResolver contentResolver,
                                        String selection, String ... selectionArgs) {
        Cursor cursor  = contentResolver.query(CONTENT_URI, QUERY_COLUMNS,
                selection, selectionArgs, null);
        List<Alarm> result = new LinkedList<Alarm>();
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    result.add(new Alarm(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public static Alarm addAlarm(ContentResolver contentResolver, Alarm alarm) {
        ContentValues values = createContentValues(alarm);
        // Timestamp is used mostly to be able to synchronised alarms, so if it already contains
        // a specified timestamp, keep it.
        if (alarm.timestamp == INVALID_TIMESTAMP) {
            alarm.timestamp = (long)(System.currentTimeMillis()/1000);
        }
        Uri uri = contentResolver.insert(CONTENT_URI, values);
        alarm.id = getId(uri);
        return alarm;
    }

    public static boolean updateAlarm(
            ContentResolver contentResolver, Alarm alarm, final boolean... bypassTimestamp) {
        if (alarm.id == Alarm.INVALID_ID) return false;
        if (bypassTimestamp.length <= 0 || !bypassTimestamp[0]) {
            alarm.timestamp = (long) (System.currentTimeMillis() / 1000);
            if (Log.LOGV) Log.d("Alarm " + alarm.id + "/" + alarm.lightuppiId + " new timestamp");
        }
        ContentValues values = createContentValues(alarm);
        long rowsUpdated = contentResolver.update(getUri(alarm.id), values, null, null);
        return rowsUpdated == 1;
    }

    public static boolean deleteAlarm(ContentResolver contentResolver, long alarmId) {
        if (alarmId == INVALID_ID) return false;
        int deletedRows = contentResolver.delete(getUri(alarmId), "", null);
        return deletedRows == 1;
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel p) {
            return new Alarm(p);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    // Public fields
    public long id;
    public boolean enabled;
    public int hour;
    public int minutes;
    public DaysOfWeek daysOfWeek;
    public boolean vibrate;
    public String label;
    public Uri alert;
    public boolean deleteAfterUse;
    public long lightuppiId;
    public long timestamp;

    // Creates a default alarm at the current time.
    public Alarm() {
        this(0, 0);
    }

    public Alarm(int hour, int minutes) {
        this.id = INVALID_ID;
        this.hour = hour;
        this.minutes = minutes;
        this.vibrate = true;
        // TODO: By default we should have full repeat
        this.daysOfWeek = new DaysOfWeek(0);
        this.label = "";
        this.alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        this.deleteAfterUse = false;
        this.lightuppiId = INVALID_ID;
        this.timestamp = INVALID_TIMESTAMP;
    }

    public Alarm(Cursor c) {
        id = c.getLong(ID_INDEX);
        enabled = c.getInt(ENABLED_INDEX) == 1;
        hour = c.getInt(HOUR_INDEX);
        minutes = c.getInt(MINUTES_INDEX);
        daysOfWeek = new DaysOfWeek(c.getInt(DAYS_OF_WEEK_INDEX));
        vibrate = c.getInt(VIBRATE_INDEX) == 1;
        label = c.getString(LABEL_INDEX);
        deleteAfterUse = c.getInt(DELETE_AFTER_USE_INDEX) == 1;
        lightuppiId = c.getLong(LIGHTUPPI_ID_INDEX);
        timestamp = c.getLong(TIMESTAMP_INDEX);

        if (c.isNull(RINGTONE_INDEX)) {
            // Should we be saving this with the current ringtone or leave it null
            // so it changes when user changes default ringtone?
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        } else {
            alert = Uri.parse(c.getString(RINGTONE_INDEX));
        }
    }

    Alarm(Parcel p) {
        id = p.readLong();
        enabled = p.readInt() == 1;
        hour = p.readInt();
        minutes = p.readInt();
        daysOfWeek = new DaysOfWeek(p.readInt());
        vibrate = p.readInt() == 1;
        label = p.readString();
        alert = (Uri) p.readParcelable(null);
        deleteAfterUse = p.readInt() == 1;
        lightuppiId = p.readLong();
        timestamp = p.readLong();
    }

    public String getLabelOrDefault(Context context) {
        if (label == null || label.length() == 0) {
            return context.getString(R.string.default_label);
        }
        return label;
    }

    public void writeToParcel(Parcel p, int flags) {
        p.writeLong(id);
        p.writeInt(enabled ? 1 : 0);
        p.writeInt(hour);
        p.writeInt(minutes);
        p.writeInt(daysOfWeek.getBitSet());
        p.writeInt(vibrate ? 1 : 0);
        p.writeString(label);
        p.writeParcelable(alert, flags);
        p.writeInt(deleteAfterUse ? 1 : 0);
        p.writeLong(lightuppiId);
        p.writeLong(timestamp);
    }

    public int describeContents() {
        return 0;
    }

    public AlarmInstance createInstanceAfter(Calendar time) {
        Calendar nextInstanceTime = Calendar.getInstance();
        nextInstanceTime.set(Calendar.YEAR, time.get(Calendar.YEAR));
        nextInstanceTime.set(Calendar.MONTH, time.get(Calendar.MONTH));
        nextInstanceTime.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH));
        nextInstanceTime.set(Calendar.HOUR_OF_DAY, hour);
        nextInstanceTime.set(Calendar.MINUTE, minutes);
        nextInstanceTime.set(Calendar.SECOND, 0);
        nextInstanceTime.set(Calendar.MILLISECOND, 0);

        // If we are still behind the passed in time, then add a day
        if (nextInstanceTime.getTimeInMillis() <= time.getTimeInMillis()) {
            nextInstanceTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        // The day of the week might be invalid, so find next valid one
        int addDays = daysOfWeek.calculateDaysToNextAlarm(nextInstanceTime);
        if (addDays > 0) {
            nextInstanceTime.add(Calendar.DAY_OF_WEEK, addDays);
        }

        AlarmInstance result = new AlarmInstance(nextInstanceTime, id);
        result.mVibrate = vibrate;
        result.mLabel = label;
        result.mRingtone = alert;
        result.mLightuppiId = lightuppiId;
        result.mTimestamp = timestamp;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Alarm)) return false;
        final Alarm other = (Alarm) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public String toString() {
        Date timestampDate = new Date(timestamp * 1000);
        return "Alarm{" +
                "alert=" + alert +
                ", id=" + id +
                ", enabled=" + enabled +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", daysOfWeek=" + daysOfWeek +
                ", vibrate=" + vibrate +
                ", label='" + label + '\'' +
                ", deleteAfterUse=" + deleteAfterUse +
                ", lightuppiId=" + lightuppiId +
                ", timestamp=" + timestampDate.toString() +
                '}';
    }
}
