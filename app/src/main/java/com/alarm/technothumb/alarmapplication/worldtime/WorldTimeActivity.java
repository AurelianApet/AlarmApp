package com.alarm.technothumb.alarmapplication.worldtime;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.alarm.technothumb.alarmapplication.R;

import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class WorldTimeActivity extends AppCompatActivity {

    //    private static final String TAG = getaclass.getName();
    //
    //Intent extras map keys
    //IN - sent to edit dialog
    //OUT - received in onActivityResult
    //
    public static final String INTENT_TZ_DISPLAYNAME_IN = "INTENT_TZ_DISPLAYNAME_IN";
    public static final String INTENT_TZ_ID_IN = "INTENT_TZ_ID_IN";
    public static final String INTENT_TZ_DISPLAYNAME_OUT = "INTENT_TZ_DISPLAYNAME_OUT";
    public static final String INTENT_TZ_ID_OUT = "INTENT_TZ_ID_OUT";

    //Request codes for intent
    private static final int REQ_CODE_ADD_ZONE = 0;
    private static final int REQ_CODE_EDIT_ZONE = 1;

    private WorldClockData data;
    private TimeZoneListAdapter adapter;

    ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_time);


        //init data
        data = new WorldClockData(getApplicationContext());


        // register to get context event to edit/delete
        mainListView = (ListView)findViewById(R.id.main_list_view);
        registerForContextMenu(mainListView);

        int listSize = refreshListView();

        Button mainAddButton = (Button)findViewById(R.id.main_button_add);
        mainAddButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                invokeAddZoneActivity();
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timezone_edit, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

//        ListView mainListView = (ListView)findViewById(R.id.main_list_view);
        WorldClockTimeZone selectedTimeZone = (WorldClockTimeZone) mainListView.getAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case R.id.menu_edit:
                // edit
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                editIntent.putExtra(INTENT_TZ_ID_IN, selectedTimeZone.getId());
                editIntent.putExtra(INTENT_TZ_DISPLAYNAME_IN, selectedTimeZone.getDisplayName());
                editIntent.setComponent(new ComponentName(WorldTimeActivity.this, TimeZoneEdit.class));
                startActivityForResult(editIntent, REQ_CODE_EDIT_ZONE);
//                WorldTimeActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            case R.id.menu_delete:
                // delete
                data.deleteZone(selectedTimeZone);
                refreshListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentReceived) {
        Log.d(TAG, "activity requestcode="+requestCode+" resultCode="+resultCode+ "data="+intentReceived);
        //process response from add/edit activity
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case REQ_CODE_ADD_ZONE:
                    Log.d(TAG, "Add zone id"+intentReceived.getStringExtra(INTENT_TZ_ID_OUT)+ " name="+intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT));
                    data.addZone(new WorldClockTimeZone(TimeZone.getTimeZone(intentReceived.getStringExtra(INTENT_TZ_ID_OUT)),intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT)));
                    break;
                case REQ_CODE_EDIT_ZONE:
                    Log.d(TAG, "EDIT - Remove zone id"+intentReceived.getStringExtra(INTENT_TZ_ID_IN)+ " name="+intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_IN));
                    Log.d(TAG, "EDIT - Add zone id"+intentReceived.getStringExtra(INTENT_TZ_ID_OUT)+ " name="+intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT));
                    data.deleteZone(new WorldClockTimeZone(TimeZone.getTimeZone(intentReceived.getStringExtra(INTENT_TZ_ID_IN))));
                    data.addZone(new WorldClockTimeZone(TimeZone.getTimeZone(intentReceived.getStringExtra(INTENT_TZ_ID_OUT)),intentReceived.getStringExtra(INTENT_TZ_DISPLAYNAME_OUT)));
                    break;
                default:
                    throw new WorldClockException("Unsupported request code!");
            }
        }

        //refresh data
        refreshListView();
        super.onActivityResult(requestCode, resultCode, intentReceived);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//    }
//    /**
//     * Present options menu to add timezones
//     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle menu item selects
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                // add a new zone
                invokeAddZoneActivity();
                return true;
//            case R.id.menu_about:
//                //about menu
//                showAboutDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void showAboutDialog() {
//        SpannableString aboutMessage = new SpannableString(getText(R.string.about_message));
//        Linkify.addLinks(aboutMessage, Linkify.WEB_URLS);
//
//        AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                .setPositiveButton(android.R.string.ok, null)
//                .setTitle(R.string.about_title)
//                .setCancelable(true)
//                .setMessage(aboutMessage)
//                .create();
//
//        dialog.show();
//
//        // Make the textview clickable. Must be called after show()
//        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
//    }

    private void invokeAddZoneActivity() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setComponent(new ComponentName(WorldTimeActivity.this, TimeZoneEdit.class));
        startActivityForResult(intent, REQ_CODE_ADD_ZONE);
    }

    /**
     * Refresh list of timezones
     * @return number of items in list after refresh
     */
    private int refreshListView() {
        WorldClockTimeZone[] values = data.getSavedTimeZones().toArray(
                new WorldClockTimeZone[] {});

        Log.d(TAG, "Loaded data size for refresh:" + values.length);

        adapter = new TimeZoneListAdapter(WorldTimeActivity.this, values);

//        ListView mainListView = (ListView)findViewById(R.id.main_list_view);
        mainListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return values.length;
    }}
