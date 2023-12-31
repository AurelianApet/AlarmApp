package com.alarm.technothumb.alarmapplication.note;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class NotesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int CAT_ALL = -1;
    private static final String TAG_WELCOME_DIALOG = "welcome_dialog";
    FloatingActionsMenu fabMenu;
    CardView card_view;
    private int selectedCategory = CAT_ALL; //ID of the currently selected category. Defaults to "all"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the OnClickListeners
        findViewById(R.id.fab_text).setOnClickListener(this);
        findViewById(R.id.fab_checklist).setOnClickListener(this);
        findViewById(R.id.fab_audio).setOnClickListener(this);
//        findViewById(R.id.fab_sketch).setOnClickListener(this);

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Cursor crserFirst = DbAccess.getCursorAllNotes(getBaseContext());
        if (crserFirst.getCount() > 0) {
            if (crserFirst.moveToFirst()) {
                do {
                    String colorId = crserFirst.getString(crserFirst.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_COLOR));
                    String cccid = crserFirst.getString(crserFirst.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID));
                    int newCID = Math.abs(Integer.parseInt(colorId));
                    DbAccess.updateNoteColor(NotesActivity.this, Integer.parseInt(cccid), "" + newCID);
                } while (crserFirst.moveToNext());

            }
        }

        //Fill the list from database
        ListView notesList = (ListView) findViewById(R.id.notes_list);
        notesList.setAdapter(new CursorAdapter(getApplicationContext(), DbAccess.getCursorAllNotes(getBaseContext()), CursorAdapter.FLAG_AUTO_REQUERY) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.item_notes, null);

                TextView text = (TextView) rowView.findViewById(R.id.item_name);
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_NAME));
                if (name.length() >= 30) {
                    text.setText(name.substring(0, 27) + "...");
                } else {
                    text.setText(name);
                }

                ImageView iv = (ImageView) rowView.findViewById(R.id.item_icon);
                switch (cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_TYPE))) {
//                    case DbContract.NoteEntry.TYPE_SKETCH:
//                        iv.setImageResource(R.drawable.ic_photo_black_24dp);
//                        break;
                    case DbContract.NoteEntry.TYPE_AUDIO:
                        iv.setImageResource(R.drawable.note_record_color);
                        text.setTextColor(Color.parseColor("#263238"));
                        break;
                    case DbContract.NoteEntry.TYPE_TEXT:
                        iv.setImageResource(R.drawable.note_text_color);
                        text.setTextColor(Color.parseColor("#263238"));
                        break;
                    case DbContract.NoteEntry.TYPE_CHECKLIST:
                        iv.setImageResource(R.drawable.note_checklist_color);
                        text.setTextColor(Color.parseColor("#263238"));
                        break;
                    default:
                }
                return rowView;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView text = (TextView) view.findViewById(R.id.item_name);
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_NAME));
                if (name.length() >= 30) {
                    text.setText(name.substring(0, 27) + "...");
                } else {
                    text.setText(name);
                }

                card_view = (CardView) view.findViewById(R.id.card_view);

                String colorId = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_COLOR));
                if (colorId.equals("1")) {

                    card_view.setBackgroundResource(R.color.scrollRed);
                } else if (colorId.equals("2")) {
                    card_view.setBackgroundResource(R.color.scrollOrange);
                } else if (colorId.equals("3")) {
                    card_view.setBackgroundResource(R.color.scrollYellow);
                } else if (colorId.equals("4")) {
                    card_view.setBackgroundResource(R.color.scrollGreen);
                } else if (colorId.equals("5")) {
                    card_view.setBackgroundResource(R.color.scrollBlue);
                } else if (colorId.equals("6")) {
                    card_view.setBackgroundResource(R.color.scrollVilote);
                } else if (colorId.equals("7")) {
                    card_view.setBackgroundResource(R.color.scrollPint);
                } else if (colorId.equals("8")) {
                    card_view.setBackgroundResource(R.color.scrollGray);
                } else if (colorId.equals("9")) {
                    card_view.setBackgroundResource(R.color.scrollWhite);
                } else {
                    card_view.setBackgroundResource(R.color.primary);
                }


                ImageView iv = (ImageView) view.findViewById(R.id.item_icon);
                switch (cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_TYPE))) {
//                    case DbContract.NoteEntry.TYPE_SKETCH:
//                        iv.setImageResource(R.drawable.ic_photo_black_24dp);
//                        break;
                    case DbContract.NoteEntry.TYPE_AUDIO:
                        iv.setImageResource(R.drawable.note_record_color);
                        text.setTextColor(Color.parseColor("#263238"));
                        break;
                    case DbContract.NoteEntry.TYPE_TEXT:
                        iv.setImageResource(R.drawable.note_text_color);
                        text.setTextColor(Color.parseColor("#263238"));
                        break;
                    case DbContract.NoteEntry.TYPE_CHECKLIST:
                        iv.setImageResource(R.drawable.note_checklist_color);
                        text.setTextColor(Color.parseColor("#263238"));
                        break;
                    default:
                }
            }
        });
        notesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get details about the clicked note
                CursorAdapter ca = (CursorAdapter) parent.getAdapter();
                Cursor c = ca.getCursor();
                c.moveToPosition(position);
                //start the appropriate activity
                switch (c.getInt(c.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_TYPE))) {
                    case DbContract.NoteEntry.TYPE_TEXT:
                        Intent i = new Intent(getApplication(), TextNoteActivity.class);
                        i.putExtra(TextNoteActivity.EXTRA_ID, c.getInt(c.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID)));
                        startActivity(i);
