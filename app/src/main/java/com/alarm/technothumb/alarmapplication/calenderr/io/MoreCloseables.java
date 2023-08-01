package com.alarm.technothumb.alarmapplication.calenderr.io;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;

import java.io.IOException;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class MoreCloseables {

    /**
     * Closes the supplied cursor iff it is not null.
     */
    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Closes the given file descriptor iff it is not null, ignoring exceptions.
     */
    public static void closeQuietly(AssetFileDescriptor assetFileDescriptor) {
        if (assetFileDescriptor != null) {
            try {
                assetFileDescriptor.close();
            } catch (IOException e) {
                // Ignore exceptions when closing.
            }
        }
    }
}
