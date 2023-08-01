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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.alarm.AlarmLandingPageActivity;
import com.alarm.technothumb.alarmapplication.anniversaries.addAnniversary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChecklistNoteActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    public static final String EXTRA_ID = "org.secuso.privacyfriendlynotes.ID";

    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 1;


    EditText etName;
    EditText etNewItem;
    ListView lvItemList, completeItemList;
    Spinner spinner;
    Button btnChangeColor;
    private ShareActionProvider mShareActionProvider = null;
    public Uri alert, ringToneuri, setRingtoneUri, setRingtoneUri2;
    private int dayOfMonth, monthOfYear, year;

    private boolean edit = false;
    private boolean hasAlarm = false;
    private boolean shouldSave = true;
    private int id = -1;
    private int notification_id = -1;
    private int currentCat;
    private int colorID;
    Cursor noteCursor = null;
    Cursor notificationCursor = null;
    String[] priority = {"Low", "Medium ", "High"};
    int dbPriority;
    private static final int REQUEST_CODE_RINGTONE = 999;
    private ArrayList<CheckListItem> itemNamesList = new ArrayList<>();
    private ArrayList<CheckListItem> completeItemNamesList = new ArrayList<>();
    String currentDateTime;
    TextView complete, RingTone;
    Spinner spinnerPriority;
    Calendar alarmtime;
    Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_note);
        setTitle("Checklist Note");

        r = new Random();
        currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.choose_ringtone).setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etNewItem = (EditText) findViewById(R.id.etNewItem);
        lvItemList = (ListView) findViewById(R.id.itemList);
        completeItemList = (ListView) findViewById(R.id.complteList);
        //  spinner = (Spinner) findViewById(R.id.spinner_category);
        btnChangeColor = (Button) findViewById(R.id.ColorChange);
        complete = (TextView) findViewById(R.id.complete);
        spinnerPriority = (Spinner) findViewById(R.id.spinner_Priority);
        spinnerPriority.setOnItemSelectedListener(this);
        RingTone = (TextView) findViewById(R.id.choose_ringtone);
        //   ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,priority);

        setListViewHeightBasedOnChildren(lvItemList);
        setListViewHeightBasedOnChildren(completeItemList);

        ringToneuri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

       /* Ringtone r = RingtoneManager.getRingtone(this, ringToneuri);
        String ringToneName = r.getTitle(this);
        RingTone.setText(ringToneName);*/

        spinnerPriority = (Spinner) findViewById(R.id.spinner_Priority);
        spinnerPriority.setOnItemSelectedListener(this);
        //   ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,priority);

        setRingtoneUri = ringToneuri;
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

                final Dialog dialog = new Dialog(ChecklistNoteActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.change_color);
                // Set dialog title
                Button redButton = (Button) dialog.findViewById(R.id.red);
                redButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //   scrollView.setBackgroundResource(R.color.scrollRed);
                        btnChangeColor.setBackgroundResource(R.color.buttonRed);
                        colorID = 1;
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });
                Button orangeButton = (Button) dialog.findViewById(R.id.orange);

                orangeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //     scrollView.setBackgroundResource(R.color.scrollOrange);
                        btnChangeColor.setBackgroundResource(R.color.buttonOrange);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 2;
                        dialog.dismiss();

                    }
                });

                Button yellowButton = (Button) dialog.findViewById(R.id.yellow);

                yellowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //    scrollView.setBackgroundResource(R.color.scrollYellow);
                        btnChangeColor.setBackgroundResource(R.color.buttonYellow);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 3;
                        dialog.dismiss();
                    }
                });

                Button greenButton = (Button) dialog.findViewById(R.id.green);

                greenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //     scrollView.setBackgroundResource(R.color.scrollGreen);
                        btnChangeColor.setBackgroundResource(R.color.buttonGreen);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 4;
                        dialog.dismiss();

                    }
                });

                Button blueButton = (Button) dialog.findViewById(R.id.blue);

                blueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //    scrollView.setBackgroundResource(R.color.scrollBlue);
                        btnChangeColor.setBackgroundResource(R.color.buttonBlue);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 5;
                        dialog.dismiss();
                    }
                });

                Button violetButton = (Button) dialog.findViewById(R.id.violet);

                violetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //    scrollView.setBackgroundResource(R.color.scrollVilote);
                        btnChangeColor.setBackgroundResource(R.color.buttonVilote);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 6;
                        dialog.dismiss();
                    }
                });

                Button pinkButton = (Button) dialog.findViewById(R.id.pink);

                pinkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  scrollView.setBackgroundResource(R.color.scrollPint);
                        btnChangeColor.setBackgroundResource(R.color.buttonPint);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 7;
                        dialog.dismiss();
                    }
                });
                Button graytButton = (Button) dialog.findViewById(R.id.gray);

                graytButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //   scrollView.setBackgroundResource(R.color.scrollGray);
                        btnChangeColor.setBackgroundResource(R.color.buttonGray);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 8;
                        dialog.dismiss();

                    }
                });
                Button whitetButton = (Button) dialog.findViewById(R.id.white);

                whitetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  scrollView.setBackgroundResource(R.color.scrollWhite);
                        btnChangeColor.setBackgroundResource(R.color.buttonWhite);
                        //  Toast.makeText(TextNoteActivity.this, "red", Toast.LENGTH_SHORT).show();
                        colorID = 9;
                        dialog.dismiss();

                    }
                });

                dialog.show();


            }
        });


        loadActivity(true);
    }

    private void loadActivity(boolean initial) {
        //get rid of the old data. Otherwise we would have duplicates.
        itemNamesList.clear();
        completeItemNamesList.clear();
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
            etName.setTextSize(Float.parseFloat(sp.getString(SettingsActivity.PREF_CUTSOM_FONT_SIZE, "15")));
            etNewItem.setTextSize(Float.parseFloat(sp.getString(SettingsActivity.PREF_CUTSOM_FONT_SIZE, "15")));
        }



        lvItemList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvItemList.setOnItemClickListener(this);
        lvItemList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                ArrayAdapter a = (ArrayAdapter) lvItemList.getAdapter();
                CheckListItem temp = (CheckListItem) a.getItem(position);
                temp.setBgChecked(checked);
                a.notifyDataSetChanged();

                int isMultiple = 0;
                for (int im = 0;im<itemNamesList.size();im++)
                {
                    CheckListItem cl = (CheckListItem) a.getItem(im);
                    if (cl.isBgChecked())
                    {
                        isMultiple = isMultiple + 1;
                    }
                }

                if (mode.getMenu() != null) {
                    MenuItem item = (mode.getMenu()).findItem(R.id.action_edit);
                    if (isMultiple > 1) {

                        item.setVisible(false);
                    }
                    else {
                        item.setVisible(true);
                    }
                }
             }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.checklist_cab, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.action_edit:
                        updateSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ArrayAdapter a = (ArrayAdapter) lvItemList.getAdapter();

                for (int im = 0;im<itemNamesList.size();im++)
                {
                    CheckListItem temp = (CheckListItem) a.getItem(im);
                    temp.setBgChecked(false);
                }
                a.notifyDataSetChanged();

                ArrayAdapter b = (ArrayAdapter) completeItemList.getAdapter();
                b.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(lvItemList);
                setListViewHeightBasedOnChildren(completeItemList);
            }
        });


        lvItemList.setAdapter(new unCheckListAdapter(getBaseContext(), R.layout.item_uncheck, itemNamesList));
        completeItemList.setAdapter(new CheckListAdapter(getBaseContext(), R.layout.item_checklist, completeItemNamesList));
        setListViewHeightBasedOnChildren(lvItemList);
        setListViewHeightBasedOnChildren(completeItemList);
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
            try {
                JSONArray content = new JSONArray(noteCursor.getString(noteCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_CONTENT)));
                for (int i = 0; i < content.length(); i++) {
                    JSONObject o = content.getJSONObject(i);
                    //Log.e("Boolean", o.getBoolean("checked")+"");
                    if (o.getBoolean("checked")) {
                        completeItemNamesList.add(new CheckListItem(o.getBoolean("checked"), o.getString("name")));
                        //  itemNamesList.remove(new CheckListItem(o.getBoolean("checked"), o.getString("name")));

                    } else {
                        itemNamesList.add(new CheckListItem(o.getBoolean("checked"), o.getString("name")));
                        //   completeItemNamesList.remove(new CheckListItem(o.getBoolean("checked"), o.getString("name")));
                    }


                }


                ((ArrayAdapter) lvItemList.getAdapter()).notifyDataSetChanged();
                ((ArrayAdapter) completeItemList.getAdapter()).notifyDataSetChanged();
                setListViewHeightBasedOnChildren(lvItemList);
                setListViewHeightBasedOnChildren(completeItemList);
                if (completeItemNamesList.size() == 0) {
                    complete.setVisibility(View.GONE);
                } else {
                    complete.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String ringtoneUri1 = notificationCursor.getString(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_RINGTONE));
                //   setRingtoneUri = ringtoneUri1;
                Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(ringtoneUri1));
                String ringToneName = r.getTitle(this);
                RingTone.setText(ringToneName);
                Log.e("ringToneURi", ringtoneUri1);
            } catch (Exception e) {

            }
            //find the current category and set spinner to that
            colorID = noteCursor.getInt(noteCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_COLOR));
            int Priority = noteCursor.getInt(noteCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_PRIORITY));


            spinnerPriority.setSelection(Priority);

            if (colorID == 1) {
                //  scrollView.setBackgroundResource(R.color.scrollRed);
                btnChangeColor.setBackgroundResource(R.color.buttonRed);

            } else if (colorID == 2) {
                //  scrollView.setBackgroundResource(R.color.scrollOrange);
                btnChangeColor.setBackgroundResource(R.color.buttonOrange);


            } else if (colorID == 3) {
                //  scrollView.setBackgroundResource(R.color.scrollYellow);
                btnChangeColor.setBackgroundResource(R.color.buttonYellow);

            } else if (colorID == 4) {
                //    scrollView.setBackgroundResource(R.color.scrollGreen);
                btnChangeColor.setBackgroundResource(R.color.buttonGreen);

            } else if (colorID == 5) {

                //  scrollView.setBackgroundResource(R.color.scrollBlue);
                btnChangeColor.setBackgroundResource(R.color.buttonBlue);


            } else if (colorID == 6) {
                //     scrollView.setBackgroundResource(R.color.scrollVilote);
                btnChangeColor.setBackgroundResource(R.color.buttonVilote);


            } else if (colorID == 7) {
                //   scrollView.setBackgroundResource(R.color.scrollPint);
                btnChangeColor.setBackgroundResource(R.color.buttonPint);


            } else if (colorID == 8) {

                //    scrollView.setBackgroundResource(R.color.scrollGray);
                btnChangeColor.setBackgroundResource(R.color.buttonGray);
                //   card_view.setBackgroundResource(R.color.scrollGray);
            } else if (colorID == 9) {
                //    scrollView.setBackgroundResource(R.color.scrollWhite);
                btnChangeColor.setBackgroundResource(R.color.buttonWhite);

                //  card_view.setBackgroundResource(R.color.scrollWhite);
            }


            /*for (int i = 0; i < adapter.getCount(); i++){
                c.moveToPosition(i);
                if (c.getInt(c.getColumnIndexOrThrow(DbContract.CategoryEntry.COLUMN_ID)) == currentCat) {
                    spinner.setSelection(i);
                    break;
                }
            }
*/            //fill the notificationCursor
            notificationCursor = DbAccess.getNotificationByNoteId(getBaseContext(), id);
            hasAlarm = notificationCursor.moveToFirst();
            if (hasAlarm) {
                notification_id = notificationCursor.getInt(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_ID));
            }
            findViewById(R.id.btn_delete).setEnabled(true);
            ((Button) findViewById(R.id.btn_save)).setText(getString(R.string.action_update));
        } else {
            findViewById(R.id.btn_delete).setEnabled(false);
        }
        if (!initial) {
            invalidateOptionsMenu();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //The Activity is not visible anymore. Save the work!
        Log.e("EDIT", shouldSave + " : " + edit + " : " + alarmtime);
        if (shouldSave) {
            if (edit) {
                Log.e("EDIT", edit + "");
                updateList();
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
            getMenuInflater().inflate(R.menu.checklist, menu);
//            MenuItem item = menu.findItem(R.id.action_share);
//            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//            setShareIntent();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reminder) {
            //open the schedule dialog
            if (setRingtoneUri != null) {
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

                    DatePickerDialog dpd = new DatePickerDialog(ChecklistNoteActivity.this, this, year, month, day);
                    dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                    dpd.show();
                }
                return true;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please select Ringtone", Toast.LENGTH_SHORT).show();
        }
//        else if (id == R.id.action_save) {
//            if (ContextCompat.checkSelfPermission(ChecklistNoteActivity.this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                // Should we show an explanation?
//                if (ActivityCompat.shouldShowRequestPermissionRationale(ChecklistNoteActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    // Show an expanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//                    ActivityCompat.requestPermissions(ChecklistNoteActivity.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            REQUEST_CODE_EXTERNAL_STORAGE);
//                } else {
//                    // No explanation needed, we can request the permission.
//                    ActivityCompat.requestPermissions(ChecklistNoteActivity.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            REQUEST_CODE_EXTERNAL_STORAGE);
//                }
//            } else {
//                saveToExternalStorage();
//            }
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                Toast.makeText(getBaseContext(), R.string.toast_canceled, Toast.LENGTH_SHORT).show();
                shouldSave = false;
                finish();
//                ChecklistNoteActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btn_delete:
                if (edit) { //note only exists in edit mode
                    displayTrashDialog();
                }
//                ChecklistNoteActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btn_save:
                if (edit) { //note only exists in edit mode
                    finish();
                }else {
                    addcalender();
                }
                shouldSave = true; //safe on exit

//                ChecklistNoteActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.choose_ringtone:
                if (edit) { //note only exists in edit mode
                    changeRingtone();
                }
            case R.id.btn_add:
                if (!etNewItem.getText().toString().isEmpty()) {
                    itemNamesList.add(new CheckListItem(false, etNewItem.getText().toString()));
                    etNewItem.setText("");
                    ((ArrayAdapter) lvItemList.getAdapter()).notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(lvItemList);
                }
                break;
            default:
        }
    }

    private void addcalender() {

        final AlertDialog.Builder alertbox = new AlertDialog.Builder(ChecklistNoteActivity.this);

        alertbox.setTitle("Do you want to add in Calender?");
        alertbox.setCancelable(false);

        alertbox.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {

                        addtoCalender(etName.getText().toString());
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

    private void addtoCalender(String s) {
        Intent calIntent = new Intent(Intent.ACTION_EDIT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, s);
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "CheckList note from Ownotes");
        Calendar calStart=Calendar.getInstance();

        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calStart.getTimeInMillis());
        Calendar calEnd=Calendar.getInstance();

        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calEnd.getTimeInMillis());

        startActivity(calIntent);

        finish();



    }

    private void updateList() {
        Adapter a = lvItemList.getAdapter();
        Adapter b = completeItemList.getAdapter();
        JSONArray jsonArray = new JSONArray();

        Log.e("Update", itemNamesList.size() + "");
        try {
            CheckListItem temp;
            for (int i = 0; i < itemNamesList.size(); i++) {
                temp = (CheckListItem) a.getItem(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", temp.getName());
                jsonObject.put("checked", temp.isChecked());
                jsonArray.put(jsonObject);
                Log.e("item_name", temp.getName() + " : " + temp.isChecked());
            }
            fillNameIfEmpty();
            // DbAccess.updateNote(getBaseContext(), id, etName.getText().toString(), jsonArray.toString(), currentCat);
            for (int i = 0; i < completeItemNamesList.size(); i++) {
                temp = (CheckListItem) b.getItem(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", temp.getName());
                jsonObject.put("checked", temp.isChecked());
                jsonArray.put(jsonObject);

                Log.e("complete_name", temp.getName() + " : " + temp.isChecked());
            }

            DbAccess.updateNote(getBaseContext(), id, etName.getText().toString(), jsonArray.toString(), colorID, dbPriority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNote() {
        try {

            if (hasAlarm) {
                //Update the current alarm
                DbAccess.updateNotificationTime(getBaseContext(), notification_id, alarmtime.getTimeInMillis(), setRingtoneUri.toString());
                try {
                    Log.e("etName", etName.getText().toString());
                    ContentResolver cr = ChecklistNoteActivity.this.getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, alarmtime.getTimeInMillis());
                    values.put(CalendarContract.Events.DTEND, alarmtime.getTimeInMillis() + 60 * 60 * 1000);
                    values.put(CalendarContract.Events.TITLE, etName.getText().toString());
                    values.put(CalendarContract.Events.DESCRIPTION, etName.getText().toString());
                    values.put(CalendarContract.Events.EVENT_LOCATION, "");
                    values.put(CalendarContract.Events.HAS_ALARM, 1);
                    values.put(CalendarContract.Events.CALENDAR_ID, 1);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                            .getTimeZone().getID());
                    System.out.println(Calendar.getInstance().getTimeZone().getID());
                    Log.e("calenderId", Calendar.getInstance().getTimeZone().getID());
                    if (ActivityCompat.checkSelfPermission(ChecklistNoteActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                    long eventId = Long.parseLong(uri.getLastPathSegment());
                    Log.e("Event_Id", String.valueOf(eventId));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //create new alarm
                //  hasAlarm = true;
                notification_id = (int) (long) DbAccess.addNotification(getBaseContext(), id, alarmtime.getTimeInMillis(), setRingtoneUri.toString());

                try {
                    Log.e("Ketan_etName", etName.getText().toString());
                    ContentResolver cr = ChecklistNoteActivity.this.getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, alarmtime.getTimeInMillis());
                    values.put(CalendarContract.Events.DTEND, alarmtime.getTimeInMillis() + 60 * 60 * 1000);
                    values.put(CalendarContract.Events.TITLE, etName.getText().toString());
                    values.put(CalendarContract.Events.DESCRIPTION, etName.getText().toString());
                    values.put(CalendarContract.Events.EVENT_LOCATION, "");
                    values.put(CalendarContract.Events.HAS_ALARM, 1);
                    values.put(CalendarContract.Events.CALENDAR_ID, 1);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                            .getTimeZone().getID());
                    System.out.println(Calendar.getInstance().getTimeZone().getID());
                    if (ActivityCompat.checkSelfPermission(ChecklistNoteActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                    long eventId = Long.parseLong(uri.getLastPathSegment());
                    Log.e("Ketan_Event_Id", String.valueOf(eventId));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            Toast.makeText(getApplicationContext(), R.string.toast_updated, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNote() {
        Adapter a = lvItemList.getAdapter();
        Adapter b = completeItemList.getAdapter();
        JSONArray jsonArray = new JSONArray();
        try {
            CheckListItem temp;
            for (int i = 0; i < itemNamesList.size(); i++) {
                temp = (CheckListItem) a.getItem(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", temp.getName());
                jsonObject.put("checked", temp.isChecked());
                jsonArray.put(jsonObject);
            }
            for (int i = 0; i < completeItemNamesList.size(); i++) {
                temp = (CheckListItem) b.getItem(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", temp.getName());
                jsonObject.put("checked", temp.isChecked());
                jsonArray.put(jsonObject);
            }
            fillNameIfEmpty();
            id = DbAccess.addNote(getBaseContext(), etName.getText().toString(), jsonArray.toString(), DbContract.NoteEntry.TYPE_CHECKLIST,
                    colorID, dbPriority);
            Toast.makeText(getApplicationContext(), R.string.toast_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        new AlertDialog.Builder(ChecklistNoteActivity.this)
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
                        startActivity(new Intent(ChecklistNoteActivity.this, ManageCategoriesActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

            Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(ChecklistNoteActivity.this, RingtoneManager.TYPE_ALARM);
            final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
            startActivityForResult(intent, REQUEST_CODE_RINGTONE);
        }
    }

    private void displayTrashDialog() {
        SharedPreferences sp = getSharedPreferences(Preferences.SP_DATA, Context.MODE_PRIVATE);
        if (sp.getBoolean(Preferences.SP_DATA_DISPLAY_TRASH_MESSAGE, true)) {
            //we never displayed the message before, so show it now
            new AlertDialog.Builder(ChecklistNoteActivity.this)
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        this.monthOfYear = monthOfYear;
        this.year = year;
        final Calendar c = Calendar.getInstance();
        if (hasAlarm) {
            c.setTimeInMillis(notificationCursor.getLong(notificationCursor.getColumnIndexOrThrow(DbContract.NotificationEntry.COLUMN_TIME)));
        }
        TimePickerDialog tpd = new TimePickerDialog(ChecklistNoteActivity.this, this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        tpd.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarmtime = Calendar.getInstance();
        alarmtime.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

        if (hasAlarm) {
            //Update the current alarm
            DbAccess.updateNotificationTime(getBaseContext(), notification_id, alarmtime.getTimeInMillis(), "");
        } else {
            //create new alarm
            //    notification_id = (int) (long) DbAccess.addNotification(getBaseContext(), id, alarmtime.getTimeInMillis(),setRingtoneUri.toString());
        }
        //Store a reference for the notification in the database. This is later used by the service.

        //Create the intent that is fired by AlarmManager
//        Intent i = new Intent(this, NotificationService.class);
//        i.putExtra(NotificationService.NOTIFICATION_ID, notification_id);
//
//        PendingIntent pi = PendingIntent.getService(this, notification_id, i, PendingIntent.FLAG_UPDATE_CURRENT);

        int i1 = r.nextInt(80 - 65) + 65;
        final Intent notifIntent = new Intent(ChecklistNoteActivity.this, AlarmLandingPageActivity.class);
        notifIntent.putExtra("uri", setRingtoneUri.toString());
        notifIntent.putExtra("name", etName.getText().toString());
        notifIntent.putExtra("desc", "");
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pIntent = PendingIntent.getActivity(
                ChecklistNoteActivity.this, i1, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );

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
            DatePickerDialog dpd = new DatePickerDialog(ChecklistNoteActivity.this, this, year, month, day);
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
            File file = new File(path, "/checklist_" + etName.getText().toString() + ".txt");
            try {
                // Make sure the directory exists.
                boolean path_exists = path.exists() || path.mkdirs();
                if (path_exists) {
                    PrintWriter out = new PrintWriter(file);
                    out.println(etName.getText().toString());
                    out.println();
                    out.println(getContentString());
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
            sendIntent.putExtra(Intent.EXTRA_TEXT, etName.getText().toString() + "\n\n" + getContentString());
            mShareActionProvider.setShareIntent(sendIntent);
        }
    }

    private String getContentString() {
        StringBuilder content = new StringBuilder();
        Adapter a = lvItemList.getAdapter();
        CheckListItem temp;
        for (int i = 0; i < itemNamesList.size(); i++) {
            temp = (CheckListItem) a.getItem(i);
            content.append("- " + temp.getName() + " [" + (temp.isChecked() ? "✓" : "   ") + "]\n");
        }
        return content.toString();
    }

    //Click on a listitem
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter a = (ArrayAdapter) lvItemList.getAdapter();
        CheckListItem temp = (CheckListItem) a.getItem(position);
        temp.setChecked(!temp.isChecked());
        a.notifyDataSetChanged();
        updateList();
        loadActivity(true);


    }

    private void deleteSelectedItems() {
        ArrayAdapter adapter = (ArrayAdapter) lvItemList.getAdapter();
        SparseBooleanArray checkedItemPositions = lvItemList.getCheckedItemPositions();
        ArrayList<CheckListItem> temp = new ArrayList<>();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if (checkedItemPositions.valueAt(i)) {
                temp.add((CheckListItem) adapter.getItem(checkedItemPositions.keyAt(i)));
            }
        }
        if (temp.size() > 0) {
            itemNamesList.removeAll(temp);
        }
    }

    private void updateSelectedItems() {

        ArrayAdapter adapter = (ArrayAdapter) lvItemList.getAdapter();
        SparseBooleanArray checkedItemPositions = lvItemList.getCheckedItemPositions();
        ArrayList<CheckListItem> temp = new ArrayList<>();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            Log.e("valueofCheckList", checkedItemPositions + "");
            Log.e("CheckListValueAt", checkedItemPositions.valueAt(i) + "");
            if (checkedItemPositions.valueAt(i)) {
                temp.add((CheckListItem) adapter.getItem(checkedItemPositions.keyAt(i)));
                Log.e("keyAt", temp.get(i).getName());
                etNewItem.setText(temp.get(i).getName());
            }
        }
        if (temp.size() > 0) {
            itemNamesList.removeAll(temp);


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

        Log.e("uri", ringToneuri + "");
        Ringtone r = RingtoneManager.getRingtone(this, ringToneuri);
        String ringToneName = r.getTitle(this);
        RingTone.setText(ringToneName);

        DbAccess.updateNotificationRing(getBaseContext(), notification_id, setRingtoneUri.toString());

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // Toast.makeText(getApplicationContext(),priority[position] ,Toast.LENGTH_LONG).show();

        dbPriority = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
