package com.alarm.technothumb.alarmapplication.travel_record;

import android.provider.BaseColumns;

/**
 * Created by NIKUNJ on 12-02-2018.
 */

public class DbContract {

    public DbContract(){}

    public static abstract class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_NAME = "name";
        public static final String LATI = "lati";
        public static final String LONGI = "longi";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String DATETIME ="datetime";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_TRASH = "in_trash";
        public static final int TYPE_AUDIO = 2;


    }

    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String LATI = "lati";
        public static final String LONGI = "longi";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String DATETIME = "datetime";
    }

    public static abstract class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notifications";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_TIME = "time";
    }
}
