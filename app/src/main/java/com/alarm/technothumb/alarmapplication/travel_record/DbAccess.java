package com.alarm.technothumb.alarmapplication.travel_record;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alarm.technothumb.alarmapplication.alarmm.Log;

/**
 * Created by NIKUNJ on 12-02-2018.
 */

public class DbAccess {

    /**
     * Returns a specific text note
     * @param c the current context
     * @param id the id of the note
     * @return the cursor to the note
     */
    public static Cursor getNote(Context c, int id) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = { DbContract.NoteEntry.COLUMN_ID,
                DbContract.NoteEntry.COLUMN_TYPE,
                DbContract.NoteEntry.COLUMN_NAME,
                DbContract.NoteEntry.LATI,
                DbContract.NoteEntry.LONGI,
                DbContract.NoteEntry.COUNTRY,
                DbContract.NoteEntry.CITY,
                DbContract.NoteEntry.STATE,
                DbContract.NoteEntry.DATETIME,
                DbContract.NoteEntry.COLUMN_CONTENT,
                DbContract.NoteEntry.COLUMN_CATEGORY };
        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {"" + id};

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                      // Columns for WHERE
                selectionArgs,                  // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);                     // Sort Order
    }


    public static int addNote(Context c, String name, String content, int type, int category, String latitude,
                              String longitude,String country,String city,String state, String currentDateTime){
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NoteEntry.COLUMN_TYPE, type);
        values.put(DbContract.NoteEntry.COLUMN_NAME, name);
        values.put(DbContract.NoteEntry.LATI,latitude);
        values.put(DbContract.NoteEntry.LONGI,longitude);
        values.put(DbContract.NoteEntry.COUNTRY,country);
        values.put(DbContract.NoteEntry.CITY,city);
        values.put(DbContract.NoteEntry.STATE,state);
        values.put(DbContract.NoteEntry.DATETIME,currentDateTime);
        values.put(DbContract.NoteEntry.COLUMN_CONTENT, content);
        values.put(DbContract.NoteEntry.COLUMN_CATEGORY, category);




        int id = (int)(long)db.insert(DbContract.NoteEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }


    public static void updateNote(Context c, int id, String name, String content, int category, String latitude,
                                  String longitude,String country,String city,String state, String currentDateTime) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NoteEntry.COLUMN_NAME, name);
        values.put(DbContract.NoteEntry.COLUMN_CONTENT, content);
        values.put(DbContract.NoteEntry.COLUMN_CATEGORY, category);
        values.put(DbContract.NoteEntry.LATI,latitude);
        values.put(DbContract.NoteEntry.LONGI,longitude);
        values.put(DbContract.NoteEntry.COUNTRY,country);
        values.put(DbContract.NoteEntry.CITY,city);
        values.put(DbContract.NoteEntry.STATE,state);
        values.put(DbContract.NoteEntry.DATETIME,currentDateTime);
        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        db.update(DbContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    /**
     * Moves a note to trash
     * @param c the current context
     * @param id the id of the note
     */
    public static void trashNote(Context c, int id) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NoteEntry.COLUMN_TRASH, 1);
        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        db.update(DbContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        deleteNotificationsByNoteId(c, id);
    }



    /**
     * Returns a cursor over all the notes in the database.
     * @param c the current context
     * @return A {@link android.database.Cursor} over all the notes
     */
    public static Cursor getCursorAllNotes(Context c) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME,
                DbContract.NoteEntry.LATI,DbContract.NoteEntry.LONGI,DbContract.NoteEntry.COUNTRY,DbContract.NoteEntry.CITY,
                DbContract.NoteEntry.STATE,DbContract.NoteEntry.DATETIME,DbContract.NoteEntry.COLUMN_CONTENT};

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                null,                           // Columns for WHERE
                null,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);                     // Sort Order
    }

    /**
     * Returns a cursor over all the notes in the database.
     * @param c the current context
     * @param selection the selection string to use with the query
     * @param selectionArgs the selection arguments to use with the query
     * @return A {@link android.database.Cursor} over all the notes
     */
    public static Cursor getCursorAllNotes(Context c, String selection, String[] selectionArgs) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME,
                DbContract.NoteEntry.LATI,DbContract.NoteEntry.LONGI, DbContract.NoteEntry.COUNTRY,
                DbContract.NoteEntry.CITY,DbContract.NoteEntry.STATE,DbContract.NoteEntry.DATETIME,DbContract.NoteEntry.COLUMN_CONTENT};

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);                     // Sort Order
    }


    /**
     * Returns a cursor over all the notes in the database in alphabetical ordering.
     * @param c the current context
     * @param selection the selection string to use with the query
     * @param selectionArgs the selection arguments to use with the query
     * @return A {@link android.database.Cursor} over all the notes
     */