//                        NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        break;
                    case DbContract.NoteEntry.TYPE_AUDIO:
                        Intent i2 = new Intent(getApplication(), AudioNoteActivity.class);
                        i2.putExtra(AudioNoteActivity.EXTRA_ID, c.getInt(c.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID)));
                        startActivity(i2);
//                        NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;
//                    case DbContract.NoteEntry.TYPE_SKETCH:
//                        Intent i3 = new Intent(getApplication(), SketchActivity.class);
//                        i3.putExtra(SketchActivity.EXTRA_ID, c.getInt(c.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID)));
//                        startActivity(i3);
//                        break;
                    case DbContract.NoteEntry.TYPE_CHECKLIST:
                        Intent i4 = new Intent(getApplication(), ChecklistNoteActivity.class);
                        i4.putExtra(ChecklistNoteActivity.EXTRA_ID, c.getInt(c.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID)));
                        startActivity(i4);
//                        NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;
                }
            }
        });
        notesList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                //do nothing


                ListView notesList = (ListView) findViewById(R.id.notes_list);
                CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
                // SparseBooleanArray checkedItemPositions = notesList.getCheckedItemPositions();
                //  for (int i = 0; i < checkedItemPositions.size(); i++) {
                Cursor cc = DbAccess.getNote(NotesActivity.this, (int) (adapter.getItemId(position)));
                if (cc.moveToFirst()) {
                    String colorId = cc.getString(cc.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_COLOR));
                    String cccid = cc.getString(cc.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID));
                    if (checked) {
                        int newCID = Math.abs(Integer.parseInt(colorId));
                        DbAccess.updateNoteColor(NotesActivity.this, ((int) (long) (adapter.getItemId(position))), "-" + newCID);
                    } else {
                        int newCID = Math.abs(Integer.parseInt(colorId));
                        DbAccess.updateNoteColor(NotesActivity.this, ((int) (long) (adapter.getItemId(position))), "" + newCID);
                    }
                    //}
                    updateList();
                }


               /* for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        Cursor newCursor = DbAccess.getCursorAllNotes(getBaseContext(), (int) (long) adapter.getItemId(checkedItemPositions.keyAt(i)));
                         String ccd = newCursor.getString(newCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_COLOR));
                         String cid = newCursor.getString(newCursor.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID));
                          Log.e("coldddor", cid  + " <> " + ccd);

                    }
                }*/


            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.delete_notes, menu);
                //Temporary fix, otherwise statusbar would be black
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    // or Color.TRANSPARENT or your preferred color
                }
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
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

                ListView notesList = (ListView) findViewById(R.id.notes_list);
                CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
                SparseBooleanArray checkedItemPositions = notesList.getCheckedItemPositions();
                for (int i = 0; i < checkedItemPositions.size(); i++) {

                    Cursor cc = DbAccess.getNote(NotesActivity.this, (int) (long) adapter.getItemId(checkedItemPositions.keyAt(i)));
                    if (cc.moveToFirst()) {
                        String colorId = cc.getString(cc.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_COLOR));
                        // String cccid = cc.getString(cc.getColumnIndexOrThrow(DbContract.NoteEntry.COLUMN_ID));
                        if (checkedItemPositions.valueAt(i)) {
                            int newCID = Math.abs(Integer.parseInt(colorId));
                            DbAccess.updateNoteColor(NotesActivity.this, ((int) (long) adapter.getItemId(checkedItemPositions.keyAt(i))), "" + newCID);
                        }
                    }
                    ((CursorAdapter) notesList.getAdapter()).notifyDataSetChanged();
                }


                //Temporary fix, otherwise statusbar would be black
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                    // or Color.TRANSPARENT or your preferred color
                }
                updateList();
            }
        });


        PreferenceManager.setDefaultValues(this, R.xml.pref_settings, false);
        SharedPreferences sp = getSharedPreferences(Preferences.SP_DATA, Context.MODE_PRIVATE);
        if (sp.getBoolean(Preferences.SP_DATA_DISPLAY_WELCOME_DIALOG, true)) {
            WelcomeDialog welcomeDialog = new WelcomeDialog();
            welcomeDialog.show(getFragmentManager(), TAG_WELCOME_DIALOG);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Preferences.SP_DATA_DISPLAY_WELCOME_DIALOG, false);
            editor.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
