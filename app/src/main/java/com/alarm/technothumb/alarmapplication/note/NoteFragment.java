package com.alarm.technothumb.alarmapplication.note;

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
 * Created by NIKUNJ on 29-01-2018.
 */

public class NoteFragment  extends Fragment{

    TextView txt_note;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_note, container, false);

        txt_note = v.findViewById(R.id.txt_note);

        txt_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),NotesActivity.class);
                getActivity().startActivity(i);
            }
        });

        return v;
    }
}
