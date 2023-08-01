package com.alarm.technothumb.alarmapplication.calenderr;

import android.content.Context;
import android.os.Bundle;

import java.io.IOException;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public interface CloudNotificationBackplane {

    public boolean open(Context context);
    public boolean subscribeToGroup(String senderId, String account, String groupId)
            throws IOException;
    public void send(String to, String msgId, Bundle data) throws IOException;
    public void close();
}
