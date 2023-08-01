package com.alarm.technothumb.alarmapplication.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.Log;
import com.alarm.technothumb.alarmapplication.anniversaries.dbHelper;

/**
 * Created by NIKUNJ on 29-01-2018.
 */

public class DbAccess {

    /**
     * Returns a specific text note
     * @param c the current context
     * @param id the id of the note
     * @return the cursor to the note
     */
    public static Cursor getNote(Context c, int id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = { DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_CATEGORY,  DbContract.NoteEntry.COLUMN_COLOR , DbContract.NoteEntry.COLUMN_PRIORITY };
        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {"" + id};

       // android.util.Log.e("IDLLLL",id+"");
        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                      // Columns for WHERE
                selectionArgs,                  // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);                     // Sort Order
    }

    /**
     * Inserts a new text note into the database.
     * @param c the current context.
     * @param name the name of the note
     * @param content the content of the note
     */


    /**
     * Updates a text note in the database.
     * @param c the current context
     * @param id the id of the note
     * @param name the new name of the note
     * @param content the new content of the note
     * @param dbPriority
     */
    public static void updateNote(Context c, int id, String name, String content,  int ColorID, int dbPriority) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NoteEntry.COLUMN_NAME, name);
        values.put(DbContract.NoteEntry.COLUMN_CONTENT, content);
      //  values.put(DbContract.NoteEntry.COLUMN_CATEGORY, category);
        values.put(DbContract.NoteEntry.COLUMN_COLOR, ColorID);
        values.put(DbContract.NoteEntry.COLUMN_PRIORITY, dbPriority);
        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        db.update(DbContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public static void updateNoteColor(Context c, int id, int ColorID) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //  values.put(DbContract.NoteEntry.COLUMN_CATEGORY, category);
        values.put(DbContract.NoteEntry.COLUMN_COLOR, ColorID);
        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.update(DbContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    /**
     * update note background on multiselectTime
     *
     */
    public static void updateNoteColor(Context c, int id,String colorId) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.NoteEntry.COLUMN_COLOR, colorId);
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
        DbOpenHelper dbHelper = new DbOpenHelper(c);
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
     * Restores a note from the trash
     * @param c the current context
     * @param id the id of the note
     */
    public static void restoreNote(Context c, int id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NoteEntry.COLUMN_TRASH, 0);
        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        db.update(DbContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    /**
     * Deletes a  text note from the database.
     * @param c the current context
     * @param id the ID of the note
     */
    public static void deleteNote(Context c, int id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //TODO delete the file for sketch and audio

        String selection = DbContract.NoteEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(DbContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        deleteNotificationsByNoteId(c, id);
    }

    /**
     * Delete notes by specifying the category id
     * @param c the current context
     * @param cat_id the category id
     */
    public static void trashNotesByCategoryId(Context c, int cat_id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Selection arguments for all the notes belonging to that category
        String selection = DbContract.NoteEntry.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = { String.valueOf(cat_id) };
        //Temporary save them
        Cursor cur = getCursorAllNotes(c, selection, selectionArgs);
        if (cur.getCount() > 0) {
            while(cur.moveToNext()) {
                trashNote(c, cur.getInt(cur.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID)));
            }
        }

    }

    /**
     * Returns a cursor over all the notes in the database.
     * @param c the current context
     * @return A {@link android.database.Cursor} over all the notes
     */
    public static Cursor getCursorAllNotes(Context c) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_CATEGORY, DbContract.NoteEntry.COLUMN_COLOR, DbContract.NoteEntry.COLUMN_PRIORITY};

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
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_CATEGORY, DbContract.NoteEntry.COLUMN_COLOR, DbContract.NoteEntry.COLUMN_PRIORITY };

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
     * @return A {@link android.database.Cursor} over all the notes
     */
    public static Cursor getCursorAllNotesAlphabetical(Context c) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_COLOR};

        String sortOrder = DbContract.NoteEntry.COLUMN_NAME + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                null,                           // Columns for WHERE
                null,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }

    /**
     * Returns a cursor over all the notes in the database in alphabetical ordering.
     * @param c the current context
     * @param selection the selection string to use with the query
     * @param selectionArgs the selection arguments to use with the query
     * @return A {@link android.database.Cursor} over all the notes
     */
    public static Cursor getCursorAllNotesAlphabetical(Context c, String selection, String[] selectionArgs) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_CATEGORY, DbContract.NoteEntry.COLUMN_COLOR, DbContract.NoteEntry.COLUMN_PRIORITY};

        String sortOrder = DbContract.NoteEntry.COLUMN_NAME + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }

    public static Cursor getCursorAllNotesColor(Context c, String selection, String[] selectionArgs) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_CATEGORY, DbContract.NoteEntry.COLUMN_COLOR, DbContract.NoteEntry.COLUMN_PRIORITY};

        String sortOrder = DbContract.NoteEntry.COLUMN_COLOR + " COLLATE NOCASE ASC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);                     // Sort Order
    }

    public static Cursor getCursorAllNotesPriority(Context baseContext, String selection, String[] selectionArgs) {
        DbOpenHelper dbHelper = new DbOpenHelper(baseContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_CATEGORY, DbContract.NoteEntry.COLUMN_COLOR, DbContract.NoteEntry.COLUMN_PRIORITY};

        String sortOrder = DbContract.NoteEntry.COLUMN_PRIORITY + " COLLATE NOCASE DESC";

        return db.query(DbContract.NoteEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);

    }

    public static Cursor getCursorAllNotesCreated(Context c, String selection, String[] selectionArgs) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NoteEntry.COLUMN_ID, DbContract.NoteEntry.COLUMN_TYPE, DbContract.NoteEntry.COLUMN_NAME, DbContract.NoteEntry.COLUMN_CONTENT, DbContract.NoteEntry.COLUMN_CATEGORY, DbContract.NoteEntry.COLUMN_COLOR, DbContract.NoteEntry.COLUMN_PRIORITY};

        String sortOrder = DbContract.NoteEntry.COLUMN_ID + " COLLATE NOCASE ASC";

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
        DbOpenHelper dbHelper = new DbOpenHelper(c);
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

    /**
     * Returns a cursor over all the categories in the database. Does not include the default category.
     * @param c the current context
     * @return A {@link android.database.Cursor} over all the notes
     */
    public static Cursor getCategoriesWithoutDefault(Context c){
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.CategoryEntry.COLUMN_ID, DbContract.CategoryEntry.COLUMN_NAME};
        String selection = DbContract.CategoryEntry.COLUMN_NAME + " != ?";
        String[] selectionArgs = { c.getString(R.string.default_category) };

        return db.query(DbContract.CategoryEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                      // Columns for WHERE
                selectionArgs,                  // Values for WHERE
                null,                           // Group
                null,                           // Filter by
                null);                     // Sort Order
    }

    /**
     * Inserts a new category into the database.
     * @param c the current context.
     * @param name the name of the category
     */
    public static boolean addCategory(Context c, String name) {
        boolean success = true;
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.CategoryEntry.COLUMN_NAME, name.trim());
        try {
            db.insertOrThrow(DbContract.CategoryEntry.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            success = false;
        }
        db.close();
        return success;
    }

    /**
     * Deletes a  category from the database.
     * @param c the current context
     * @param id the ID of the category
     */
    public static void deleteCategory(Context c, int id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //delete the category
        String selection = DbContract.CategoryEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(DbContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);

        //delete the id from the notes
        ContentValues values = new ContentValues();
        values.putNull(DbContract.NoteEntry.COLUMN_CATEGORY);
        String selection2 = DbContract.NoteEntry.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs2 = { String.valueOf(id) };
        db.update(DbContract.NoteEntry.TABLE_NAME, values, selection2, selectionArgs2);

        db.close();
    }

    /**
     * Inserts a new Notification into the database
     * @param c the current context
     * @param note_id the id of the note
     * @return the rowID
     */
    public static long addNotification(Context c, int note_id, long time,String uri) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NotificationEntry.COLUMN_NOTE, note_id);
        values.put(DbContract.NotificationEntry.COLUMN_TIME, time);
        values.put(DbContract.NotificationEntry.COLUMN_RINGTONE, uri);
        //values.put(DbContract.NotificationEntry.COLUMN_RINGTONE, ringtone.toString());
        long rowId = db.insert(DbContract.NotificationEntry.TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    /**
     * Updates the time of a notification
     * @param c the current context
     * @param id the notification id
     * @param time the new time
     */
    public static void updateNotificationTime(Context c, int id, long time, String uri) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NotificationEntry.COLUMN_TIME, time);
        values.put(DbContract.NotificationEntry.COLUMN_RINGTONE, uri);
        String selection = DbContract.NotificationEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.update(DbContract.NotificationEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }
    public static void updateNotificationRing(Context c, int id, String uri) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
         values.put(DbContract.NotificationEntry.COLUMN_RINGTONE, uri);
        String selection = DbContract.NotificationEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.update(DbContract.NotificationEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    /**
     * Returns a specific notification
     * @param c the current context
     * @param id the id of the notification
     * @return the cursor to the notification
     */
    public static Cursor getNotification(Context c, int id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NotificationEntry.COLUMN_ID, DbContract.NotificationEntry.COLUMN_NOTE, DbContract.NotificationEntry.COLUMN_TIME, DbContract.NotificationEntry.COLUMN_RINGTONE};
        String selection = DbContract.NotificationEntry.COLUMN_NOTE + " = ?";
        String[] selectionArgs =  { String.valueOf(id) };

        return db.query(DbContract.NotificationEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                      // Columns for WHERE
                selectionArgs,                  // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);                     // Sort Order
    }



    /**
     * Deletes a notification from the database
     * @param c the current context
     * @param id the id of the notification
     */
    public static void deleteNotification(Context c, int id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = DbContract.NotificationEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(DbContract.NotificationEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    /**
     * Get a notification by its note id
     * @param c the current context
     * @param note_id the note id
     * @return the cursor to the notification
     */
    public static Cursor getNotificationByNoteId(Context c, int note_id) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NotificationEntry.COLUMN_ID, DbContract.NotificationEntry.COLUMN_NOTE, DbContract.NotificationEntry.COLUMN_TIME};
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
        DbOpenHelper dbHelper = new DbOpenHelper(c);
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
    public static Cursor getAllNotifications(Context c) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DbContract.NotificationEntry.COLUMN_ID, DbContract.NotificationEntry.COLUMN_NOTE, DbContract.NotificationEntry.COLUMN_TIME};

        return db.query(DbContract.NotificationEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                null,                           // Columns for WHERE
                null,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);                     // Sort Order
    }

    public static int addNote(Context c, String name, String content, int type,  int ColorID, int dbPriority) {
        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.NoteEntry.COLUMN_TYPE, type);
        values.put(DbContract.NoteEntry.COLUMN_NAME, name);
        values.put(DbContract.NoteEntry.COLUMN_CONTENT, content);
      //  values.put(DbContract.NoteEntry.COLUMN_CATEGORY, category);
        values.put(DbContract.NoteEntry.COLUMN_COLOR, ColorID);
        values.put(DbContract.NoteEntry.COLUMN_PRIORITY, dbPriority);
        int id = (int)(long)db.insert(DbContract.NoteEntry.TABLE_NAME, null, values);
        db.close();
        return id;


    }

    public static Cursor getAllAnniNotifications(Context context) {
        dbHelper Helper = new dbHelper(context);
        SQLiteDatabase db = Helper.getReadableDatabase();

        String[] projection = {Helper.AnniversaryID,Helper.tableID, Helper.AnniversaryDate};

        return db.query(DbContract.NotificationEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                null,                           // Columns for WHERE
                null,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);

    }

    public static Cursor getAnniverseryNotification(Context baseContext, int notification_id) {


        dbHelper Helper = new dbHelper(baseContext);
        SQLiteDatabase db = Helper.getReadableDatabase();

        String[] projection = {Helper.AnniversaryID,Helper.tableID, Helper.AnniversaryDate};
        String selection = Helper.AnniversaryID + " = ?";
        String[] selectionArgs =  { String.valueOf(notification_id) };

        return db.query(DbContract.NotificationEntry.TABLE_NAME,   // Table name
                projection,                     // SELECT
                selection,                      // Columns for WHERE
                selectionArgs,                  // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                null);
    }


}

