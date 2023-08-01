package com.alarm.technothumb.alarmapplication.medicine.alarm;

import com.alarm.technothumb.alarmapplication.medicine.data.source.History;
import com.alarm.technothumb.alarmapplication.medicine.data.source.MedicineAlarm;
import com.alarm.technothumb.alarmapplication.medicine.views.BasePresenter;
import com.alarm.technothumb.alarmapplication.medicine.views.BaseView;

/**
 * Created by NIKUNJ on 19-01-2018.
 */

public interface ReminderContract {

    interface View extends BaseView<Presenter> {

        void showMedicine(MedicineAlarm medicineAlarm);

        void showNoData();

        boolean isActive();

        void onFinish();

    }

    interface Presenter extends BasePresenter {

        void finishActivity();

        void onStart(long id);

        void loadMedicineById(long id);

        void addPillsToHistory(History history);

    }
}
