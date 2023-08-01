package com.alarm.technothumb.alarmapplication.travel_record;

/**
 * Created by wdl-android on 25-04-2018.
 */

public class model {

    int id;
    String noteText;
    String Path;
    String AudioPath;
    String Latitude;
    String longitude;
    String dataTime;
    String Address;
    String city;
    String state;
    String country;
    int dataType;

    public model(String noteText, String address, String city, String state, String country, String audioPath, String path,  int id, String dataTime) {

        this.id = id;
        this.noteText = noteText;
        this.Path = path;
        this.AudioPath = audioPath;
        this.dataTime =dataTime;
        this.Address = address;
        this.city = city;
        this.state = state;
        this.country = country;

    }

    public model() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getAudioPath() {
        return AudioPath;
    }

    public void setAudioPath(String audioPath) {
        AudioPath = audioPath;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
