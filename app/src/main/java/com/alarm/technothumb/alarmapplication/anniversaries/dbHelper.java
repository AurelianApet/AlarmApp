package com.alarm.technothumb.alarmapplication.anniversaries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wdl-android on 13-04-2018.
 */

public class dbHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "anniversary";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;

    //Constants for identifying table and columns
    public static final String AnniversaryDate = "date";
    public static final String tableID = "Id";
    public static final String title = "title";
    public static final String category = "category";
    public static final String AnniversaryID = "AnniID";
    public static final int AnniID = 5;
    public static final String TABLE_NAME = "anniversary";
    public static final String[] ALL_COLUMNS =
            {AnniversaryDate, title, category};//

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    tableID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AnniversaryID + " INTERGER, " +
                    category + " TEXT, " +
                    title + " TEXT, " +
                    AnniversaryDate + " TEXT" +

                    ")";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public void insertData(anniversaryModel getset) {

        sqLiteDatabase = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(AnniversaryDate, getset.getDate());
        cv.put(title, getset.getTitle());
        cv.put(category, getset.getCategory());
        cv.put(AnniversaryID, getset.getAnniID());


        sqLiteDatabase.insert(TABLE_NAME, null, cv);
        sqLiteDatabase.close();


    }

    public List<anniversaryModel> getData() {
        List<anniversaryModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                anniversaryModel c = new anniversaryModel();
                c.setTitleID(cursor.getInt(0));
                c.setAnniID(cursor.getInt(1));
                c.setCategory(cursor.getString(2));
                c.setTitle(cursor.getString(3));

                c.setDate(cursor.getString(4));

                list.add(c);
            } while (cursor.moveToNext());
        }

        return list;
    }

    public void isdelect(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, tableID+" = ?", new String[]{String.valueOf(id)});
        db.close();

        Log.e("id", id+"");
    }

    public boolean isExistpersonID(int PersonID,String UserDocumentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor  cur = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE UserDocumentId=" + UserDocumentId + " and persondid = '" + PersonID + "'", null);

        boolean exist = (cur.getCount() > 0);
        cur.close();
        db.close();
        return exist;
    }


    public int updateData(anniversaryModel getset) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AnniversaryDate, getset.getDate());
        values.put(title, getset.getTitle());
        values.put(category, getset.getCategory());
        values.put(AnniversaryID, getset.getAnniID());

        int tableID1 = getset.getTitleID();
        Log.e("tableID", tableID1+"");
        // updating row
        return db.update(TABLE_NAME, values, tableID + " = ?",
                new String[] { String.valueOf(tableID1) });
    }




   /* public int isdelect(int id) {

        Log.e("id", id+"");

  //       return sqLiteDatabase.delete(TABLE_NAME,  tableID + "=" + id, null);

        return id;
    }*/
}

