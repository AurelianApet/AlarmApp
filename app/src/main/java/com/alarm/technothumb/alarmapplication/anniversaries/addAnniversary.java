package com.alarm.technothumb.alarmapplication.anniversaries;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.note.TextNoteActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class addAnniversary extends AppCompatActivity {

    Spinner spinner1;
    String[] plants = new String[]{
            "Anniversary",
            "Birthday",
            "Wedding Day",
            "Day of Departure",
            "Public Holiday",
            "Others"
    };
    List<String> stringlist;
    ArrayAdapter<String> adapter;
    EditText anneveryTitle, etBirthdate;
    ImageView bithdateIcon;
    Button btnAnniversary;
    public static dbHelper db;
    String Title;
    Toolbar toolbar_aboutus;
    Button btnNotification;
    int AnniID;
    Boolean edit = false;
    Intent intent;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anniversary);


        stringlist = new ArrayList<>(Arrays.asList(plants));

        toolbar_aboutus = (Toolbar) findViewById(R.id.tooldarAnniversary); // get the reference of Toolbar
        setSupportActionBar(toolbar_aboutus);
        //  setTitle("CHECKOUT");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stringlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1 = findViewById(R.id.spinner1);
        anneveryTitle = (EditText) findViewById(R.id.anniveryTitle);
        etBirthdate = (EditText) findViewById(R.id.etBirthdate);
        bithdateIcon = (ImageView) findViewById(R.id.iconBithdate);
        btnAnniversary = (Button) findViewById(R.id.btnAnniversary);
        spinner1.setAdapter(adapter);

        intent = getIntent();
        edit = intent.getBooleanExtra("edit", false);

        if (edit) {
            editAnniverseary();
        } else {


            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                String abc = spinner1.getSelectedItem().toString();

                    //mEditTextTitle.setText(plants[position]+ event.title);
                    anneveryTitle.setHint("Type here... " + plants[position] + " Date");
                    Title = (plants[position]);
                    AnniID = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });

            btnNotification = (Button) findViewById(R.id.button);

            btnNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendNotification();

                }
            });

            final Calendar c = Calendar.getInstance();
            int yy = c.get(Calendar.YEAR);
            int mm = c.get(Calendar.MONTH);
            int dd = c.get(Calendar.DAY_OF_MONTH);

            // set current date into textview
            etBirthdate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(dd).append("-").append(mm + 1).append("-").append(yy));


            bithdateIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myCalendar = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String myFormat = "dd-MM-yy";

                            //   Calendar cal = Calendar.getInstance();

                            Log.e("beingTime", myCalendar.getTimeInMillis() + "");
                            Log.e("endTime", myCalendar.getTimeInMillis() + 60 * 60 * 1000 + "");


                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                            etBirthdate.setText(sdf.format(myCalendar.getTime()));


                        }

                    };
                    new DatePickerDialog(addAnniversary.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();


                }
            });


            btnAnniversary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    db = new dbHelper(addAnniversary.this);

                    String date = etBirthdate.getText().toString();
                    final String category = Title;
                    final String Titletxt = anneveryTitle.getText().toString();
                    Log.e("data", date);
                    Log.e("category", category);
                    anniversaryModel getset = new anniversaryModel();
                    getset.setDate(date);
                    getset.setTitle(category);
                    getset.setCategory(Titletxt);
                    getset.setAnniID(AnniID);

                    db.insertData(getset);

                    final AlertDialog.Builder alertbox = new AlertDialog.Builder(addAnniversary.this);

                    alertbox.setTitle("Do you want to add in Calender?");
                    alertbox.setCancelable(false);

                    alertbox.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0,
                                                    int arg1) {

                                    addtoCalender(category,Titletxt);
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


                    //  Toast.makeText(getApplicationContext(), "Please select Ringtone", Toast.LENGTH_SHORT).show();




                  /*  try {
                        ContentResolver cr = addAnniversary.this.getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put(CalendarContract.Events.DTSTART,  myCalendar.getTimeInMillis());
                        values.put(CalendarContract.Events.DTEND, myCalendar.getTimeInMillis()+60*60*1000);
                        values.put(CalendarContract.Events.TITLE, category+"-"+Titletxt);
                        values.put(CalendarContract.Events.DESCRIPTION, Titletxt);
                        values.put(CalendarContract.Events.EVENT_LOCATION,"");
                        values.put(CalendarContract.Events.HAS_ALARM,1);
                        values.put(CalendarContract.Events.CALENDAR_ID, 1);
                        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                                .getTimeZone().getID());
                        System.out.println(Calendar.getInstance().getTimeZone().getID());
                        if (ActivityCompat.checkSelfPermission(addAnniversary.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                        long eventId = Long.parseLong(uri.getLastPathSegment());
                        Log.e("Ketan_Event_Id", String.valueOf(eventId));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
*/
/*
                            Intent intent1 = new Intent(addAnniversary.this, Anniversary.class);
                            startActivity(intent1);
                            finish();*/

                   /*
*/
                }
            });

        }

    }

    private void addtoCalender(String category, String titletxt) {
        Intent calIntent = new Intent(Intent.ACTION_EDIT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, category+"-"+titletxt);
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, titletxt);
        Calendar calStart=Calendar.getInstance();

        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calStart.getTimeInMillis());
        Calendar calEnd=Calendar.getInstance();

        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calEnd.getTimeInMillis());

        startActivity(calIntent);

        finish();

    }

    private void editAnniverseary() {


      String Date = intent.getStringExtra("date");
        String TitleEdit = intent.getStringExtra("Title");
        int AnniIDEdit = intent.getIntExtra("AnniID", 0);
        final int id = intent.getIntExtra("id", 0);

        etBirthdate.setText(Date);
        anneveryTitle.setText(TitleEdit);
        spinner1.setSelection(AnniIDEdit);
        btnAnniversary.setText("Update");
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                String abc = spinner1.getSelectedItem().toString();

                //mEditTextTitle.setText(plants[position]+ event.title);
                anneveryTitle.setHint("Type here... " + plants[position] + " Date");
                Title = (plants[position]);
                AnniID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });




        bithdateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd-MM-yy";

                        /*if(Title.equals("Wedding Day")){
                            myFormat = "dd-MM-yy";
                        }else if (Title.equals("Day of Departure")){
                            myFormat = "dd-MM-yy";
                        }else {
                            myFormat = "dd-MM";
                        }*/

                        // your format
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                        etBirthdate.setText(sdf.format(myCalendar.getTime()));


                    }

                };
                new DatePickerDialog(addAnniversary.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();


            }
        });



        btnAnniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db = new dbHelper(addAnniversary.this);

                String date = etBirthdate.getText().toString();
                String category = Title;
                String Titletxt = anneveryTitle.getText().toString();

                anniversaryModel getset = new anniversaryModel();
                getset.setDate(date);
                getset.setTitle(category);
                getset.setCategory(Titletxt);
                getset.setAnniID(AnniID);
                getset.setTitleID(id);


                db.updateData(getset);




                Intent intent = new Intent(addAnniversary.this, Anniversary.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendNotification() {

        final Uri currentTone= RingtoneManager.getActualDefaultRingtoneUri(addAnniversary.this, RingtoneManager.TYPE_ALARM);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
        startActivityForResult( intent, 999);

      /*  NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_action_anniversay);
        Intent intent = new Intent(addAnniversary.this, Anniversary.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
       // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_notification));
        builder.setContentTitle("Notifications Title");
        builder.setContentText("Your notification content here.");
        builder.setSubText("Tap to view the website.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());*/
    }
}
