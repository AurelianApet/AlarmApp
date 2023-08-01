package com.alarm.technothumb.alarmapplication.calenderr;

import android.accounts.Account;
import android.content.Context;

import com.android.ex.chips.BaseRecipientAdapter;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class RecipientAdapter extends BaseRecipientAdapter {
    public RecipientAdapter(Context context) {
        super(context);
    }

    /**
     * Set the account when known. Causes the search to prioritize contacts from
     * that account.
     */
    public void setAccount(Account account) {
        if (account != null) {
            // TODO: figure out how to infer the contacts account
            // type from the email account
            super.setAccount(new Account(account.name, "unknown"));
        }
    }
}
