package com.alarm.technothumb.alarmapplication.calender;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 18-01-2018.
 */

public class CalenderFragment extends Fragment {

    TextView txt_calender;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_calender, container, false);

        txt_calender = v.findViewById(R.id.txt_calender);

        txt_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =new Intent(getActivity(),CalenderActivity.class);
                getActivity().startActivity(i);
            }
        });


        return v;
    }
}
