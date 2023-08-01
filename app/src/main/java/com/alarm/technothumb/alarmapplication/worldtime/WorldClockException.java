package com.alarm.technothumb.alarmapplication.worldtime;

/**
 * Created by NIKUNJ on 17-01-2018.
 */

public class WorldClockException extends RuntimeException {

    public WorldClockException() {
        super();
    }

    public WorldClockException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public WorldClockException(String detailMessage) {
        super(detailMessage);
    }

    public WorldClockException(Throwable throwable) {
        super(throwable);
    }
}