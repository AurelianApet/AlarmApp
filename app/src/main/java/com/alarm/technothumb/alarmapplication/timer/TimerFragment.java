package com.alarm.technothumb.alarmapplication.timer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by NIKUNJ on 17-01-2018.
 */

public class TimerFragment extends Fragment implements View.OnClickListener {


    private static TextView countdownTimerText;
    private static EditText minutes,entername;
    private static Button startTimer, resetTimer;
    private static CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_timer, container, false);

        countdownTimerText = (TextView) v.findViewById(R.id.countdownText);
        minutes = (EditText) v.findViewById(R.id.enterMinutes);
        entername = (EditText) v.findViewById(R.id.entername);
        startTimer = (Button) v.findViewById(R.id.startTimer);
        resetTimer = (Button) v.findViewById(R.id.resetTimer);

        setListeners();



        return v;
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
                SharedPreferences sharedPref = getActivity().getSharedPreferences("mypref", 0);

                //now get Editor
                SharedPreferences.Editor editor = sharedPref.edit();

                //put your value
                editor.putString("minute", minutes.getText().toString());

                Log.e("minute",minutes.getText().toString());

                //commits your edits
                editor.commit();


                if (entername.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity(), "Enter Label", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Please enter no. of Minutes.", Toast.LENGTH_SHORT).show();//Display toast if edittext is empty
                } else {
                    //Else stop timer and change text
                    stopCountdown();
                    startTimer.setText("Start");
                }
                break;
            case R.id.resetTimer:
                stopCountdown();//stop count down
                startTimer.setText("Start");
                entername.setText("");
                minutes.setText("");
                countdownTimerText.setText("00:00:00");
                //Change text to Start Timer
//                countdownTimerText.setText("Timer");//Change Timer text

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
                Intent i =new Intent(getActivity(),Timer_DismissActivity.class);
                i.putExtra("Title",entername.getText().toString());
                startActivity(i);
//                countdownTimerText.setText("TIME'S UP!!"); //On finish change timer text
//                countDownTimer = null;//set CountDownTimer to null
//                startTimer.setText("Start Time");//Change button text
            }
        }.start();

    }

}

