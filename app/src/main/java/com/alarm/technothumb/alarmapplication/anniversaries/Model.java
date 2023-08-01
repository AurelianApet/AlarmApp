package com.alarm.technothumb.alarmapplication.anniversaries;

/**
 * Created by wdl-android on 13-04-2018.
 */


public class Model  {

    String Title;
    String Date;
    String category;
    int TitleID;
    int AnniID;

    public Model(String title, String date) {
        Title = title;
        Date = date;
    }

    public Model() {

    }

    public Model(String title, String date, int titleID, String category, int AnniID) {
        this.Title = title;
        this.Date =date;
        this.category=category;
        this.TitleID=titleID;

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTitleID() {
        return TitleID;
    }

    public void setTitleID(int titleID) {
        TitleID = titleID;
    }

    public int getAnniID() {
        return AnniID;
    }

    public void setAnniID(int anniID) {
        AnniID = anniID;
    }
}