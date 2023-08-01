package com.alarm.technothumb.alarmapplication.note;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarm.AlarmLandingPageActivity;
import com.alarm.technothumb.alarmapplication.anniversaries.addAnniversary;
import com.alarm.technothumb.alarmapplication.medicinee.model.Alarm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TextNoteActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    public static final String EXTRA_ID = "org.secuso.privacyfriendlynotes.ID";

    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 1;

    EditText etName;
    EditText etContent;
    Spinner spinner;
    public Uri alert, ringToneuri, setRingtoneUri, setRingtoneUri2;
    private ShareActionProvider mShareActionProvider = null;
    private static final int REQUEST_CODE_RINGTONE = 999;
    private int dayOfMonth, monthOfYear, year;

    private boolean edit = false;
    private boolean hasAlarm = false;
    private boolean shouldSave = true;
    private int id = -1;
    private int notification_id = -1;
    private int currentCat;
    Cursor noteCursor = null;
    Cursor notificationCursor = null;
    String currentDateTime;
    Button btnChangeColor;
    ScrollView scrollView;
    String ColorCode;
    TextView RingTone;
    Spinner spinnerPriority;
    String[] priority = {"Low", "Medium ", "High"};
    int dbPriority;
    Calendar alarmtime;
    Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note);
        setTitle("Text Note");
        r = new Random();
        currentDateTime = DateFormat.getDateTimeInstance().format(new Date());

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.choose_ringtone).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etContent = (EditText) findViewById(R.id.etContent);
        spinner = (Spinner) findViewById(R.id.spinner_category);
        btnChangeColor = (Button) findViewById(R.id.ColorChange);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        RingTone = (TextView) findViewById(R.id.choose_ringtone);

        ringToneuri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
