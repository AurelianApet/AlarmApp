package com.alarm.technothumb.alarmapplication.travel_record;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.note.AudioNoteActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AudioNote_RecordActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_ID = "org.secuso.privacyfriendlynotes.ID";

    private static final int REQUEST_CODE_AUDIO = 1;
    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 2;

    EditText etName;
    ImageButton btnPlayPause;
    ImageButton btnRecord;
    TextView tvRecordingTime;
    SeekBar seekBar;
    String newPhoto1 = " ";

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private Handler mHandler = new Handler();
    private String mFileName = "finde_die_datei.mp4";
    private String mFilePath;
    private boolean recording = false;
    private boolean playing = false;
    private long startTime = System.currentTimeMillis();

    public static int PLACE_PICKER_REQUEST = 999;
    public static int PLACE_PICKER_EDIT = 99;
    private boolean edit = false;
    private boolean hasAlarm = false;
    private boolean shouldSave = true, AudioRecord = false;
    private int id = -1;
    private int notification_id = -1;
    private int currentCat;
    Cursor noteCursor = null;
    Cursor notificationCursor = null;
    public static Double lat, log;

    String currentDateTime, newText;

    String latitude1,longitude1,country1,city1,state1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_note__record);
        setTitle("Audio Notes");

        currentDateTime = DateFormat.getDateTimeInstance().format(new Date());


        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        Log.e("AudioId", id+"");
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        btnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        btnRecord = (ImageButton) findViewById(R.id.btn_record);
        tvRecordingTime = (TextView) findViewById(R.id.recording_time);
