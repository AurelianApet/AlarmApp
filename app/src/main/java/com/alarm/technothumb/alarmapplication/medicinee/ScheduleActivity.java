package com.alarm.technothumb.alarmapplication.medicinee;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.medicinee.model.Alarm;
import com.alarm.technothumb.alarmapplication.medicinee.model.PillBox;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Week at a Glance");

        TableLayout stk = (TableLayout) findViewById(R.id.table_calendar);

        PillBox pillBox = new PillBox();

        List<Alarm> alarms = null;

        List<String> days = Arrays.asList("Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday", "Saturday");

        int color = getResources().getColor(R.color.primary_light);

        for (int i = 1; i < 8; i++) {

            String day = days.get(i-1);
            TableRow headerRow = new TableRow(this);
            TextView headerText = new TextView(this);

            headerText.setText(day);
            headerText.setTextColor(Color.WHITE);
            headerText.setPadding(30, 10, 0, 10);
            headerText.setTypeface(null, Typeface.BOLD);
            headerText.setGravity(Gravity.CENTER);

            headerRow.addView(headerText);
            headerRow.setBackgroundColor(color);
            stk.addView(headerRow);

            //Let headerText span two columns
            TableRow.LayoutParams params = (TableRow.LayoutParams)headerText.getLayoutParams();
            params.span = 2;
            headerText.setLayoutParams(params);

            try {
                alarms = pillBox.getAlarms(this, i);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            if(alarms.size() != 0) {
                for(Alarm alarm: alarms) {
                    TableRow tbrow = new TableRow(this);

                    TextView t1v = new TextView(this);
                    t1v.setText(alarm.getPillName());
                    t1v.setMaxEms(6);
                    t1v.setGravity(Gravity.CENTER);
                    t1v.setTextColor(Color.WHITE);
                    t1v.setPadding(0,10,0,10);
                    tbrow.addView(t1v);

                    TextView t2v = new TextView(this);

                    String time = alarm.getStringTime();
                    t2v.setText(time);
                    t2v.setGravity(Gravity.CENTER);
                    t2v.setTextColor(Color.WHITE);
                    t1v.setPadding(0,10,0,10);
                    tbrow.addView(t2v);

                    stk.addView(tbrow);
                }
            } else {
                TableRow tbrow = new TableRow(this);
                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                tv.setText("You don't have any alarms for " + day + ".");
                tv.setTextColor(Color.WHITE);
                tv.setPadding(0,10,0,10);

                tbrow.addView(tv);
                stk.addView(tbrow);

                //Let tv span two columns
                TableRow.LayoutParams params2 = (TableRow.LayoutParams)tv.getLayoutParams();
                params2.span = 2;
                tv.setLayoutParams(params2);
            }
        }
    }


    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_medi_schedule, menu);
        return true;
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent returnHome = new Intent(getBaseContext(), MedicineActivity.class);
        startActivity(returnHome);
        finish();
//        ScheduleActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnHome = new Intent(getBaseContext(), MedicineActivity.class);
        startActivity(returnHome);
        finish();
//        ScheduleActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
