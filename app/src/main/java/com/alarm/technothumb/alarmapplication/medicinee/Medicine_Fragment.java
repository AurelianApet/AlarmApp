package com.alarm.technothumb.alarmapplication.medicinee;

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
 * Created by NIKUNJ on 24-01-2018.
 */

public class Medicine_Fragment extends Fragment {

    TextView txt_medicine;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragmentmedicine, container, false);

        txt_medicine = v.findViewById(R.id.txt_medicine);
        txt_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),MedicineActivity.class);
                getActivity().startActivity(i);
            }
        });

        return v;

    }


}
