package com.alarm.technothumb.alarmapplication.medicinee;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.medicinee.model.Alarm;
import com.alarm.technothumb.alarmapplication.medicinee.model.Pill;
import com.alarm.technothumb.alarmapplication.medicinee.model.PillBox;
import com.alarm.technothumb.alarmapplication.note.AudioNoteActivity;
import com.alarm.technothumb.alarmapplication.note.DbContract;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    private AlarmManager alarmManager;
    private PendingIntent operation;
    private boolean dayOfWeekList[] = new boolean[7];
    TextView selectRingtone;
    int hour, minute;
    TextView timeLabel;
    PillBox pillBox = new PillBox();
    private static final int REQUEST_CODE_RINGTONE = 999;
    public Uri alert, ringToneuri, setRingtoneUri, setRingtoneUri2;
    // Time picker dialog that pops up when the user presses the time string
    // This method specifies the hour and minute of the time picker before the user
    // does anything
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
            hour = hourOfDay;
            minute = minuteOfHour;
            timeLabel.setText(setTime(hour, minute));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add an Alarm");

        // Set up the time string on the page
        timeLabel = (TextView) findViewById(R.id.reminder_time);
        Typeface lightFont = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        timeLabel.setTypeface(lightFont);

        // Get the time right now and set it to be the time string
        Calendar rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        minute = rightNow.get(Calendar.MINUTE);
        timeLabel.setText(setTime(hour, minute));
        selectRingtone = (TextView) findViewById(R.id.choose_ringtone);

        timeLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(AddActivity.this,
                        //R.style.Theme_AppCompat_Dialog,
                        t,
                        hour,
                        minute,
                        true).show();
            }
        });
        timeLabel.setText(setTime(hour, minute));

        View.OnClickListener setClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int checkBoxCounter = 0;

                EditText editText = (EditText) findViewById(R.id.pill_name);
                String pill_name = editText.getText().toString();

                if (selectRingtone.getText().toString().equals("Select Tone")) {
                    Toast.makeText(getBaseContext(), "Please select RingTone", Toast.LENGTH_SHORT).show();
                } else {

                    Alarm alarm = new Alarm();

                    /** If Pill does not already exist */
                    if (!pillBox.pillExist(getApplicationContext(), pill_name)) {
                        Pill pill = new Pill();
                        pill.setPillName(pill_name);
                        alarm.setHour(hour);
                        alarm.setMinute(minute);
                        alarm.setPillName(pill_name);
                        alarm.setDayOfWeek(dayOfWeekList);
                        pill.addAlarm(alarm);
                        long pillId = pillBox.addPill(getApplicationContext(), pill);
                        pill.setPillId(pillId);
                        pillBox.addAlarm(getApplicationContext(), alarm, pill, setRingtoneUri.toString());

                    } else {
                        Pill pill = pillBox.getPillByName(getApplicationContext(), pill_name);
                        alarm.setHour(hour);
                        alarm.setMinute(minute);
                        alarm.setPillName(pill_name);
                        alarm.setDayOfWeek(dayOfWeekList);
                        pill.addAlarm(alarm);
                        pillBox.addAlarm(getApplicationContext(), alarm, pill, setRingtoneUri.toString());

                    }


                    List<Long> ids = new LinkedList<Long>();
                    try {
                        List<Alarm> alarms = pillBox.getAlarmByPill(getApplicationContext(), pill_name);
                        for (Alarm tempAlarm : alarms) {
                            if (tempAlarm.getHour() == hour && tempAlarm.getMinute() == minute) {
                                ids = tempAlarm.getIds();
                                break;
                            }
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < 7; i++) {
                        if (dayOfWeekList[i] && pill_name.length() != 0) {

                            int dayOfWeek = i + 1;

                            long _id = ids.get(checkBoxCounter);
                            int id = (int) _id;
                            checkBoxCounter++;

                            /** This intent invokes the activity AlertActivity, which in turn opens the AlertAlarm window */
                            Intent intent = new Intent(getBaseContext(), MyAlarmReceivers.class);
                            intent.putExtra("uri", setRingtoneUri.toString());
                            intent.putExtra("pill_name", pill_name);

                            operation = PendingIntent.getBroadcast(getBaseContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            /** Getting a reference to the System Service ALARM_SERVICE */
                            alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

                            /** Creating a calendar object corresponding to the date and time set by the user */
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 01);
                            //  calendar.set(Calendar.MILLISECOND, 00);

                            Log.e(">>", minute + " "+calendar.getTimeInMillis());

                            /** Converting the date and time in to milliseconds elapsed since epoch */
                            long alarm_time = calendar.getTimeInMillis();

                            if (calendar.before(Calendar.getInstance()))
                                alarm_time += AlarmManager.INTERVAL_DAY * 7;

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                                    alarmManager.INTERVAL_DAY * 7, operation);
                        }
                    }


                    if (checkBoxCounter == 0 || pill_name.length() == 0)
                        Toast.makeText(getBaseContext(), "Please input a pill name or check at least one day!", Toast.LENGTH_SHORT).show();
                    else { // Input form is completely filled out


                        Toast.makeText(getBaseContext(), "Alarm for " + pill_name + " is set successfully", Toast.LENGTH_SHORT).show();
                        Intent returnHome = new Intent(getBaseContext(), MedicineActivity.class);
                        startActivity(returnHome);
                        finish();
//                    AddActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            }
        };

        View.OnClickListener cancelClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnHome = new Intent(getBaseContext(), MedicineActivity.class);
                startActivity(returnHome);
                finish();
//                AddActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        };

        Button btnSetAlarm = (Button) findViewById(R.id.btn_set_alarm);
        btnSetAlarm.setOnClickListener(setClickListener);

        Button btnQuitAlarm = (Button) findViewById(R.id.btn_cancel_alarm);
        btnQuitAlarm.setOnClickListener(cancelClickListener);

        selectRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(AddActivity.this, RingtoneManager.TYPE_ALARM);
                final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
                startActivityForResult(intent, REQUEST_CODE_RINGTONE);

            }
        });
    }

    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu__medi_add, menu);
        return true;
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        /** Checking which checkbox was clicked */
        switch (view.getId()) {
            case R.id.checkbox_monday:
                if (checked)
                    dayOfWeekList[1] = true;
                else
                    dayOfWeekList[1] = false;
                break;
            case R.id.checkbox_tuesday:
                if (checked)
                    dayOfWeekList[2] = true;
                else
                    dayOfWeekList[2] = false;
                break;
            case R.id.checkbox_wednesday:
                if (checked)
                    dayOfWeekList[3] = true;
                else
                    dayOfWeekList[3] = false;
                break;
            case R.id.checkbox_thursday:
                if (checked)
                    dayOfWeekList[4] = true;
                else
                    dayOfWeekList[4] = false;
                break;
            case R.id.checkbox_friday:
                if (checked)
                    dayOfWeekList[5] = true;
                else
                    dayOfWeekList[5] = false;
                break;
            case R.id.checkbox_saturday:
                if (checked)
                    dayOfWeekList[6] = true;
                else
                    dayOfWeekList[6] = false;
                break;
            case R.id.checkbox_sunday:
                if (checked)
                    dayOfWeekList[0] = true;
                else
                    dayOfWeekList[0] = false;
                break;
            case R.id.every_day:
                LinearLayout ll = (LinearLayout) findViewById(R.id.checkbox_layout);
                for (int i = 0; i < ll.getChildCount(); i++) {
                    View v = ll.getChildAt(i);
                    ((CheckBox) v).setChecked(checked);
                    onCheckboxClicked(v);
                }
                break;
        }
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        Intent returnHome = new Intent(getBaseContext(), MedicineActivity.class);
        startActivity(returnHome);
        finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method takes hours and minute as input and returns
     * a string that is like "12:01pm"
     */
    public String setTime(int hour, int minute) {
        String am_pm = (hour < 12) ? "am" : "pm";
        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;
        String minuteWithZero;
        if (minute < 10)
            minuteWithZero = "0" + minute;
        else
            minuteWithZero = "" + minute;
        return nonMilitaryHour + ":" + minuteWithZero + am_pm;
    }

    @Override
    public void onBackPressed() {
        Intent returnHome = new Intent(getBaseContext(), MedicineActivity.class);
        startActivity(returnHome);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_RINGTONE:
                    saveRingtoneUri(data);
                    break;
                default:
                    com.alarm.technothumb.alarmapplication.alarmm.Log.w("Unhandled request code in onActivityResult: " + requestCode);
            }
        }
    }

    private void saveRingtoneUri(Intent data) {
        ringToneuri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

        setRingtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

        Log.e("uri", ringToneuri + "");
        Ringtone r = RingtoneManager.getRingtone(this, ringToneuri);
        String ringToneName = r.getTitle(this);
        selectRingtone.setText(ringToneName);


    }
}


