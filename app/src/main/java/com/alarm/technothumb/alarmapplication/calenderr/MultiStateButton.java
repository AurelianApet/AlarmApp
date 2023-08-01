package com.alarm.technothumb.alarmapplication.calenderr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class MultiStateButton extends Button {
    //The current state for this button, ranging from 0 to maxState-1
    private int mState;
    //The maximum number of states allowed for this button.
    private int mMaxStates;
    //The currently displaying resource ID. This gets set to a default on creation and remains
    //on the last set if the resources get set to null.
    private int mButtonResource;
    //A list of all drawable resources used by this button in the order it uses them.
    private int[] mButtonResources;
    private Drawable mButtonDrawable;

    public MultiStateButton(Context context) {
        this(context, null);
    }

    public MultiStateButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiStateButton(Context context, AttributeSet attrs, int defStyle) {
        //Currently using the standard buttonStyle, will update when new resources are added.
        super(context, attrs, defStyle);
        mMaxStates = 1;
        mState = 0;
        //TODO add a more generic default button
        mButtonResources = new int[] { R.drawable.widget_show };
        setButtonDrawable(mButtonResources[mState]);
    }

    @Override
    public boolean performClick() {
        /* When clicked, toggle the state */
        transitionState();
        return super.performClick();
    }

    public void transitionState() {
        mState = (mState + 1) % mMaxStates;
        setButtonDrawable(mButtonResources[mState]);
    }

    /**
     * Allows for a new set of drawable resource ids to be set.
     *
     * This sets the maximum states allowed to the length of the resources array. It will also
     * set the current state to the maximum allowed if it's greater than the new max.
     */
    public void setButtonResources(int[] resources) throws IllegalArgumentException {
        if(resources == null) {
            throw new IllegalArgumentException("Button resources cannot be null");
        }
        mMaxStates = resources.length;
        if(mState >= mMaxStates) {
            mState = mMaxStates - 1;
        }
        mButtonResources = resources;
    }

    /**
     * Attempts to set the state. Returns true if successful, false otherwise.
     */
    public boolean setState(int state){
        if(state >= mMaxStates || state < 0) {
            //When moved out of Calendar the tag should be changed.
            Log.w("Cal", "MultiStateButton state set to value greater than maxState or < 0");
            return false;
        }
        mState = state;
        setButtonDrawable(mButtonResources[mState]);
        return true;
    }

    public int getState() {
        return mState;
    }

    /**
     * Set the background to a given Drawable, identified by its resource id.
     *
     * @param resid the resource id of the drawable to use as the background
     */
    public void setButtonDrawable(int resid) {
        if (resid != 0 && resid == mButtonResource) {
            return;
        }

        mButtonResource = resid;

        Drawable d = null;
        if (mButtonResource != 0) {
            d = getResources().getDrawable(mButtonResource);
        }
        setButtonDrawable(d);
    }

    /**
     * Set the background to a given Drawable
     *
     * @param d The Drawable to use as the background
     */
    public void setButtonDrawable(Drawable d) {
        if (d != null) {
            if (mButtonDrawable != null) {
                mButtonDrawable.setCallback(null);
                unscheduleDrawable(mButtonDrawable);
            }
            d.setCallback(this);
            d.setState(getDrawableState());
            d.setVisible(getVisibility() == VISIBLE, false);
            mButtonDrawable = d;
            mButtonDrawable.setState(null);
            setMinHeight(mButtonDrawable.getIntrinsicHeight());
            setWidth(mButtonDrawable.getIntrinsicWidth());
        }
        refreshDrawableState();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mButtonDrawable != null) {
            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            final int horizontalGravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
            final int height = mButtonDrawable.getIntrinsicHeight();
            final int width = mButtonDrawable.getIntrinsicWidth();

            int y = 0;
            int x = 0;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y = getHeight() - height;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y = (getHeight() - height) / 2;
                    break;
            }
            switch (horizontalGravity) {
                case Gravity.RIGHT:
                    x = getWidth() - width;
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    x = (getWidth() - width) / 2;
                    break;
            }

            mButtonDrawable.setBounds(x, y, x + width, y + height);
            mButtonDrawable.draw(canvas);
        }
    }
}
