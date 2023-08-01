package com.alarm.technothumb.alarmapplication.medicine;

import com.alarm.technothumb.alarmapplication.medicine.data.source.MedicineAlarm;
import com.alarm.technothumb.alarmapplication.medicine.views.BasePresenter;
import com.alarm.technothumb.alarmapplication.medicine.views.BaseView;

import java.util.List;

/**
 * Created by NIKUNJ on 19-01-2018.
 */

public interface MedicineContract {

    interface View extends BaseView<Presenter> {

        void showLoadingIndicator(boolean active);

        void showMedicineList(List<MedicineAlarm> medicineAlarmList);

        void showAddMedicine();

        void showMedicineDetails(long medId, String medName);

        void showLoadingMedicineError();

        void showNoMedicine();

        void showSuccessfullySavedMessage();

        boolean isActive();


    }

    interface Presenter extends BasePresenter {

        void onStart(int day);

        void reload(int day);

        void result(int requestCode, int resultCode);

        void loadMedicinesByDay(int day, boolean showIndicator);


        void addNewMedicine();

    }
}
