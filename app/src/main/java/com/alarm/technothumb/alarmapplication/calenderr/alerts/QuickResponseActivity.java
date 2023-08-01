package com.alarm.technothumb.alarmapplication.calenderr.alerts;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.calenderr.Utils;

import java.util.Arrays;

public class QuickResponseActivity extends ListActivity implements AdapterView.OnItemClickListener {
    public static final String EXTRA_EVENT_ID = "eventId";
    private static final String TAG = "QuickResponseActivity";
    static long mEventId;
    private String[] mResponses = null;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        mEventId = intent.getLongExtra(EXTRA_EVENT_ID, -1);
        if (mEventId == -1) {
            finish();
            return;
        }

        // Set listener
        getListView().setOnItemClickListener(QuickResponseActivity.this);

        // Populate responses
        String[] responses = Utils.getQuickResponses(this);
        Arrays.sort(responses);

        // Add "Custom response..."
        mResponses = new String[responses.length + 1];
        int i;
        for (i = 0; i < responses.length; i++) {
            mResponses[i] = responses[i];
        }
        mResponses[i] = getResources().getString(R.string.quick_response_custom_msg);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_quick_response, mResponses));
    }

    // implements OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String body = null;
        if (mResponses != null && position < mResponses.length - 1) {
            body = mResponses[position];
        }

        // Start thread to query provider and send mail
        new QueryThread(mEventId, body).start();
    }

    private class QueryThread extends Thread {
        long mEventId;
        String mBody;

        QueryThread(long eventId, String body) {
            mEventId = eventId;
            mBody = body;
        }

        @Override
        public void run() {
            Intent emailIntent = AlertReceiver.createEmailIntent(QuickResponseActivity.this,
                    mEventId, mBody);
            if (emailIntent != null) {
                try {
                    startActivity(emailIntent);
                    finish();
                } catch (ActivityNotFoundException ex) {
                    QuickResponseActivity.this.getListView().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QuickResponseActivity.this,
                                    R.string.quick_response_email_failed, Toast.LENGTH_LONG);
                            finish();
                        }
                    });
                }
            }
        }
    }
}

