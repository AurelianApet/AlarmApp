package com.alarm.technothumb.alarmapplication.medicinee;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.medicinee.adapter.TabsAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MedicineActivity extends AppCompatActivity implements android.support.v7.app.ActionBar.TabListener{
    private ViewPager tabsviewPager;
    private TabsAdapter mTabsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());

        tabsviewPager.setAdapter(mTabsAdapter);

        getSupportActionBar().setTitle("Medication");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        String todayString = new SimpleDateFormat("EEE, MMM d").format(today);
        String tomorrowString = new SimpleDateFormat("EEE, MMM d").format(tomorrow);

        ActionBar.Tab historytab = getSupportActionBar().newTab().setTabListener(this);
        ActionBar.Tab todaytab = getSupportActionBar().newTab().setTabListener(this);
        ActionBar.Tab tomorrowtab = getSupportActionBar().newTab().setTabListener(this);

        TextView tt1 = new TextView(this);
        tt1.setText(Html.fromHtml("<b>HISTORY</b>"));
        tt1.setTextColor(Color.WHITE);
        tt1.setGravity(Gravity.CENTER);
        tt1.setHeight(200);
        historytab.setCustomView(tt1);

        TextView tt2 = new TextView(this);
        tt2.setText(Html.fromHtml("<b>TODAY</b><br><small>" + todayString + "</small>"));
        tt2.setTextColor(Color.WHITE);
        tt2.setGravity(Gravity.CENTER);
        tt2.setHeight(200);
        todaytab.setCustomView(tt2);

        TextView tt3 = new TextView(this);
        tt3.setText(Html.fromHtml("<b>TOMORROW</b><br><small>" + tomorrowString + "</small>"));
        tt3.setTextColor(Color.WHITE);
        tt3.setGravity(Gravity.CENTER);
        tt3.setHeight(200);
        tomorrowtab.setCustomView(tt3);

        getSupportActionBar().addTab(historytab);
        getSupportActionBar().addTab(todaytab);
        getSupportActionBar().addTab(tomorrowtab);

        getSupportActionBar().setSelectedNavigationItem(1);

        /** This helps in providing swiping effect for v7 compat library */
        tabsviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }


    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_medi_main, menu);
        return true;
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            sendSetting();
            return true;
        }

        if (id == R.id.action_add) {
            sendAdd();
            return true;
        }

        if (id == R.id.action_edit) {
            sendEdit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab selectedtab, FragmentTransaction arg1) {
        /** Update tab position on tap */
        tabsviewPager.setCurrentItem(selectedtab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {

    }

    public void sendAdd() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
        finish();
//        MedicineActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void sendSetting() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
        finish();
//        MedicineActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void sendEdit() {
        Intent intent = new Intent(this, PillBoxActivity.class);
        startActivity(intent);
        finish();
//        MedicineActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

}