//        spinner = (Spinner) findViewById(R.id.spinner_category);

        findViewById(R.id.btn_record).setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);



        if (ContextCompat.checkSelfPermission(AudioNote_RecordActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AudioNote_RecordActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(AudioNote_RecordActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_AUDIO);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(AudioNote_RecordActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_AUDIO);
            }
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mPlayer != null && fromUser) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        loadActivity(true);

    }

    private void loadActivity(boolean initial){
        //Look for a note ID in the intent. If we got one, then we will edit that note. Otherwise we create a new one.
        if (id == -1) {
            Intent intent = getIntent();
            id = intent.getIntExtra(EXTRA_ID, -1);
        }
       edit = (id != -1);

        SimpleCursorAdapter adapter = null;
        // Should we set a custom font size?


        //CategorySpinner
        Cursor c = DbAccess.getCategories(getBaseContext());
        if (c.getCount() == 0) {
            displayCategoryDialog();
        } else {
            String[] from = {DbContract.CategoryEntry.COLUMN_NAME};
            int[] to = {R.id.text1};
            adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.simple_spinner_item, c, from, to, CursorAdapter.FLAG_AUTO_REQUERY);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        }
        //fill in values if update
        if (edit) {

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            Intent intent = getIntent();
            String title = intent.getStringExtra("title");
            String audioPath = intent.getStringExtra("AudioPath");
            Button delete = (Button) findViewById(R.id.btn_delete) ;




          mFilePath = audioPath;

            btnPlayPause.setVisibility(View.VISIBLE);
            findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
            btnRecord.setVisibility(View.INVISIBLE);
            tvRecordingTime.setVisibility(View.INVISIBLE);
            etName.setText(title);




            for (int i = 0; i < adapter.getCount(); i++){
                c.moveToPosition(i);
                if (c.getInt(c.getColumnIndexOrThrow(DbContract.CategoryEntry.COLUMN_ID)) == currentCat) {
                    break;
                }
            }
            //fill the notificationCursor
            notificationCursor = DbAccess.getNotificationByNoteId(getBaseContext(), id);
            hasAlarm = notificationCursor.moveToFirst();
            if (hasAlarm) {
                notification_id = notificationCursor.getInt(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_ID));
            }
            findViewById(R.id.btn_delete).setEnabled(true);
            ((Button) findViewById(R.id.btn_save)).setText(getString(R.string.action_update));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    deleteNote(id);


                }
            });

        } else {

            mFileName = "/recording_" + System.currentTimeMillis() + ".aac";
            mFilePath = getFilesDir().getPath() + "/audio_notes";
            new File(mFilePath).mkdirs(); //ensure that the file exists
            mFilePath = getFilesDir().getPath() + "/audio_notes" + mFileName;
            seekBar.setEnabled(false);
            tvRecordingTime.setVisibility(View.VISIBLE);
            shouldSave = false; // will be set to true, once we have a recording
        }
        if(!initial) {
            invalidateOptionsMenu();
        }
    }

    private void deleteNote(int id) {

        DBOpenHelper db;
        db = new DBOpenHelper(this);
        db.isdelect(id);

        Intent intent = new Intent(AudioNote_RecordActivity.this, TravelRecordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //The Activity is not visible anymore. Save the work!
        if (shouldSave ) {
            if (edit) {
              //  updateNote();
            } else {
              //  saveNote();
            }
        } else {
            if(!edit) {
                new File(mFilePath).delete();
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadActivity(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            if (recording) {
                stopRecording();
                finish();
            } else if (playing) {
                pausePlaying();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                Toast.makeText(getBaseContext(), R.string.toast_canceled, Toast.LENGTH_SHORT).show();
                shouldSave = false;
                finish();
                break;
            case R.id.btn_delete:
                if (edit) {
                    displayTrashDialog();
                }
                break;
            case R.id.btn_save:
                shouldSave = true; //safe on exit
             //   finish();
                saveNote();
                break;
            case R.id.btn_record:
                if (!recording) {
                    startRecording();
                } else {
                    stopRecording();
                }
                break;
            case R.id.btn_play_pause:
                if (!playing) {
                    startPlaying();
                } else {
                    pausePlaying();
                }
                break;
            default:
        }
    }

    private void startRecording() {
        recording = true;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
            final Animation animation = new AlphaAnimation(1, (float)0.5); // Change alpha from fully visible to invisible
            animation.setDuration(500); // duration - half a second
            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            btnRecord.startAnimation(animation);
            startTime = System.currentTimeMillis();
            AudioNote_RecordActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRecorder != null) {
                        long time = System.currentTimeMillis() - startTime;
                        int seconds = (int) time / 1000;
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        tvRecordingTime.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                        mHandler.postDelayed(this, 100);
                    }
                }
            });

            mRecorder.start();
        } catch (IOException e) {
            recording = false;
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        Log.d("LALALA", "Stopped recording");
        mRecorder.stop();
        mRecorder.release();
        btnRecord.clearAnimation();
        mRecorder = null;
        recording = false;
        recordingFinished();
        AudioRecord = true;
    }

    private void startPlaying() {
        playing = true;
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(mFilePath);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playing = false;
                togglePlayPauseButton();
                seekBar.setProgress(0);
                mPlayer.release();
                mPlayer = null;
            }
        });

        togglePlayPauseButton();
        seekBar.setMax(mPlayer.getDuration());
        AudioNote_RecordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null) {
                    seekBar.setProgress(mPlayer.getCurrentPosition());
                    mHandler.postDelayed(this, 100);
                }
            }
        });
        mPlayer.start();
    }

    private void pausePlaying() {
        playing = false;
        togglePlayPauseButton();
        try {
            mPlayer.pause();
        } catch (RuntimeException stopException) {
        }
    }

    private void recordingFinished() {
        shouldSave = true;
        btnRecord.setVisibility(View.INVISIBLE);
        btnPlayPause.setVisibility(View.VISIBLE);
        seekBar.setEnabled(true);
    }

    private void togglePlayPauseButton(){
        if (playing) {
            btnPlayPause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
        } else {
            btnPlayPause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    private void updateNote(){
        fillNameIfEmpty();
        DbAccess.updateNote(getBaseContext(), id, etName.getText().toString(), mFileName, currentCat,
                latitude1,longitude1,country1,city1,state1,currentDateTime);
      //  Toast.makeText(getApplicationContext(), R.string.toast_updated, Toast.LENGTH_SHORT).show();
    }

    private void saveNote(){

        if(edit){

            Log.e("recording", recording+"");
            AudioRecord = true;
            if (etName.getText().toString().equals("")) {
                Toast.makeText(AudioNote_RecordActivity.this, "Enter Title", Toast.LENGTH_SHORT).show();
            } else if (!AudioRecord) {
                Toast.makeText(AudioNote_RecordActivity.this, "Pl. record  Audio", Toast.LENGTH_SHORT).show();
            } else {

                if (!isNetworkAvailable()) {
                    // Create an Alert Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(AudioNote_RecordActivity.this);
                    // Set the Alert Dialog Message
                    builder.setMessage("Internet Connection Required for Map")
                            .setCancelable(false)
                            .setPositiveButton("Retry",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            Intent intent = new Intent(AudioNote_RecordActivity.this, AudioNote_RecordActivity.class);
                                            startActivity(intent);
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                    // System.exit(0);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {


                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent intent;
                    try {
                        intent = builder.build(AudioNote_RecordActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_EDIT);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                }


            }


        }else {

            Log.e("recording", recording+"");
            if (etName.getText().toString().equals("")) {
                Toast.makeText(AudioNote_RecordActivity.this, "Enter Title", Toast.LENGTH_SHORT).show();
            } else if (!AudioRecord) {
                Toast.makeText(AudioNote_RecordActivity.this, "Pl. record  Audio", Toast.LENGTH_SHORT).show();
            } else {

                if (!isNetworkAvailable()) {
                    // Create an Alert Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(AudioNote_RecordActivity.this);
                    // Set the Alert Dialog Message
                    builder.setMessage("Internet Connection Required for Map")
                            .setCancelable(false)
                            .setPositiveButton("Retry",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            Intent intent = new Intent(AudioNote_RecordActivity.this, AudioNote_RecordActivity.class);
                                            startActivity(intent);
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                    // System.exit(0);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {


                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent intent;
                    try {
                        intent = builder.build(AudioNote_RecordActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

                }


            }
        }







    }

    private void insertNote(String noteText, String photoPath, Double latitude, Double longitude, String currentDateTime, String address, String city, String state, String Country, String audioFile, int type) {
        DBOpenHelper db;
        db = new DBOpenHelper(this);
        db.insertData(noteText, photoPath, latitude, longitude, currentDateTime, address,city, state,Country, audioFile, type);

    }

  /*  private void insertNote( String name, String mFileName, String lat, String lon, String currentDateTime) {
//
        ContentValues values = new ContentValues();
//
        values.put(DBOpenHelper.NOTE_TEXT, name);
        values.put(DBOpenHelper.AUDIOFILE, mFileName);
        values.put(DBOpenHelper.Latitude, lat);
        values.put(DBOpenHelper.Longitude, lon);
        values.put(DBOpenHelper.DateTime,currentDateTime);

        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);

    }*/

    @SuppressLint("StringFormatMatches")
    private void fillNameIfEmpty(){
        if (etName.getText().toString().isEmpty()) {
            SharedPreferences sp = getSharedPreferences(Preferences.SP_VALUES, Context.MODE_PRIVATE);
            int counter = sp.getInt(Preferences.SP_VALUES_NAMECOUNTER, 1);
            etName.setText(String.format(getString(R.string.note_standardname), counter));
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(Preferences.SP_VALUES_NAMECOUNTER, counter+1);
            editor.commit();
        }
    }

    private void displayCategoryDialog() {
        new AlertDialog.Builder(AudioNote_RecordActivity.this)
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
//                         startActivity(new Intent(AudioNoteActivity.this, ManageCategoriesActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void displayTrashDialog() {
        SharedPreferences sp = getSharedPreferences(Preferences.SP_DATA, Context.MODE_PRIVATE);
        if (sp.getBoolean(Preferences.SP_DATA_DISPLAY_TRASH_MESSAGE, true)){
            //we never displayed the message before, so show it now
            new AlertDialog.Builder(AudioNote_RecordActivity.this)
                    .setTitle("Delete Audio Record")
                    .setMessage("Are you Sure To Delete?")
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Do nothing. App should work
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_need_permission_audio, Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case REQUEST_CODE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Save the file
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_need_permission_write_external, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

//    private void getLocation() {
//        gpsTracker = new GPSTracker(getApplicationContext());
//        mLocation = gpsTracker.getLocation();
//        latitude = String.valueOf(mLocation.getLatitude());
//        longitude = String.valueOf(mLocation.getLongitude());
//
//        Log.e("Lati",String.valueOf(mLocation.getLatitude()));
//        Log.e("Longi",String.valueOf(mLocation.getLongitude()));
//    }

private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
        .getActiveNetworkInfo();
        return activeNetworkInfo != null;

        }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){


                if(resultCode==RESULT_OK){
                    if(requestCode== PLACE_PICKER_REQUEST){
                    Place place = PlacePicker.getPlace(data,this);


                    String address = String.format("%s, ", place.getName());
                    lat = place.getLatLng().latitude;
                    log = place.getLatLng().longitude;

                    android.util.Log.e("Lat", lat+"|");
                    android.util.Log.e("Log", log+"");
                    String AudioFile = mFilePath;
                    int Type = 2;
                    newText = etName.getText().toString();


                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());
                        String city = "", state="",country = "";


                        try {
                            addresses = geocoder.getFromLocation(lat, log, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();

                            if((city+"").equals("null")){
                                city = "";
                            }
                            if((state+"").equals("null")){
                                state = "";
                            }


                        } catch (IOException e) {
                          //  e.printStackTrace();

                        }
                        catch (Exception e)
                        {

                        }



                        insertNote(newText, newPhoto1, lat, log, currentDateTime, address, city,state, country, AudioFile, Type);

                    Intent i =new Intent(AudioNote_RecordActivity.this,TravelRecordActivity.class);
                    startActivity(i);



                }

                    if(requestCode== PLACE_PICKER_EDIT){
                        Place place = PlacePicker.getPlace(data,this);


                        String address = String.format("%s, ", place.getName());
                        lat = place.getLatLng().latitude;
                        log = place.getLatLng().longitude;

                        android.util.Log.e("Lat", lat+"|");
                        android.util.Log.e("Log", log+"");
                        String AudioFile = mFilePath;
                        int Type = 2;
                        newText = etName.getText().toString();
                        newPhoto1="";

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());
                        String city = "", state="",country = "";


                        try {
                            addresses = geocoder.getFromLocation(lat, log, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();

                        } catch (IOException e) {

                        }catch (Exception e)
                        {

                        }

                        //insertNote(newText, newPhoto1, lat, log, currentDateTime, address, AudioFile, Type);
                        updateNote2(newText, newPhoto1, lat, log, currentDateTime, address,city,state, country,  AudioFile, Type, id);
                        Intent i =new Intent(AudioNote_RecordActivity.this,TravelRecordActivity.class);
                        startActivity(i);



                    }
            }


    }

    private void updateNote2(String newText, String newPhoto1, Double lat, Double log, String currentDateTime, String address, String city, String State, String Country,String audioFile, int type, int id) {

        DBOpenHelper db;
        db = new DBOpenHelper(this);
        db.updateData(newText, newPhoto1, lat, log, currentDateTime, address, city,State, Country,audioFile, type, id);



    }
}
