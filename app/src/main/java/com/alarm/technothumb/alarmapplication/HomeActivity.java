package com.alarm.technothumb.alarmapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.alarm.technothumb.alarmapplication.alarmm.DeskClock;
import com.alarm.technothumb.alarmapplication.anniversaries.Anniversary;
import com.alarm.technothumb.alarmapplication.calender.CalenderActivity;
import com.alarm.technothumb.alarmapplication.calenderr.CalenderrActivity;
import com.alarm.technothumb.alarmapplication.medicinee.MedicineActivity;
import com.alarm.technothumb.alarmapplication.note.NotesActivity;
import com.alarm.technothumb.alarmapplication.travel_record.TravelRecordActivity;

public class HomeActivity extends AppCompatActivity {

    LinearLayout ll_alarm,ll_worldclock,ll_stop_watch,ll_timer,ll_notes,ll_calender,ll_anniversary,ll_medication,ll_travel_record;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(HomeActivity.this)){
                // change setting here
            }
            else{
                //Migrate to Setting write permission screen.
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + HomeActivity.this.getPackageName()));
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        } else {
        }

        ll_alarm = findViewById(R.id.ll_alarm);
        ll_notes = findViewById(R.id.ll_notes);
        ll_calender = findViewById(R.id.ll_calender);
        ll_anniversary = findViewById(R.id.ll_anniversary);
        ll_medication = findViewById(R.id.ll_medication);
        ll_travel_record = findViewById(R.id.ll_travel_record);

        ll_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i_alarm = new Intent(HomeActivity.this, DeskClock.class);
                startActivity(i_alarm);

            }
        });


        ll_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inotes = new Intent(HomeActivity.this, NotesActivity.class);
                startActivity(inotes);
            }
        });

        ll_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent icalender = new Intent(HomeActivity.this, CalenderrActivity.class);
                startActivity(icalender);
            }
        });

        ll_anniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ianniversary = new Intent(HomeActivity.this, Anniversary.class);
                startActivity(ianniversary);
            }
        });

        ll_medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imedication = new Intent(HomeActivity.this, MedicineActivity.class);
                startActivity(imedication);

           }
        });

        ll_travel_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imap = new Intent(HomeActivity.this, TravelRecordActivity.class);
                startActivity(imap);


            }
        });


    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)

                + ContextCompat.checkSelfPermission(HomeActivity.this,
                        android.Manifest.permission.CAMERA)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.READ_CONTACTS)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.WRITE_CONTACTS)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.RECORD_AUDIO)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.WRITE_SETTINGS)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.READ_CALENDAR)

                + ContextCompat
                .checkSelfPermission(HomeActivity.this,
                        Manifest.permission.WRITE_CALENDAR)

                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (HomeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ||

                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, android.Manifest.permission.CAMERA)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.READ_CONTACTS)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.WRITE_CONTACTS)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.RECORD_AUDIO)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.WRITE_SETTINGS)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.READ_CALENDAR)

                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (HomeActivity.this, Manifest.permission.WRITE_CALENDAR)) {

            } else {
                requestPermissions(
                        new String[]{android.Manifest.permission
                                .READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS
                                , Manifest.permission.WRITE_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO
                                ,Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
//            getDeviceImei();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean readcontact = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean writecontact = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean accesscoarselocation = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean accessfinelocation = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean recordaudio = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean writesetting = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    boolean readcalender = grantResults[9] == PackageManager.PERMISSION_GRANTED;
                    boolean writecalender = grantResults[10] == PackageManager.PERMISSION_GRANTED;


                    if (cameraPermission && readExternalFile && writeExternalFile && readcontact && writecontact
                            && accesscoarselocation && accessfinelocation && recordaudio && writesetting && readcalender && writecalender) {


                    } else {


                    }
                }
                break;
        }

    }
}
