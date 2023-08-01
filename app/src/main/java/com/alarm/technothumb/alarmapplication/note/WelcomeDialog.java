package com.alarm.technothumb.alarmapplication.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 29-01-2018.
 */

public class WelcomeDialog extends DialogFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater i = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(i.inflate(R.layout.fragment_welcome_dialog, null));
        builder.setIcon(R.drawable.cal_logo);
        builder.setTitle(getActivity().getString(R.string.dialog_welcome));
        builder.setPositiveButton(getActivity().getString(R.string.dialog_ok), null);
//        builder.setNegativeButton(getActivity().getString(R.string.dialog_help), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                startActivity(new Intent(getActivity(), HelpActivity.class));
//            }
//        });

        return builder.create();
    }
}
