package com.alarm.technothumb.alarmapplication.alarmm.timer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.CircleTimerView;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class TimerListItem extends LinearLayout {

    CountingTimerView mTimerText;
    CircleTimerView mCircleView;

    long mTimerLength;

    public TimerListItem(Context context) {
        this(context, null);
    }

    public TimerListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.timer_list_item, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTimerText = (CountingTimerView)findViewById(R.id.timer_time_text);
        mCircleView = (CircleTimerView)findViewById(R.id.timer_time);
        mCircleView.setTimerMode(true);
    }

    public void set(long timerLength, long timeLeft, boolean drawRed) {
        if (mCircleView == null) {
            mCircleView = (CircleTimerView)findViewById(R.id.timer_time);
            mCircleView.setTimerMode(true);
        }
        mTimerLength = timerLength;
        mCircleView.setIntervalTime(mTimerLength);
        mCircleView.setPassedTime(timerLength - timeLeft, drawRed);
        invalidate();
    }

    public void start() {
        mCircleView.startIntervalAnimation();
        mTimerText.redTimeStr(false, true);
        mTimerText.showTime(true);
        mCircleView.setVisibility(VISIBLE);
        Log.e("timer", "Start");
        TimerFragment.counterIsActive = true;
    }

    public void pause() {
        mCircleView.pauseIntervalAnimation();
        mTimerText.redTimeStr(false, true);
        mTimerText.showTime(true);
        mCircleView.setVisibility(VISIBLE);
        Log.e("timer", "puase");
    }

    public void stop() {
        mCircleView.stopIntervalAnimation();
        mTimerText.redTimeStr(false, true);
        mTimerText.showTime(true);
        mCircleView.setVisibility(VISIBLE);
        Log.e("timer", "stop");
    }

    public void timesUp() {
        mCircleView.abortIntervalAnimation();
        mTimerText.redTimeStr(true, true);
       // Log.e("timer", "tiemsUp");
    }

    public void done() {
        mCircleView.stopIntervalAnimation();
        mCircleView.setVisibility(VISIBLE);
        mCircleView.invalidate();
        mTimerText.redTimeStr(true, false);
        Log.e("timer", "Done");
    }

    public void setLength(long timerLength) {
        mTimerLength = timerLength;
        mCircleView.setIntervalTime(mTimerLength);
        mCircleView.invalidate();

        Log.e("mTimerLength", timerLength+"");
    }

    public void setTextBlink(boolean blink) {
        mTimerText.showTime(!blink);
    }

    public void setCircleBlink(boolean blink) {
        mCircleView.setVisibility(blink ? INVISIBLE : VISIBLE);
    }

    public void setTime(long time, boolean forceUpdate) {
        if (mTimerText == null) {
            mTimerText = (CountingTimerView)findViewById(R.id.timer_time_text);
        }

        mTimerText.setTime(time, false, forceUpdate);
    }

    // Used by animator to animate the size of a timer
    @SuppressWarnings("unused")
    public void setAnimatedHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = height;
            requestLayout();
        }
    }

}
