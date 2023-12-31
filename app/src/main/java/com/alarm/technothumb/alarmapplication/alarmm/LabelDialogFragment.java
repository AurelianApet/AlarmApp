package com.alarm.technothumb.alarmapplication.alarmm;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.provider.Alarm;
import com.alarm.technothumb.alarmapplication.alarmm.timer.TimerObj;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class LabelDialogFragment extends DialogFragment {

    private static final String KEY_LABEL = "label";
    private static final String KEY_ALARM = "alarm";
    private static final String KEY_TIMER = "timer";
    private static final String KEY_TAG = "tag";

    private EditText mLabelBox;

    public static LabelDialogFragment newInstance(Alarm alarm, String label, String tag) {
        final LabelDialogFragment frag = new LabelDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_LABEL, label);
        args.putParcelable(KEY_ALARM, alarm);
        args.putString(KEY_TAG, tag);
        frag.setArguments(args);
        return frag;
    }

    public static LabelDialogFragment newInstance(TimerObj timer, String label, String tag) {
        final LabelDialogFragment frag = new LabelDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_LABEL, label);
        args.putParcelable(KEY_TIMER, timer);
        args.putString(KEY_TAG, tag);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final String label = bundle.getString(KEY_LABEL);
        final Alarm alarm = bundle.getParcelable(KEY_ALARM);
        final TimerObj timer = bundle.getParcelable(KEY_TIMER);
        final String tag = bundle.getString(KEY_TAG);

        final View view = inflater.inflate(R.layout.label_dialog, container, false);

        mLabelBox = (EditText) view.findViewById(R.id.labelBox);
        mLabelBox.setText(label);
        mLabelBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    set(alarm, timer, tag);
                    return true;
                }
                return false;
            }
        });

        final Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        final Button setButton = (Button) view.findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set(alarm, timer, tag);
            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    private void set(Alarm alarm, TimerObj timer, String tag) {
        String label = mLabelBox.getText().toString();
        if (label.trim().length() == 0) {
            // Don't allow user to input label with only whitespace.
            label = "";
        }

        if (alarm != null) {
            set(alarm, tag, label);
        } else if (timer != null) {
            set(timer, tag, label);
        } else {
            Log.e("No alarm or timer available.");
        }
    }

    private void set(Alarm alarm, String tag, String label) {
        final Activity activity = getActivity();
        // TODO just pass in a listener in newInstance()
        if (activity instanceof AlarmLabelDialogHandler) {
            ((DeskClock) getActivity()).onDialogLabelSet(alarm, label, tag);
        } else {
            Log.e("Error! Activities that use LabelDialogFragment must implement "
                    + "AlarmLabelDialogHandler");
        }
        dismiss();
    }

    private void set(TimerObj timer, String tag, String label) {
        final Activity activity = getActivity();
        // TODO just pass in a listener in newInstance()
        if (activity instanceof TimerLabelDialogHandler){
            ((DeskClock) getActivity()).onDialogLabelSet(timer, label, tag);
        } else {
            Log.e("Error! Activities that use LabelDialogFragment must implement "
                    + "AlarmLabelDialogHandler or TimerLabelDialogHandler");
        }
        dismiss();
    }

    interface AlarmLabelDialogHandler {
        void onDialogLabelSet(Alarm alarm, String label, String tag);
    }

    interface TimerLabelDialogHandler {
        void onDialogLabelSet(TimerObj timer, String label, String tag);
    }
}
