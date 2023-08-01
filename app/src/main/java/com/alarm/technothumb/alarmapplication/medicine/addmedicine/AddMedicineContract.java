package com.alarm.technothumb.alarmapplication.medicine.addmedicine;

import com.alarm.technothumb.alarmapplication.medicine.data.source.MedicineAlarm;
import com.alarm.technothumb.alarmapplication.medicine.data.source.Pills;
import com.alarm.technothumb.alarmapplication.medicine.views.BasePresenter;
import com.alarm.technothumb.alarmapplication.medicine.views.BaseView;

import java.util.List;

/**
 * Created by NIKUNJ on 19-01-2018.
 */

public interface AddMedicineContract {

    interface View extends BaseView<Presenter> {

        void showEmptyMedicineError();

        void showMedicineList();

        boolean isActive();

    }

    interface  Presenter extends BasePresenter {


        void saveMedicine(MedicineAlarm alarm, Pills pills);


        boolean isDataMissing();

        boolean isMedicineExits(String pillName);

        long addPills(Pills pills);

        Pills getPillsByName(String pillName);

        List<MedicineAlarm> getMedicineByPillName(String pillName);

        List<Long> tempIds();

        void deleteMedicineAlarm(long alarmId);

    }
}
