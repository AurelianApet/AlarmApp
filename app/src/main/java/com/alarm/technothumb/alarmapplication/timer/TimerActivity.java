package com.alarm.technothumb.alarmapplication.timer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;

import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {


    private static TextView countdownTimerText;
    private static EditText minutes,entername;
    private static Button startTimer, resetTimer;
    private static CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


        countdownTimerText = (TextView) findViewById(R.id.countdownText);
        minutes = (EditText) findViewById(R.id.enterMinutes);
        entername = (EditText) findViewById(R.id.entername);
        startTimer = (Button) findViewById(R.id.startTimer);
        resetTimer = (Button) findViewById(R.id.resetTimer);

        setListeners();
    }

    private void setListeners() {
        startTimer.setOnClickListener(this);
        resetTimer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTimer:

                // Create object of SharedPreferences.
                SharedPreferences sharedPref = getSharedPreferences("mypref", 0);

                //now get Editor
                SharedPreferences.Editor editor = sharedPref.edit();

                //put your value
                editor.putString("minute", minutes.getText().toString());

                Log.e("minute",minutes.getText().toString());

                //commits your edits
                editor.commit();


                if (entername.getText().toString().isEmpty())
                {
                    Toast.makeText(TimerActivity.this, "Enter Label", Toast.LENGTH_SHORT).show();
                }
                //If CountDownTimer is null then start timer
                else if (countDownTimer == null) {
                    String getMinutes = minutes.getText().toString();//Get minutes from edittexf
                    //Check validation over edittext
                    if (!getMinutes.equals("") && getMinutes.length() > 0) {
                        int noOfMinutes = Integer.parseInt(getMinutes) * 60 * 1000;//Convert minutes into milliseconds

                        startTimer(noOfMinutes);//start countdown
                        startTimer.setText("Stop");//Change Text

                    } else
                        Toast.makeText(TimerActivity.this, "Please enter no. of Minutes.", Toast.LENGTH_SHORT).show();//Display toast if edittext is empty
                } else {
                    //Else stop timer and change text
                    stopCountdown();
                    startTimer.setText("Start");
                }
                break;
            case R.id.resetTimer:
                stopCountdown();//stop count down
                startTimer.setText("Start");//Change text to Start Timer
                countdownTimerText.setText("00:00:00");//Change Timer text
                break;
        }


    }


    //Stop Countdown method
    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    //Start Countodwn method
    private void startTimer(int noOfMinutes) {
        countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                countdownTimerText.setText(hms);//set text
            }

            public void onFinish() {



                countdownTimerText.setText("");
                Intent i =new Intent(TimerActivity.this,Timer_DismissActivity.class);
                i.putExtra("Title",entername.getText().toString());
                startActivity(i);
//                countdownTimerText.setText("TIME'S UP!!"); //On finish change timer text
//                countDownTimer = null;//set CountDownTimer to null
//                startTimer.setText("Start Time");//Change button text
            }
        }.start();

    }
}
