package com.alarm.technothumb.alarmapplication.stopwatcher;

import android.animation.LayoutTransition;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by NIKUNJ on 17-01-2018.
 */

public class StopWatcherFragment extends Fragment {

    private Button startButton;
    private Button lapButton;
    private TextView textView;
    private Timer timer;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private LayoutTransition transition;
    private LinearLayout linearLayout;
    private int currentTime = 0;
    private int lapTime = 0;
    private int lapCounter = 0;
    private int mId = 1;
    private boolean lapViewExists;
    private boolean isButtonStartPressed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_stopwatcher, container, false);

        startButton = (Button) v.findViewById(R.id.btn_start);
        lapButton = (Button) v.findViewById(R.id.btn_lap);
        textView = (TextView) v.findViewById(R.id.stopwatch_view);
        linearLayout = (LinearLayout) v.findViewById(R.id.layout);
        textView.setTextSize(55);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isButtonStartPressed) {
                    onSWatchStop();
                } else {
                    isButtonStartPressed = true;

//                    startButton.setBackgroundResource(R.drawable.btn_stop_state);
                    startButton.setText(R.string.btn_stop);

//                    lapButton.setBackgroundResource(R.drawable.btn_lap_state);
                    lapButton.setText(R.string.btn_lap);
                    lapButton.setEnabled(true);

                    setUpNotification();

                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    currentTime += 1;
                                    lapTime += 1;

                                    manager = (NotificationManager)
                                            getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                    // update notification text
                                    builder.setContentText(TimeFormatUtil.toDisplayString(currentTime));
                                    manager.notify(mId, builder.build());

                                    // update ui
                                    textView.setText(TimeFormatUtil.toDisplayString(currentTime));
                                }
                            });
                        }
                    }, 0, 100);
                }
            }
        });

        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isButtonStartPressed) {
                    onSWatchReset();
                } else {
                    lapViewExists = true;
                    lapCounter++;

                    transition = new LayoutTransition();
                    transition.setAnimator(LayoutTransition.CHANGE_APPEARING, null);
                    transition.setStartDelay(LayoutTransition.APPEARING, 0);


                    linearLayout.setLayoutTransition(transition);

                    TextView lapDisplay = new TextView(getActivity());
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setFocusableInTouchMode(true);

                    lapDisplay.setGravity(Gravity.CENTER);
                    lapDisplay.setTextColor(Color.WHITE);
                    lapDisplay.setTextSize(30);

                    linearLayout.addView(lapDisplay);
                    linearLayout.addView(imageView);

                    imageView.requestFocus();

                    lapDisplay.setText(String.format("Lap %d: %s", lapCounter, TimeFormatUtil.toDisplayString(lapTime)));
                    imageView.setImageResource(R.drawable.divider);
                    lapTime = 0;
                }

            }
        });

        return v;
    }


    public void onSWatchStop() {
//        startButton.setBackgroundResource(R.drawable.btn_start_state);
        startButton.setText(R.string.btn_start);
        lapButton.setEnabled(true);
//        lapButton.setBackgroundResource(R.drawable.btn_lap_state);
        lapButton.setText(R.string.btn_reset);

        isButtonStartPressed = false;
        timer.cancel();
        manager.cancel(mId);
    }

    public void onSWatchReset() {
        timer.cancel();
        currentTime = 0;
        lapTime = 0;
        lapCounter = 0;
        textView.setText(TimeFormatUtil.toDisplayString(currentTime));
        lapButton.setEnabled(false);
        lapButton.setText(R.string.btn_lap);
//        lapButton.setBackgroundResource(R.drawable.btn_reset_state);

        if (lapViewExists) {
            linearLayout.setLayoutTransition(null);
            linearLayout.removeAllViews();
            lapViewExists = false;
        }
    }


    public void setUpNotification() {
        builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.ic_timelapse_white_18dp)
                        .setContentTitle("Stopwatch running")
                        .setContentText("00:00:00")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true);


        Intent resultIntent = new Intent(getActivity(), StopWatcherFragment.class);



        PendingIntent resultPendingIntent = PendingIntent.getActivity(getActivity(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

    }
}
