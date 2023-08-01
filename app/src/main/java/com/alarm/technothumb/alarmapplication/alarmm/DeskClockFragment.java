package com.alarm.technothumb.alarmapplication.alarmm;

import android.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.alarm.technothumb.alarmapplication.R;

/**
 * Created by NIKUNJ on 28-03-2018.
 */

public class DeskClockFragment extends Fragment {

    public void onPageChanged(int page) {
        // Do nothing here , only in derived classes
    }

    /**
     * Installs click and touch listeners on a fake overflow menu button.
     *
     * @param menuButton the fragment's fake overflow menu button
     */
    public void setupFakeOverflowMenuButton(View menuButton) {
        final PopupMenu fakeOverflow = new PopupMenu(menuButton.getContext(), menuButton) {
            @Override
            public void show() {
                getActivity().onPrepareOptionsMenu(getMenu());
                super.show();
            }
        };
        fakeOverflow.inflate(R.menu.desk_clock_menu);
        fakeOverflow.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener () {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return getActivity().onOptionsItemSelected(item);
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeOverflow.show();
            }
        });
    }
}
