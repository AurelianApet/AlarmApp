package com.alarm.technothumb.alarmapplication.calenderr;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class Rfc822InputFilter implements InputFilter {

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                               int dstart, int dend) {

        // quick check - did they enter a single space?
        if (end-start != 1 || source.charAt(start) != ' ') {
            return null;
        }

        // determine if the characters before the new space fit the pattern
        // follow backwards and see if we find a comma, dot, or @
        int scanBack = dstart;
        boolean dotFound = false;
        while (scanBack > 0) {
            char c = dest.charAt(--scanBack);
            switch (c) {
                case '.':
                    dotFound = true;    // one or more dots are req'd
                    break;
                case ',':
                    return null;
                case '@':
                    if (!dotFound) {
                        return null;
                    }
                    // we have found a comma-insert case.  now just do it
                    // in the least expensive way we can.
                    if (source instanceof Spanned) {
                        SpannableStringBuilder sb = new SpannableStringBuilder(",");
                        sb.append(source);
                        return sb;
                    } else {
                        return ", ";
                    }
                default:
                    // just keep going
            }
        }

        // no termination cases were found, so don't edit the input
        return null;
    }
}

