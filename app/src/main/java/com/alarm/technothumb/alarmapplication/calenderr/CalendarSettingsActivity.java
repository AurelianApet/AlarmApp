package com.alarm.technothumb.alarmapplication.calenderr;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.selectcalendars.SelectCalendarsSyncFragment;

import java.util.List;

public class CalendarSettingsActivity extends PreferenceActivity {
    private static final int CHECK_ACCOUNTS_DELAY = 3000;
    private Account[] mAccounts;
    Runnable mCheckAccounts = new Runnable() {
        @Override
        public void run() {
            Account[] accounts = AccountManager.get(CalendarSettingsActivity.this).getAccounts();
            if (accounts != null && !accounts.equals(mAccounts)) {
                invalidateHeaders();
            }
        }
    };
    private Handler mHandler = new Handler();
    private boolean mHideMenuButtons = false;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.app_bar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationIcon(R.drawable.ic_ab_back);
        bar.setTitle(getTitle());
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.calendar_settings_headers, target);

        Account[] accounts = AccountManager.get(this).getAccounts();
        if (accounts != null) {
            int length = accounts.length;
            for (int i = 0; i < length; i++) {
                Account acct = accounts[i];
                if (ContentResolver.getIsSyncable(acct, CalendarContract.AUTHORITY) > 0) {
                    Header accountHeader = new Header();
                    accountHeader.title = acct.name;
                    accountHeader.fragment =
                            "com.alarm.technothumb.alarmapplication.calenderr.selectcalendars.SelectCalendarsSyncFragment";

                    Bundle args = new Bundle();
                    args.putString(CalendarContract.Calendars.ACCOUNT_NAME, acct.name);
                    args.putString(CalendarContract.Calendars.ACCOUNT_TYPE, acct.type);
                    accountHeader.fragmentArguments = args;
                    target.add(1, accountHeader);
                }
            }
        }
        mAccounts = accounts;
        if (Utils.getTardis() + DateUtils.MINUTE_IN_MILLIS > System.currentTimeMillis()) {
            Header tardisHeader = new Header();
            tardisHeader.title = getString(R.string.preferences_experimental_category);
            tardisHeader.fragment = "com.alarm.technothumb.alarmapplication.callenderr.OtherPreferences";
            target.add(tardisHeader);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_add_account) {
            Intent nextIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
            final String[] array = { "com.alarm.technothumb.alarmapplication" };
            nextIntent.putExtra(Settings.EXTRA_AUTHORITIES, array);
            nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(nextIntent);
//           CalendarSettingsActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mHideMenuButtons) {
            getMenuInflater().inflate(R.menu.settings_title_bar, menu);
        }
        getActionBar()
                .setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        return true;
    }

    @Override
    public void onResume() {
        if (mHandler != null) {
            mHandler.postDelayed(mCheckAccounts, CHECK_ACCOUNTS_DELAY);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mCheckAccounts);
        }
        super.onPause();
    }

    protected boolean isValidFragment(String fragmentName) {
        return GeneralPreferences.class.getName().equals(fragmentName)
                || SelectCalendarsSyncFragment.class.getName().equals(fragmentName)
                || OtherPreferences.class.getName().equals(fragmentName)
                || AboutPreferences.class.getName().equals(fragmentName)
                || QuickResponseSettings.class.getName().equals(fragmentName);
    }

    public void hideMenuButtons() {
        mHideMenuButtons = true;
    }
}
