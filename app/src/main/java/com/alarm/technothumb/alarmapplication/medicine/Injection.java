package com.alarm.technothumb.alarmapplication.medicine;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alarm.technothumb.alarmapplication.medicine.data.source.MedicineRepository;
import com.alarm.technothumb.alarmapplication.medicine.local.MedicinesLocalDataSource;

/**
 * Created by NIKUNJ on 19-01-2018.
 */

public class Injection {

    public static MedicineRepository provideMedicineRepository(@NonNull Context context) {
        return MedicineRepository.getInstance(MedicinesLocalDataSource.getInstance(context));
    }
}
