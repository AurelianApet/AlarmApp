package com.alarm.technothumb.alarmapplication.calenderr.recurrencepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class LinearLayoutWithMaxWidth extends LinearLayout {

    public LinearLayoutWithMaxWidth(Context context) {
        super(context);
    }

    public LinearLayoutWithMaxWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutWithMaxWidth(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        WeekButton.setSuggestedWidth((MeasureSpec.getSize(widthMeasureSpec)) / 7);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
