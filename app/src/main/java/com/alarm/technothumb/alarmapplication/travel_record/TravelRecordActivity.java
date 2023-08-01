package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.alarm.technothumb.alarmapplication.HomeActivity;
import com.alarm.technothumb.alarmapplication.R;

public class TravelRecordActivity extends AppCompatActivity  {



    Cursor cursor;
    TravelRecordPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_record);
        setTitle("Travel Record");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
         adapter = new TravelRecordPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_travel_shorting, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement

        int id = item.getItemId();

        if (id == R.id.action_sort_Title) {
            //switch to an alphabetically sorted cursor.
            updateListByaddress("title");

            return true;
        } else if (id == R.id.action_sort_city) {
            //switch to an alphabetically sorted cursor.
            updateListByaddress("city");
            return true;
        } else if (id == R.id.action_sort_state) {
            //switch to an alphabetically sorted cursor.
            updateListByaddress("state");
            return true;
        } else if (id == R.id.action_sort_Country) {
            //switch to an alphabetically sorted cursor.
            updateListByaddress("country");
            return true;
        } else if (id == R.id.action_sort_Search) {
            //switch to an alphabetically sorted cursor.
            updateListBySearch("Search");
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void updateListBySearch(String search) {

        Map_mainFragment fragment = (Map_mainFragment) adapter.getItem(0);
        fragment.Search(this);
        Log.e("call", "search");
    }


    private void updateListByaddress(String sortString) {


        Map_mainFragment fragment = (Map_mainFragment) adapter.getItem(0);
        fragment.updateList(sortString);

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(TravelRecordActivity.this, HomeActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
    }




}
