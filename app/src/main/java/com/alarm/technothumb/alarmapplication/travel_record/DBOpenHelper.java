package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alarm.technothumb.alarmapplication.note.DbContract;
import com.alarm.technothumb.alarmapplication.note.DbOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIKUNJ on 09-02-2018.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "TravellingRecord";
    private static final int DATABASE_VERSION = 4;
    private SQLiteDatabase sqLiteDatabase;

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String Photopath = "path";
    public static final String Latitude = "latitude";
    public static final String Longitude = "longitude";
    public static final String DateTime = "datetime";
    public static final String Address = "address";
    public static final String city = "city";
    public static final String state = "state";
    public static final String country = "country";
    public static final String AUDIOFILE = "audiofile";
    public static final String Type = "Type";
    public static final String NOTE_CREATED = "noteCreated";

    public static final String[] ALL_COLUMNS =
            {NOTE_ID, NOTE_TEXT,Photopath, Latitude, Longitude,DateTime,AUDIOFILE, NOTE_CREATED};//

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                    NOTE_TEXT + " TEXT, " +
                    Photopath + " TEXT, " +
                    Latitude + " TEXT, " +
                    Longitude + " TEXT, " +
                    DateTime + " TEXT, " +
                    AUDIOFILE + " TEXT," +
                    Address + " TEXT," +
                    city + " TEXT," +
                    state + " TEXT," +
                    country + " TEXT," +
                    Type + " TEXT," +
                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }


    public void insertData(String noteText, String photoPath, Double latitude, Double longitude, String currentDateTime, String address,String City, String State, String Country, String audioFile, int type) {

        sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(DBOpenHelper.C_ID,c_id);
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.Photopath, photoPath);
        values.put(DBOpenHelper.Latitude, latitude);
        values.put(DBOpenHelper.Longitude, longitude);
        values.put(DBOpenHelper.DateTime,currentDateTime);
        values.put(DBOpenHelper.AUDIOFILE,audioFile);
        values.put(DBOpenHelper.Address,address);
        values.put(DBOpenHelper.city,City);
        values.put(DBOpenHelper.state,State);
        values.put(DBOpenHelper.country,Country);
        values.put(DBOpenHelper.Type,type);
        sqLiteDatabase.insert(TABLE_NOTES, null, values);
        sqLiteDatabase.close();



    }

    public List<model> getData() {
        List<model> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TABLE_NOTES;

        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                model c = new model();
                c.setId(cursor.getInt(0));
                c.setNoteText(cursor.getString(1));
                c.setPath(cursor.getString(2));
                c.setLatitude(cursor.getString(3));
                c.setLongitude(cursor.getString(4));
                c.setDataTime(cursor.getString(5));
                c.setAudioPath(cursor.getString(6));
                c.setAddress(cursor.getString(7));
                c.setCity(cursor.getString(8));
                c.setState(cursor.getString(9));
                c.setCountry(cursor.getString(10));
                c.setDataType(cursor.getInt(11));
                list.add(c);
            } while (cursor.moveToNext());
        }

        return list;

    }

    public int updateData(String newText, String newPhoto1, Double lat, Double log, String currentDateTime, String address,String City, String State, String Country, String audioFile, int type, int travellingNoteId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText);
        values.put(DBOpenHelper.Photopath, newPhoto1);
        values.put(DBOpenHelper.Latitude, lat);
        values.put(DBOpenHelper.Longitude, log);
        values.put(DBOpenHelper.DateTime,currentDateTime);
        values.put(DBOpenHelper.AUDIOFILE,audioFile);
        values.put(DBOpenHelper.Address,address);
        values.put(DBOpenHelper.city,City);
        values.put(DBOpenHelper.state,State);
        values.put(DBOpenHelper.country,Country);
        values.put(DBOpenHelper.Type,type);
        Log.e("noteID", travellingNoteId+"");


        return db.update(TABLE_NOTES, values, NOTE_ID + " = ?",
                new String[] { String.valueOf(travellingNoteId) });


    }

    public void isdelect(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, NOTE_ID+" = ?", new String[]{String.valueOf(id)});
        db.close();

        Log.e("id", id+"");
    }

    public static Cursor getCursorAllTitle( Context c, String selection, String[] selectionArgs) {

        DbOpenHelper dbHelper = new DbOpenHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();



        String[] projection = {DBOpenHelper.NOTE_ID, DBOpenHelper.NOTE_TEXT, DBOpenHelper.Photopath, DBOpenHelper.Latitude, DBOpenHelper.Longitude, DBOpenHelper.DateTime, DBOpenHelper.AUDIOFILE, DBOpenHelper.Address, DBOpenHelper.city, DBOpenHelper.state, DBOpenHelper.country,DBOpenHelper.Type };

        String sortOrder = DBOpenHelper.NOTE_TEXT + " COLLATE NOCASE ASC";

        return db.query(DBOpenHelper.TABLE_NOTES,   // Table name
                projection,                     // SELECT
                selection,                           // Columns for WHERE
                selectionArgs,                           // Values for WHERE
                null,                           // Group
                null,                           // Filter by Group
                sortOrder);
    }
}