//        buildDrawerMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notess, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement

        int id = item.getItemId();

        if (id == R.id.action_sort_alphabetical) {
            //switch to an alphabetically sorted cursor.
            updateListAlphabetical();
            return true;
        } else if (id == R.id.action_sort_Color) {
            //switch to an alphabetically sorted cursor.
            updateListByColor();
            return true;
        } else if (id == R.id.action_sort_created) {
            //switch to an alphabetically sorted cursor.
            updateListByCreated();
            return true;
        } else if (id == R.id.action_sort_Priority) {
            //switch to an alphabetically sorted cursor.
            updateListByPriority();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setCheckable(true);
        item.setChecked(true);
        int id = item.getItemId();
        if (id == R.id.nav_trash) {
            startActivity(new Intent(getApplication(), RecycleActivity.class));
//            NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (id == R.id.nav_all) {
            selectedCategory = CAT_ALL;
            updateList();
        }
//        else if (id == R.id.nav_manage_categories) {
//            startActivity(new Intent(getApplication(), ManageCategoriesActivity.class));
////                NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//        }
        else {
            selectedCategory = id;
            updateList();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_text:
                startActivity(new Intent(getApplication(), TextNoteActivity.class));
//                NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                fabMenu.collapseImmediately();
                break;
            case R.id.fab_checklist:
                startActivity(new Intent(getApplication(), ChecklistNoteActivity.class));
//                NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                fabMenu.collapseImmediately();
                break;
            case R.id.fab_audio:
                startActivity(new Intent(getApplication(), AudioNoteActivity.class));
//                NotesActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                fabMenu.collapseImmediately();
                break;
//            case R.id.fab_sketch:
//                startActivity(new Intent(getApplication(), SketchActivity.class));
//                fabMenu.collapseImmediately();
//                break;
        }
    }



    private void updateList() {
        ListView notesList = (ListView) findViewById(R.id.notes_list);
        CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
        if (selectedCategory == -1) { //show all
            String selection = DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {"0"};
            adapter.changeCursor(DbAccess.getCursorAllNotes(getBaseContext(), selection, selectionArgs));
        } else {
            String selection = DbContract.NoteEntry.COLUMN_CATEGORY + " = ? AND " + DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {String.valueOf(selectedCategory), "0"};
            adapter.changeCursor(DbAccess.getCursorAllNotes(getBaseContext(), selection, selectionArgs));
        }
    }

    private void updateListByPriority() {

        ListView notesList = (ListView) findViewById(R.id.notes_list);
        CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
        if (selectedCategory == -1) { //show all
            String selection = DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {"0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesPriority(getBaseContext(), selection, selectionArgs));
        } else {
            String selection = DbContract.NoteEntry.COLUMN_CATEGORY + " = ? AND " + DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {String.valueOf(selectedCategory), "0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesPriority(getBaseContext(), selection, selectionArgs));
        }

    }

    private void updateListAlphabetical() {
        ListView notesList = (ListView) findViewById(R.id.notes_list);
        CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
        if (selectedCategory == -1) { //show all
            String selection = DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {"0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesAlphabetical(getBaseContext(), selection, selectionArgs));
        } else {
            String selection = DbContract.NoteEntry.COLUMN_CATEGORY + " = ? AND " + DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {String.valueOf(selectedCategory), "0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesAlphabetical(getBaseContext(), selection, selectionArgs));
        }
    }

    private void updateListByColor() {
        ListView notesList = (ListView) findViewById(R.id.notes_list);
        CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
        if (selectedCategory == -1) { //show all
            String selection = DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {"0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesColor(getBaseContext(), selection, selectionArgs));
        } else {
            String selection = DbContract.NoteEntry.COLUMN_CATEGORY + " = ? AND " + DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {String.valueOf(selectedCategory), "0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesColor(getBaseContext(), selection, selectionArgs));
        }
    }

    private void updateListByCreated() {
        ListView notesList = (ListView) findViewById(R.id.notes_list);
        CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
        if (selectedCategory == -1) { //show all
            String selection = DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {"0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesCreated(getBaseContext(), selection, selectionArgs));
        } else {
            String selection = DbContract.NoteEntry.COLUMN_CATEGORY + " = ? AND " + DbContract.NoteEntry.COLUMN_TRASH + " = ?";
            String[] selectionArgs = {String.valueOf(selectedCategory), "0"};
            adapter.changeCursor(DbAccess.getCursorAllNotesCreated(getBaseContext(), selection, selectionArgs));
        }
    }

    private void deleteSelectedItems() {
        ListView notesList = (ListView) findViewById(R.id.notes_list);
        CursorAdapter adapter = (CursorAdapter) notesList.getAdapter();
        SparseBooleanArray checkedItemPositions = notesList.getCheckedItemPositions();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if (checkedItemPositions.valueAt(i)) {
                DbAccess.trashNote(getBaseContext(), (int) (long) adapter.getItemId(checkedItemPositions.keyAt(i)));
            }
        }
    }
}