//    sort By Alphabet
    public static Cursor getCursorAllNotesAlphabetical(Context c, String selection, String[] selectionArgs) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE,
                DbContract.NoteEntry.COLUMN_NAME,DbContract.NoteEntry.LATI,DbContract.NoteEntry.LONGI,
                DbContract.NoteEntry.COUNTRY,DbContract.NoteEntry.CITY,DbContract.NoteEntry.STATE,DbContract.NoteEntry.DATETIME, DbContract.NoteEntry.COLUMN_CONTENT};

        String sortOrder = DbContract.NoteEntry.COLUMN_NAME + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }

//    sort By Date
    public static Cursor getCursorAllNotesDate(Context c, String selection, String[] selectionArgs) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE,
                DbContract.NoteEntry.COLUMN_NAME,DbContract.NoteEntry.LATI,DbContract.NoteEntry.LONGI,
                DbContract.NoteEntry.COUNTRY,DbContract.NoteEntry.CITY,DbContract.NoteEntry.STATE,DbContract.NoteEntry.DATETIME, DbContract.NoteEntry.COLUMN_CONTENT};

        String sortOrder = DbContract.NoteEntry.DATETIME + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }

//    sort By Country
    public static Cursor getCursorAllNotesCountry(Context c, String selection, String[] selectionArgs) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE,
                DbContract.NoteEntry.COLUMN_NAME,DbContract.NoteEntry.LATI,DbContract.NoteEntry.LONGI,
                DbContract.NoteEntry.COUNTRY,DbContract.NoteEntry.CITY,DbContract.NoteEntry.STATE,
                DbContract.NoteEntry.DATETIME, DbContract.NoteEntry.COLUMN_CONTENT};

        String sortOrder = DbContract.NoteEntry.COUNTRY + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }

    public static Cursor getCursorAllNotesRegion(Context c, String selection, String[] selectionArgs) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE,
                DbContract.NoteEntry.COLUMN_NAME,DbContract.NoteEntry.LATI,DbContract.NoteEntry.LONGI,
                DbContract.NoteEntry.COUNTRY,DbContract.NoteEntry.CITY,DbContract.NoteEntry.STATE,
                DbContract.NoteEntry.DATETIME, DbContract.NoteEntry.COLUMN_CONTENT};

        String sortOrder = DbContract.NoteEntry.STATE + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }

//    sort By City
    public static Cursor getCursorAllNotesCity(Context c, String selection, String[] selectionArgs) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE,
                DbContract.NoteEntry.COLUMN_NAME,DbContract.NoteEntry.LATI,DbContract.NoteEntry.LONGI,
                DbContract.NoteEntry.COUNTRY,DbContract.NoteEntry.CITY,DbContract.NoteEntry.STATE,
                DbContract.NoteEntry.DATETIME, DbContract.NoteEntry.COLUMN_CONTENT};

        String sortOrder = DbContract.NoteEntry.CITY + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }


    /**
     * Returns a cursor over all the categories in the database.
     * @param c the current context
     * @return A {@link android.database.Cursor} over all the notes
     */
    public static Cursor getCategories(Context c){
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.CategoryEntry.COLUMN_ID, DbContract.CategoryEntry.COLUMN_NAME};

        return db.query(DbContract.CategoryEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                null,                           // Columns for WHERE
                null,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by
                null);                     // Sort Order
    }




    public static Cursor getNotificationByNoteId(Context c, int note_id) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NotificationEntry.COLUMN_ID, DbContract.NotificationEntry.COLUMN_NOTE,
                DbContract.NotificationEntry.COLUMN_TIME};
        String selection = DbContract.NotificationEntry.COLUMN_NOTE + " = ?";
        String[] selectionArgs = { String.valueOf(note_id) };

        return db.query(DbContract.NotificationEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                      // Columns for WHERE
                selectionArgs,                  // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);                     // Sort Order
    }

    /**
     * Delete notifications by specifying the note id
     * @param c the current context
     * @param note_id the note id
     */
    public static void deleteNotificationsByNoteId(Context c, int note_id) {
        DbOpenHelperr dbHelper = new DbOpenHelperr(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = DbContract.NotificationEntry.COLUMN_NOTE + " = ?";
        String[] selectionArgs = { String.valueOf(note_id) };
        db.delete(DbContract.NotificationEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    /**
     * Returns a cursor over all the notifications in the database.
     * @param c the current context
     * @return A {@link android.database.Cursor} over all the notes
     */

}

