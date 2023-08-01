package com.alarm.technothumb.alarmapplication.alarm;

import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarmm.Log;
import com.alarm.technothumb.alarmapplication.note.Config;

public class AlarmLandingPageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null)
        {
            Config.TONE=getIntent().getStringExtra("uri");
            Config.NAME=getIntent().getStringExtra("name");
            Config.CONTENT=getIntent().getStringExtra("desc");
            android.util.Log.e("ALARMMPAGEACTIVIT",Config.TONE+"");


        }
        setContentView(R.layout.activity_alarm_landing_page);


    }
}
