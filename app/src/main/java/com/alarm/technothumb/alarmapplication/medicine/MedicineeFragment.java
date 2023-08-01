package com.alarm.technothumb.alarmapplication.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alarm.technothumb.alarmapplication.R;
import com.alarm.technothumb.alarmapplication.medicine.report.MonthlyReportActivity;
import com.alarm.technothumb.alarmapplication.medicine.utils.ActivityUtils;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by NIKUNJ on 19-01-2018.
 */

public class MedicineeFragment extends Fragment {

//    @BindView(R.id.compactcalendar_view)
    CompactCalendarView mCompactCalendarView;

//    @BindView(R.id.date_picker_text_view)
    TextView datePickerTextView;

//    @BindView(R.id.date_picker_button)
    RelativeLayout datePickerButton;

//    @BindView(R.id.toolbar)
    Toolbar toolbar;

//    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

//    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

//    @BindView(R.id.contentFrame)
    FrameLayout contentFrame;

//    @BindView(R.id.fab_add_task)
    FloatingActionButton fabAddTask;
    FloatingActionButton fab_task;

//    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

//    @BindView(R.id.date_picker_arrow)
    ImageView arrow;

    private MedicinePresenter presenter;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", /*Locale.getDefault()*/Locale.ENGLISH);

    private boolean isExpanded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_medicinee, container, false);

//        setHasOptionsMenu(true);
//        ButterKnife.bind(getActivity());
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        mCompactCalendarView = (CompactCalendarView) v.findViewById(R.id.compactcalendar_view);
        datePickerTextView = (TextView) v.findViewById(R.id.date_picker_text_view);
        datePickerButton = (RelativeLayout)v.findViewById(R.id.date_picker_button);
        toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = (AppBarLayout)v.findViewById(R.id.app_bar_layout);
        contentFrame = (FrameLayout) v.findViewById(R.id.contentFrame);
        fabAddTask = (FloatingActionButton)v.findViewById(R.id.fab_add_task);
        fab_task = (FloatingActionButton)v.findViewById(R.id.fab_task);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayout);
        arrow = (ImageView) v.findViewById(R.id.date_picker_arrow);

        fab_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MonthlyReportActivity.class);
                startActivity(intent);
            }
        });


        mCompactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateClicked);

                int day = calendar.get(Calendar.DAY_OF_WEEK);

                if (isExpanded) {
                    ViewCompat.animate(arrow).rotation(0).start();
                } else {
                    ViewCompat.animate(arrow).rotation(180).start();
                }
                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);
                presenter.reload(day);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
            }
        });
        setCurrentDate(new Date());
        MedicineFragment medicineFragment = (MedicineFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (medicineFragment == null) {
            medicineFragment = MedicineFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getActivity().getSupportFragmentManager(), medicineFragment, R.id.contentFrame);
        }

        //Create MedicinePresenter
        presenter = new MedicinePresenter(Injection.provideMedicineRepository(getActivity()), medicineFragment);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isExpanded) {
                    ViewCompat.animate(arrow).rotation(0).start();
                } else {
                    ViewCompat.animate(arrow).rotation(180).start();
                }

                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);

            }
        });

        return v;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.medicine_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.medicine_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_stats) {
//            Intent intent = new Intent(getActivity(), MonthlyReportActivity.class);
//            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        mCompactCalendarView.setCurrentDate(date);
    }

    public void setSubtitle(String subtitle) {
        datePickerTextView.setText(subtitle);
    }

//    @OnClick(R.id.date_picker_button)
//    void onDatePickerButtonClicked() {
//        if (isExpanded) {
//            ViewCompat.animate(arrow).rotation(0).start();
//        } else {
//            ViewCompat.animate(arrow).rotation(180).start();
//        }
//
//        isExpanded = !isExpanded;
//        appBarLayout.setExpanded(isExpanded, true);
//    }
}
