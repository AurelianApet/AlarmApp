package com.alarm.technothumb.alarmapplication.calenderr;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by NIKUNJ on 31-01-2018.
 */

public interface AllInOneMenuExtensionsInterface {

    /**
     * Returns additional options.
     */
    Integer getExtensionMenuResource(Menu menu);

    /**
     * Handle selection of the additional options.
     */
    boolean handleItemSelected(MenuItem item, Context context);
}
