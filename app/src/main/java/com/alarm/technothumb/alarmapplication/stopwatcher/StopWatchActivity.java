package com.alarm.technothumb.alarmapplication.stopwatcher;

import android.animation.LayoutTransition;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class StopWatchActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch);


        startButton = (Button) findViewById(R.id.btn_start);
        lapButton = (Button) findViewById(R.id.btn_lap);
        textView = (TextView) findViewById(R.id.stopwatch_view);
        linearLayout = (LinearLayout) findViewById(R.id.layout);
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
                           StopWatchActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    currentTime += 1;
                                    lapTime += 1;

                                    manager = (NotificationManager)
                                            StopWatchActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

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

                    TextView lapDisplay = new TextView(StopWatchActivity.this);
                    ImageView imageView = new ImageView(StopWatchActivity.this);
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
                new NotificationCompat.Builder(StopWatchActivity.this)
                        .setSmallIcon(R.mipmap.ic_timelapse_white_18dp)
                        .setContentTitle("Stopwatch running")
                        .setContentText("00:00:00")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true);


        Intent resultIntent = new Intent(StopWatchActivity.this, StopWatchActivity.class);



        PendingIntent resultPendingIntent = PendingIntent.getActivity(StopWatchActivity.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

    }

}
