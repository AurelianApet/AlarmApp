package com.alarm.technothumb.alarmapplication.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;

import java.io.IOException;

public class Timer_DismissActivity extends AppCompatActivity implements View.OnClickListener{

    private MediaPlayer mMediaPlayer;

    TextView countdownText,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer__dismiss);

        SharedPreferences sharedPref = getSharedPreferences("mypref", 0);
        String name1 = sharedPref.getString("minute", "");

        final Button dismiss = (Button) findViewById(R.id.dismiss_btn);
        countdownText= findViewById(R.id.countdownText);
        title= findViewById(R.id.title);

//        String name = getIntent().getExtras().getString("Title");
//        title.setText(name);

        countdownText.setText("TIME'S UP!!");


        dismiss.setOnClickListener(this);

        playSound(this, getAlarmUri());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.dismiss_btn:

//                r.stop();
                mMediaPlayer.stop();

                finish();

                break;
        }

    }

    private void playSound(Context context, Uri alert) {
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
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}