/*

        Ringtone r = RingtoneManager.getRingtone(this, ringToneuri);
        String ringToneName = r.getTitle(this);
        RingTone.setText(ringToneName);
*/

        spinnerPriority = (Spinner) findViewById(R.id.spinner_Priority);
        spinnerPriority.setOnItemSelectedListener(this);
        //   ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,priority);

        //   setRingtoneUri = ringToneuri;
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, priority) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerPriority.setAdapter(aa);

        btnChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(TextNoteActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.change_color);
                // Set dialog title
                Button redButton = (Button) dialog.findViewById(R.id.red);
                redButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        scrollView.setBackgroundResource(R.color.scrollRed);
                        btnChangeColor.setBackgroundResource(R.color.buttonRed);
                        currentCat = 1;
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });
                Button orangeButton = (Button) dialog.findViewById(R.id.orange);

                orangeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        scrollView.setBackgroundResource(R.color.scrollOrange);
                        btnChangeColor.setBackgroundResource(R.color.buttonOrange);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 2;
                        dialog.dismiss();

                    }
                });

                Button yellowButton = (Button) dialog.findViewById(R.id.yellow);

                yellowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollView.setBackgroundResource(R.color.scrollYellow);
                        btnChangeColor.setBackgroundResource(R.color.buttonYellow);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 3;
                        dialog.dismiss();
                    }
                });

                Button greenButton = (Button) dialog.findViewById(R.id.green);

                greenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollView.setBackgroundResource(R.color.scrollGreen);
                        btnChangeColor.setBackgroundResource(R.color.buttonGreen);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 4;
                        dialog.dismiss();

                    }
                });

                Button blueButton = (Button) dialog.findViewById(R.id.blue);

                blueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        scrollView.setBackgroundResource(R.color.scrollBlue);
                        btnChangeColor.setBackgroundResource(R.color.buttonBlue);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 5;
                        dialog.dismiss();
                    }
                });

                Button violetButton = (Button) dialog.findViewById(R.id.violet);

                violetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollView.setBackgroundResource(R.color.scrollVilote);
                        btnChangeColor.setBackgroundResource(R.color.buttonVilote);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 6;
                        dialog.dismiss();
                    }
                });

                Button pinkButton = (Button) dialog.findViewById(R.id.pink);

                pinkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollView.setBackgroundResource(R.color.scrollPint);
                        btnChangeColor.setBackgroundResource(R.color.buttonPint);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 7;
                        dialog.dismiss();
                    }
                });
                Button graytButton = (Button) dialog.findViewById(R.id.gray);

                graytButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollView.setBackgroundResource(R.color.scrollGray);
                        btnChangeColor.setBackgroundResource(R.color.buttonGray);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 8;
                        dialog.dismiss();

                    }
                });
                Button whitetButton = (Button) dialog.findViewById(R.id.white);

                whitetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollView.setBackgroundResource(R.color.scrollWhite);
                        btnChangeColor.setBackgroundResource(R.color.buttonWhite);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        currentCat = 9;
                        dialog.dismiss();

                    }
                });

                dialog.show();


            }
        });

        loadActivity(true);
    }


    private void loadActivity(boolean initial) {
        //Look for a note ID in the intent. If we got one, then we will edit that note. Otherwise we create a new one.
        if (id == -1) {
            Intent intent = getIntent();
            id = intent.getIntExtra(EXTRA_ID, -1);
         }
        edit = (id != -1);

        SimpleCursorAdapter adapter = null;
        // Should we set a custom font size?
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean(SettingsActivity.PREF_CUSTOM_FONT, false)) {
            etContent.setTextSize(Float.parseFloat(sp.getString(SettingsActivity.PREF_CUTSOM_FONT_SIZE, "15")));
            etName.setTextSize(Float.parseFloat(sp.getString(SettingsActivity.PREF_CUTSOM_FONT_SIZE, "15")));
        }

        //CategorySpinner
        Cursor c = DbAccess.getCategories(getBaseContext());
        if (c.getCount() == 0) {
            displayCategoryDialog();
        } else {
            String[] from = {DbContract.CategoryEntry.COLUMN_NAME};
            int[] to = {R.id.text1};
            adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.simple_spinner_item, c, from, to, CursorAdapter.FLAG_AUTO_REQUERY);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Cursor c = (Cursor) parent.getItemAtPosition(position);
                    currentCat = c.getInt(c.getColumnIndexOrThrow(DbContract.CategoryEntry.COLUMN_ID));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        //fill in values if update
        if (edit) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            noteCursor = DbAccess.getNote(getBaseContext(), id);
            noteCursor.moveToFirst();
            notificationCursor = DbAccess.getNotification(getBaseContext(), id);
            notificationCursor.moveToFirst();
            findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
            findViewById(R.id.choose_ringtone).setVisibility(View.VISIBLE);
            etName.setText(noteCursor.getString(noteCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_NAME)));
            etContent.setText(noteCursor.getString(noteCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_CONTENT)));
            //    RingTone.setText(notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_RINGTONE)));
            //find the current category and set spinner to that
            currentCat = noteCursor.getInt(noteCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_COLOR));
            int Priority = noteCursor.getInt(noteCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_PRIORITY));


            ringToneuri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

           /* Ringtone r = RingtoneManager.getRingtone(this, ringToneuri);
            String ringToneName = r.getTitle(this);
            RingTone.setText(ringToneName);*/


            spinnerPriority.setSelection(Priority);

            try {
                String ringtoneUri1 = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_RINGTONE));
                //   setRingtoneUri = ringtoneUri1;
                Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(ringtoneUri1));
                String ringToneName = r.getTitle(this);
                RingTone.setText(ringToneName);
             } catch (Exception e) {
             }


            if (currentCat == 1) {
                scrollView.setBackgroundResource(R.color.scrollRed);
                btnChangeColor.setBackgroundResource(R.color.buttonRed);

            } else if (currentCat == 2) {
                scrollView.setBackgroundResource(R.color.scrollOrange);
                btnChangeColor.setBackgroundResource(R.color.buttonOrange);


            } else if (currentCat == 3) {
                scrollView.setBackgroundResource(R.color.scrollYellow);
                btnChangeColor.setBackgroundResource(R.color.buttonYellow);

            } else if (currentCat == 4) {
                scrollView.setBackgroundResource(R.color.scrollGreen);
                btnChangeColor.setBackgroundResource(R.color.buttonGreen);

            } else if (currentCat == 5) {

                scrollView.setBackgroundResource(R.color.scrollBlue);
                btnChangeColor.setBackgroundResource(R.color.buttonBlue);


            } else if (currentCat == 6) {
                scrollView.setBackgroundResource(R.color.scrollVilote);
                btnChangeColor.setBackgroundResource(R.color.buttonVilote);


            } else if (currentCat == 7) {
                scrollView.setBackgroundResource(R.color.scrollPint);
                btnChangeColor.setBackgroundResource(R.color.buttonPint);


            } else if (currentCat == 8) {

                scrollView.setBackgroundResource(R.color.scrollGray);
                btnChangeColor.setBackgroundResource(R.color.buttonGray);
                //   card_view.setBackgroundResource(R.color.scrollGray);
            } else if (currentCat == 9) {
                scrollView.setBackgroundResource(R.color.scrollWhite);
                btnChangeColor.setBackgroundResource(R.color.buttonWhite);

                //  card_view.setBackgroundResource(R.color.scrollWhite);
            }




