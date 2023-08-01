package com.alarm.technothumb.alarmapplication.anniversaries;

/**
 * Created by wdl-android on 13-04-2018.
 */

public class anniversaryModel  {

    String Title;
    String Date;
    String category;
    int titleID;
    int anniID = 5;

    public anniversaryModel(String title, String date) {
        Title = title;
        Date = date;
    }

    public anniversaryModel() {

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
        return titleID;
    }

    public void setTitleID(int titleID) {
        this.titleID = titleID;
    }

    public int getAnniID() {
        return anniID;
    }

    public void setAnniID(int anniID) {
        this.anniID = anniID;
    }
}
