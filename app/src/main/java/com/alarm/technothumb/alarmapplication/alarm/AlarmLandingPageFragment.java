package com.alarm.technothumb.alarmapplication.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.note.Config;

import java.io.IOException;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by NIKUNJ on 17-01-2018.
 */

public class AlarmLandingPageFragment extends Fragment implements View.OnClickListener {

    //    Ringtone r;
    private MediaPlayer mMediaPlayer;

    TextView tvName, tvdesc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View v = inflater.inflate(R.layout.fragment_alarm_landing_page, container, false);

//        final Button launchMainActivityBtn = (Button) v.findViewById(R.id.load_main_activity_btn);
        final Button dismiss = (Button) v.findViewById(R.id.dismiss_btn);
        Button snooze = v.findViewById(R.id.snooze);
        tvName = v.findViewById(R.id.tvName);
        tvdesc = v.findViewById(R.id.tvDesc);

        tvName.setText(Config.NAME);
        tvdesc.setText(Config.CONTENT);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String snooze = null;

                PendingIntent operation;
                AlarmManager alarmManager;

                mMediaPlayer.stop();
                final int _id = (int) System.currentTimeMillis();
                final long minute = 60000;
                long snoozeLength = 10;
                long currTime = System.currentTimeMillis();
                long min = currTime + minute * snoozeLength;

                Intent intent = new Intent(getActivity(), AlarmLandingPageActivity.class);
                intent.putExtra("pill_name", snooze);
                intent.putExtra("uri",Config.TONE);
                intent.putExtra("name",Config.NAME);
                intent.putExtra("desc",Config.CONTENT);
                operation = PendingIntent.getActivity(getActivity(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                /** Getting a reference to the System Service ALARM_SERVICE */
                alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, min, operation);
                Toast.makeText(getActivity(), "Alarm was snoozed for 10 minutes", Toast.LENGTH_LONG).show();

                getActivity().finish();
            }
        });

        dismiss.setOnClickListener(this);

        playSound(getActivity(), getAlarmUri());

        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
//            case R.id.load_main_activity_btn:
//                startActivity(new Intent(getContext(), MainActivity.class));
//                getActivity().finish();
//                break;
            case R.id.dismiss_btn:

//                r.stop();
                mMediaPlayer.stop();

                getActivity().finish();

                break;
        }

    }

    private void playSound(Context context, Uri alert) {
        Log.e("FRGMENT alerr1", " " + alert);
        Log.e("FRGMENT tone111", Config.TONE + " ");
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }


    private Uri getAlarmUri() {
        Uri alert;
        if (!Config.TONE.equals("")) {
            alert = Uri.parse(Config.TONE);
        } else {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (alert == null) {
                    alert = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
        }
        return alert;
    }

}