/*
            for (int i = 0; i < adapter.getCount(); i++){
                c.moveToPosition(i);
                if (c.getInt(c.getColumnIndexOrThrow(DbContract.CategoryEntry.COLUMN_ID)) == currentCat) {
                    spinner.setSelection(i);
                    break;
                }
            }*/
            //fill the notificationCursor
            notificationCursor = DbAccess.getNotificationByNoteId(getBaseContext(), id);
            hasAlarm = notificationCursor.moveToFirst();
            if (hasAlarm) {
                notification_id = notificationCursor.getInt(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_ID));
            }
            findViewById(R.id.btn_delete).setEnabled(true);
            findViewById(R.id.choose_ringtone).setEnabled(true);
            ((Button) findViewById(R.id.btn_save)).setText(getString(R.string.action_update));
        } else {

            findViewById(R.id.btn_delete).setEnabled(false);
            findViewById(R.id.choose_ringtone).setEnabled(false);


        }
        if (!initial) {
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //The Activity is not visible anymore. Save the work!

        if (shouldSave) {





            if (edit) {
                fillNameIfEmpty();
                DbAccess.updateNote(getBaseContext(), id, etName.getText().toString(), etContent.getText().toString(), currentCat, dbPriority);
                Toast.makeText(getApplicationContext(), R.string.toast_updated, Toast.LENGTH_SHORT).show();

                if (alarmtime != null) {
                    updateNote();
                }
            } else {
                saveNote();




            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActivity(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (edit) {
            getMenuInflater().inflate(R.menu.notes_text, menu);
//            MenuItem item = menu.findItem(R.id.action_share);
//            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//            setShareIntent();
        } else {
            // getMenuInflater().inflate(R.menu.notes_text, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_reminder);
        if (hasAlarm) {
            item.setIcon(R.drawable.ic_alarm_on_white_24dp);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        setShareIntent();

        if (setRingtoneUri != null) {

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_reminder) {
                //open the schedule dialog
                final Calendar c = Calendar.getInstance();
                if (hasAlarm) {
                    //ask whether to delete or update the current alarm
                    PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_reminder));
                    popupMenu.inflate(R.menu.reminder);
                    popupMenu.setOnMenuItemClickListener(this);
                    popupMenu.show();
                } else {
                    //create a new one
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dpd = new DatePickerDialog(TextNoteActivity.this, this, year, month, day);
                    dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                    dpd.show();
                }
                return true;
            }
        } else {

            final AlertDialog.Builder alertbox = new AlertDialog.Builder(TextNoteActivity.this);

            alertbox.setTitle("Please select Ringtone");
            alertbox.setCancelable(false);

            alertbox.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0,
                                            int arg1) {


                            arg0.dismiss();
                        }
                    });

            alertbox.show();


            //  Toast.makeText(getApplicationContext(), "Please select Ringtone", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                Toast.makeText(getBaseContext(), R.string.toast_canceled, Toast.LENGTH_SHORT).show();
                shouldSave = false;
                finish();
//                TextNoteActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btn_delete:
                if (edit) { //note only exists in edit mode
                    displayTrashDialog();
                }
//                TextNoteActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.choose_ringtone:
                if (edit) { //note only exists in edit mode
                    changeRingtone();
                }
//                TextNoteActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btn_save:
                if (edit) { //note only exists in edit mode
                finish();
                }else {
                    addcalender();
                }

                shouldSave = true; //safe on exit

//                TextNoteActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
        }
    }

    private void addcalender() {


        final AlertDialog.Builder alertbox = new AlertDialog.Builder(TextNoteActivity.this);

        alertbox.setTitle("Do you want to add in Calender?");
        alertbox.setCancelable(false);

        alertbox.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {

                        addtoCalender(etContent.getText().toString(),etName.getText().toString());
                        arg0.dismiss();
                    }
                });
        alertbox.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {

                        finish();
                        arg0.dismiss();
                    }
                });


        alertbox.show();
    }




    private void changeRingtone() {
        notificationCursor = DbAccess.getNotification(getBaseContext(), id);
        notificationCursor.moveToFirst();


        try {
            String ringtoneUri2 = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_RINGTONE));
            setRingtoneUri2 = Uri.parse(ringtoneUri2);

        } catch (Exception e) {

        }
        if (setRingtoneUri2 != null) {

            String ringtoneUri1 = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_RINGTONE));
            final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtoneUri1));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
            startActivityForResult(intent, REQUEST_CODE_RINGTONE);

        } else {

            Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(TextNoteActivity.this, RingtoneManager.TYPE_ALARM);
            final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
            startActivityForResult(intent, REQUEST_CODE_RINGTONE);
        }

        // final Uri currentTone= RingtoneManager.getActualDefaultRingtoneUri(TextNoteActivity.this, RingtoneManager.TYPE_ALARM);


      /*  Ringtone r = RingtoneManager.getRingtone(this, alert);
        String ringToneName = r.getTitle(this);
        RingTone.setText(ringToneName);*/

    }

    private void updateNote() {


        if (hasAlarm) {
            //Update the current alarm
            DbAccess.updateNotificationTime(getBaseContext(), notification_id, alarmtime.getTimeInMillis(), setRingtoneUri.toString());


        } else {
            notification_id = (int) (long) DbAccess.addNotification(getBaseContext(), id, alarmtime.getTimeInMillis(), setRingtoneUri.toString());




        }
    }


    private void addtoCalender(String category, String titletxt) {
        Intent calIntent = new Intent(Intent.ACTION_EDIT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, titletxt);
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, category);
        Calendar calStart=Calendar.getInstance();

        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calStart.getTimeInMillis());
        Calendar calEnd=Calendar.getInstance();

        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calEnd.getTimeInMillis());

        startActivity(calIntent);

        finish();

    }


    private void saveNote() {
        fillNameIfEmpty();
        id = DbAccess.addNote(getBaseContext(), etName.getText().toString(), etContent.getText().toString(),
                DbContract.NoteEntry.TYPE_TEXT, currentCat, dbPriority);





        // Toast.makeText(getApplicationContext(), R.string.toast_saved, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("StringFormatMatches")
    private void fillNameIfEmpty() {
        if (etName.getText().toString().isEmpty()) {
            SharedPreferences sp = getSharedPreferences(Preferences.SP_VALUES, Context.MODE_PRIVATE);
            int counter = sp.getInt(Preferences.SP_VALUES_NAMECOUNTER, 1);
            etName.setText(String.format(getString(R.string.note_standardname), counter));
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(Preferences.SP_VALUES_NAMECOUNTER, counter + 1);
            editor.commit();
        }
    }

    private void displayCategoryDialog() {
        new AlertDialog.Builder(TextNoteActivity.this)
                .setTitle(getString(R.string.dialog_need_category_title))
                .setMessage(getString(R.string.dialog_need_category_message))
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(TextNoteActivity.this, ManageCategoriesActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void displayTrashDialog() {
        SharedPreferences sp = getSharedPreferences(Preferences.SP_DATA, Context.MODE_PRIVATE);
        if (sp.getBoolean(Preferences.SP_DATA_DISPLAY_TRASH_MESSAGE, true)) {
            //we never displayed the message before, so show it now
            new AlertDialog.Builder(TextNoteActivity.this)
                    .setTitle(getString(R.string.dialog_trash_title))
                    .setMessage(getString(R.string.dialog_trash_message))
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shouldSave = false;
                            DbAccess.trashNote(getBaseContext(), id);
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Preferences.SP_DATA_DISPLAY_TRASH_MESSAGE, false);
            editor.commit();
        } else {
            shouldSave = false;
            DbAccess.trashNote(getBaseContext(), id);
            finish();
        }

    }

    private void displayDeleteDialog() {
        new AlertDialog.Builder(TextNoteActivity.this)
                .setTitle(String.format(getString(R.string.dialog_delete_title), etName.getText().toString()))
                .setMessage(String.format(getString(R.string.dialog_delete_message), etName.getText().toString()))
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shouldSave = false;
                        DbAccess.trashNote(getBaseContext(), id);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        this.monthOfYear = monthOfYear;
        this.year = year;
        final Calendar c = Calendar.getInstance();
        if (hasAlarm) {
            c.setTimeInMillis(notificationCursor.getLong(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_TIME)));
        }
        TimePickerDialog tpd = new TimePickerDialog(TextNoteActivity.this, this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        tpd.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarmtime = Calendar.getInstance();
        alarmtime.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);


        //Store a reference for the notification in the database. This is later used by the service.

        //Create the intent that is fired by AlarmManager
//        Intent i = new Intent(this, NotificationService.class);
//        i.putExtra(NotificationService.NOTIFICATION_ID, notification_id);


        int i1 = r.nextInt(80 - 65) + 65;
        final Intent notifIntent = new Intent(TextNoteActivity.this, AlarmLandingPageActivity.class);
        notifIntent.putExtra("uri", setRingtoneUri.toString());
        notifIntent.putExtra("name", etName.getText().toString());
        notifIntent.putExtra("desc", etContent.getText().toString());
        //   Log.e("TN>> a",setRingtoneUri.toString());
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pIntent = PendingIntent.getActivity(
                TextNoteActivity.this, i1, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

//        PendingIntent pi = PendingIntent.getService(this, notification_id, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmtime.getTimeInMillis(), pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmtime.getTimeInMillis(), pIntent);
        }
        Toast.makeText(getApplicationContext(), String.format(getString(R.string.toast_alarm_scheduled), dayOfMonth + "." + (monthOfYear + 1) + "." + year + " " + hourOfDay + ":" + String.format("%02d", minute)), Toast.LENGTH_SHORT).show();
        loadActivity(false);

    }

    private void cancelNotification() {
        //Create the intent that would be fired by AlarmManager
        Intent i = new Intent(this, NotificationService.class);
        i.putExtra(NotificationService.NOTIFICATION_ID, notification_id);

        PendingIntent pi = PendingIntent.getService(this, notification_id, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        DbAccess.deleteNotification(getBaseContext(), notification_id);
        loadActivity(false);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reminder_edit) {
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(notificationCursor.getLong(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_TIME)));
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(TextNoteActivity.this, this, year, month, day);
            dpd.getDatePicker().setMinDate(new Date().getTime());
            dpd.show();
            return true;
        } else if (id == R.id.action_reminder_delete) {
            cancelNotification();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Save the file
                    saveToExternalStorage();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_need_permission_write_external, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void saveToExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File path;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                path = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), "/PrivacyFriendlyNotes");
            } else {
                path = new File(Environment.getExternalStorageDirectory(), "/PrivacyFriendlyNotes");
            }

            File file = new File(path, "/text_" + etName.getText().toString() + ".txt");
            try {
                // Make sure the directory exists.
                boolean path_exists = path.exists() || path.mkdirs();
                if (path_exists) {

                    PrintWriter out = new PrintWriter(file);
                    out.println(etName.getText().toString());
                    out.println();
                    out.println(etContent.getText().toString());
                    out.close();
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                    MediaScannerConnection.scanFile(this,
                            new String[]{file.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });

                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.toast_file_exported_to), file.getAbsolutePath()), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.w("ExternalStorage", "Error writing " + file, e);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.toast_external_storage_not_mounted, Toast.LENGTH_LONG).show();
        }
    }

    private void setShareIntent() {
        if (mShareActionProvider != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, etName.getText().toString() + "\n\n" + etContent.getText().toString());
            mShareActionProvider.setShareIntent(sendIntent);
        }
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

         Ringtone r = RingtoneManager.getRingtone(this, ringToneuri);
        String ringToneName = r.getTitle(this);
        RingTone.setText(ringToneName);

        DbAccess.updateNotificationRing(getBaseContext(), notification_id, setRingtoneUri.toString());

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        dbPriority = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

