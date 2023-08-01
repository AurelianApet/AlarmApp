package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.travel_record.entities.MyLocationManager;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Constants;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Util;

public class Map_mainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;
    Cursor cursor;

    private Activity mActivity;
    private Context mContext;
    private static FragmentManager mFragmentManager;
    private MyLocationManager mLocationManager;

//    Button place_picker;
//    TextView txt_adrs;
//
//    public static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);
        setTitle("Travel Record");

        mActivity = this;
        mContext = getApplicationContext();
        mFragmentManager = getSupportFragmentManager();
        Util.initGPSManager(mContext, mActivity);

      //  cursorAdapter = new NotesCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        registerForContextMenu(list);

        getLoaderManager().initLoader(0, null, this);

    }
//    public void openEditorForNewNote(View view) {
//        Intent i = new Intent(Map_mainActivity.this,MapActivity.class);
//        startActivity(i);
//        Map_mainActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
////        finish();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Util.onRequestPermissionsResult(mActivity, mContext, requestCode, grantResults);
        if (Util.isPermissionsGranted(Constants.LOCATION_PERMISSION_CODES, mContext)) {

        } else {
            mActivity.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mLocationManager == null) {
            Util.initGPSManager(mContext, mActivity);
            mLocationManager = Util.getLocationManager();
        }

        if (mLocationManager.checkLocationServicesStatus()) {
            if (Util.checkPermissions(mActivity, Constants.LOCATION_PERMISSIONS_FLAG,
                    Constants.LOCATION_PERMISSION_CODES, Constants.LOCATION_PERMISSIONS_CODE, null)) {
                mLocationManager.startLocationUpdatesByPrecisionStatus();

            }
            // TODO run more exploration tests: what to do if it returns false?
        } else {
            mLocationManager.openEnableLocationServicesDialog(mActivity);
        }
        restartLoader();
    }

    @Override
    public void onCreateContextMenu (final ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //final restrantdata student = (restrantdata)
        //      ResturantList.getItemAtPosition(info.position);
        MenuItem EditMenu = menu.add("Edit");


        MenuItem ViewLocMenu = menu.add("View Location");


        MenuItem delete = menu.add("Delete");


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit") {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Intent intentEdit = new Intent(Map_mainActivity.this, CameraActivity.class);
            Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + info.id);
            intentEdit.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);

            startActivity(intentEdit);
//            Map_mainActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//            finish();
//
        }
        else if (item.getTitle() == "View Location") {



            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Intent locationViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
            Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + info.id);
            locationViewIntent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);

//
            //Toast.makeText(this,uri.toString(), Toast.LENGTH_SHORT).show();
            startActivity(locationViewIntent);
//            Map_mainActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//            finish();
            // restartLoader();

        }
        else if (item.getTitle() == "Delete") {
            // TODO Delete action

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + info.id);
            String noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            getContentResolver().delete(NotesProvider.CONTENT_URI,
                    noteFilter, null);
            Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
            restartLoader();
        }
        else {
            return false;
        }
        return true;
    }

    private void startActivityForResult(Map_mainActivity mainActivity, Class<CameraActivity> editorActivityClass, int editorRequestCode) {
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_travel_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_delete_all:
                deleteAllNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(
                                    NotesProvider.CONTENT_URI, null, null
                            );
                            restartLoader();

                            Toast.makeText(Map_mainActivity.this, getString(R.string.all_deleted), Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }



    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }



}


