package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.note.DbAccess;
import com.alarm.technothumb.alarmapplication.note.DbContract;
import com.alarm.technothumb.alarmapplication.travel_record.entities.MyLocationManager;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Constants;
import com.alarm.technothumb.alarmapplication.travel_record.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by NIKUNJ on 12-02-2018.
 */

public class Map_mainFragment extends Fragment

{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;
    Cursor cursor;
    public static String check = "title";
    public static Activity mActivity;
    public static Context mContext;
    private static FragmentManager mFragmentManager;
    private MyLocationManager mLocationManager;
    public static ListView list;
    DBOpenHelper db;
    public static List<model> data;
    public static List<model> personIDList = new ArrayList<model>();
    public static NotesCursorAdapter adapter;
  //  SharedPreferences SM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_map_main, container, false);

        setHasOptionsMenu(true);
//        getActivity().setTitle("Travel Record");
//         latitude = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Latitude));
//         longitude = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Longitude));

        mActivity = getActivity();
        mContext = getActivity().getApplicationContext();
        mFragmentManager = getActivity().getSupportFragmentManager();
        Util.initGPSManager(mContext, mActivity);
        list = (ListView) v.findViewById(android.R.id.list);


        db = new DBOpenHelper(getActivity());
        data = db.getData();

        adapter = new NotesCursorAdapter(getActivity(), data);
        list.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_map_add_options);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i_camera = new Intent(getActivity(), CameraActivity.class);

                startActivity(i_camera);


            }
        });

        FloatingActionButton fabAudio = (FloatingActionButton) v.findViewById(R.id.fab_map_add_Audio);
        fabAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i_camera = new Intent(getActivity(), AudioNote_RecordActivity.class);

                startActivity(i_camera);


            }
        });


        return v;
    }




    public void updateList(String s) {
        if (s.equalsIgnoreCase("title")) {
            adapter.getFilter().filter("");
            Collections.sort(data, new Comparator<model>() {
                public int compare(model obj1, model obj2) {
                    // ## Ascending order
                    return obj1.noteText.compareToIgnoreCase(obj2.noteText); // To compare string values

                }
            });
        }
        else if (s.equalsIgnoreCase("city"))
        {
            adapter.getFilter().filter("");
            Collections.sort(data, new Comparator<model>() {
            public int compare(model obj1, model obj2) {
                // ## Ascending order
                return obj1.city.compareToIgnoreCase(obj2.city); // To compare string values

            }
        });

        }else if (s.equalsIgnoreCase("state"))
        {
            adapter.getFilter().filter("");
            Collections.sort(data, new Comparator<model>() {
                public int compare(model obj1, model obj2) {
                    // ## Ascending order
                    return obj1.state.compareToIgnoreCase(obj2.state); // To compare string values

                }
            });

        }else if (s.equalsIgnoreCase("country"))
        {
            adapter.getFilter().filter("");
            Collections.sort(data, new Comparator<model>() {
                public int compare(model obj1, model obj2) {
                    // ## Ascending order
                    return obj1.country.compareToIgnoreCase(obj2.country); // To compare string values

                }
            });

        }


        adapter.notifyDataSetChanged();
    }



    public void Search(TravelRecordActivity search) {
        Log.e("call", "search");
        final Dialog dialog = new Dialog(search);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final CheckBox chk_Title, chk_City, chk_State, chk_County;
        final EditText simpleSearchView;
        Button btn_cancel, btn_ok;


        chk_City = dialog.findViewById(R.id.chk_category);
        chk_State = dialog.findViewById(R.id.chk_shop);
        chk_County = dialog.findViewById(R.id.chk_date);
        chk_Title  = dialog.findViewById(R.id.chk_name);
        simpleSearchView = dialog.findViewById(R.id.searchCity);

      /*  SM = getActivity().getSharedPreferences("userrecord", 0);

        String textView =  SM.getString("filterText", null);
        simpleSearchView.setText(textView);*/

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                dialog.dismiss();
                // Prevent dialog close on back press button
                return keyCode == KeyEvent.KEYCODE_BACK;

            }
        });
        simpleSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                adapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //      setupSearchView();

        //  et_pattern_name = dialog.findViewById(R.id.et_pattern_name);


        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_ok = dialog.findViewById(R.id.btn_ok);

        if(check.equals("title") ){
            chk_Title.setChecked(true);
            chk_City.setChecked(false);
            chk_State.setChecked(false);
            chk_County.setChecked(false);
        }else  if(check.equals("city") ){
            chk_City.setChecked(true);
            chk_Title.setChecked(false);
            chk_State.setChecked(false);
            chk_County.setChecked(false);
        }else  if(check.equals("state") ){
            chk_State.setChecked(true);
            chk_County.setChecked(false);
            chk_City.setChecked(false);
            chk_Title.setChecked(false);
        }else  if(check.equals("country") ){
            chk_County.setChecked(true);
            chk_Title.setChecked(false);
            chk_City.setChecked(false);
            chk_State.setChecked(false);
        }


        chk_Title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (chk_Title.isChecked()) {
                    chk_City.setChecked(false);
                    chk_State.setChecked(false);
                    chk_County.setChecked(false);
                    check = "title";
                    simpleSearchView.setVisibility(View.VISIBLE);

                }else {
                    adapter.getFilter().filter("");
                    simpleSearchView.setText("");
                }
            }
        });

        chk_City.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (chk_City.isChecked()) {
                    chk_Title.setChecked(false);
                    chk_State.setChecked(false);
                    chk_County.setChecked(false);
                    check = "city";
                    simpleSearchView.setVisibility(View.VISIBLE);

                }else {
                    adapter.getFilter().filter("");
                    simpleSearchView.setText("");
                }
            }
        });

        chk_State.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (chk_State.isChecked()) {
                    chk_Title.setChecked(false);
                    chk_City.setChecked(false);
                    chk_County.setChecked(false);
                    check = "state";
                    simpleSearchView.setVisibility(View.VISIBLE);


                }else {
                    adapter.getFilter().filter("");
                    simpleSearchView.setText("");
                }
            }
        });

        chk_County.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                                    if (chk_County.isChecked()) {
                                                        chk_Title.setChecked(false);
                                                        chk_City.setChecked(false);
                                                        chk_State.setChecked(false);
                                                        check = "country";
                                                        simpleSearchView.setVisibility(View.VISIBLE);


                                                    }else {
                                                        adapter.getFilter().filter("");
                                                        simpleSearchView.setText("");
                                                    }
                                                }
                                            }


        );


        btn_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                dialog.dismiss();
                adapter.getFilter().filter("");
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String filterText = simpleSearchView.getText().toString();



               /* SharedPreferences.Editor edit = SM.edit();
                edit.putString("filterText", filterText);
                edit.commit();*/

                adapter.getFilter().filter(filterText);

                dialog.dismiss();
            }
        });

        dialog.show();


    }
}
