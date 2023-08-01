package com.alarm.technothumb.alarmapplication.anniversaries;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.alarm.technothumb.alarmapplication.HomeActivity;
import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.note.CheckListItem;
import com.alarm.technothumb.alarmapplication.note.DbAccess;
import com.alarm.technothumb.alarmapplication.note.DbContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Anniversary extends AppCompatActivity  {

    public static ListView arriversaryList;
    FloatingActionButton fabButton;
    public static List<anniversaryModel> data;
    public static dbHelper db;
    public static anniversaryModel getSet;
    public static List<Model> personIDList = new ArrayList<Model>();
    public static AnniversaryAdapter adaptor;
    Toolbar toolbar_aboutus;
    Button btnNotification;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anniversary);

        toolbar_aboutus = (Toolbar) findViewById(R.id.anniversaryMain); // get the reference of Toolbar
        setSupportActionBar(toolbar_aboutus);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
      //  id = intent.getIntExtra("Anni", -1);


        arriversaryList = (ListView) findViewById(R.id.anniversary);
        arriversaryList.setTextFilterEnabled(true);
        fabButton = (FloatingActionButton) findViewById(R.id.floating_action_button);


        adaptor = new AnniversaryAdapter(Anniversary.this, personIDList);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Anniversary.this, addAnniversary.class);
                startActivity(intent);

            }
        });
      //  personIDList.clear();








    //    getUserData();

    }



    private void getUserData() {
        personIDList.clear();

        db = new dbHelper(Anniversary.this);
        data = db.getData();
        for (int a = 0; a < data.size(); a++) {

            getSet = data.get(a);

             String Title = getSet.getTitle();
            String Category = getSet.getCategory();
            String date = getSet.getDate();
            int TitleId = getSet.getTitleID();
            int Anni = getSet.getAnniID();
        //   Log.e("TitleId", TitleId);
        /*    Log.e("Category", Category);
            Log.e("date", date);*/
            Model getSet1 = new Model();
            getSet1.setTitle(Category);
            getSet1.setCategory(Title);
            getSet1.setDate(date);
            getSet1.setTitleID(TitleId);
            getSet1.setAnniID(Anni);
            personIDList.add(getSet1);

            Log.e("resume", "call");
            arriversaryList.setAdapter(adaptor);
            adaptor.notifyDataSetChanged();

        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anniversary_nemu, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent1 = new Intent(Anniversary.this, HomeActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            finish();
        }else if(item.getItemId() == R.id.action_anniversary) {

            adaptor.getFilter().filter("Anniversary");
        }else if(item.getItemId() == R.id.action_Birthday) {

            adaptor.getFilter().filter("Birthday");
        }else if(item.getItemId() == R.id.action_weddingDay) {

            adaptor.getFilter().filter("Wedding Day");
        }else if(item.getItemId() == R.id.action_Departure) {

            adaptor.getFilter().filter("Day of Departure");
        }else if(item.getItemId() == R.id.action_Holiday) {

            adaptor.getFilter().filter("Public Holiday");
        }else if(item.getItemId() == R.id.action_Other) {

            adaptor.getFilter().filter("Others");
        }else if(item.getItemId() == R.id.action_Date) {
            updateListByDate();
            adaptor.notifyDataSetChanged();
        }
        else if(item.getItemId() == R.id.action_All) {

            adaptor.getFilter().filter("");
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateListByDate() {

      /*  arriversaryList = (ListView) findViewById(R.id.anniversary);
        AnniversaryAdapter adapter = (AnniversaryAdapter) arriversaryList.getAdapter();

            String selection = dbHelper.tableID + " = ?";
            String[] selectionArgs = { "0" };
         //   adapter.changeCursor(dbHelper.getCursorAllAnniversarybyDate(getBaseContext(), selection, selectionArgs));*/

        Collections.sort(personIDList, new CustomComparator <Model>() {




        });

    }




    private class CustomComparator<T> implements Comparator< Model> {

        @Override
        public int compare(Model model, Model t1) {
            String myFormat = "dd-MM-yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(model.getDate());
               d2 = sdf.parse(t1.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return d1.compareTo(d2);

        }
    }

    @Override
    protected void onResume() {
         super.onResume();
        getUserData();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(Anniversary.this, HomeActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
    }
}
