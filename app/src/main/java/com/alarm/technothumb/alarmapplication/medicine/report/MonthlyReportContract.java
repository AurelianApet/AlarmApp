package com.alarm.technothumb.alarmapplication.medicine.report;

import com.alarm.technothumb.alarmapplication.medicine.data.source.History;
import com.alarm.technothumb.alarmapplication.medicine.views.BasePresenter;
import com.alarm.technothumb.alarmapplication.medicine.views.BaseView;

import java.util.List;

/**
 * Created by NIKUNJ on 19-01-2018.
 */

public interface MonthlyReportContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showHistoryList(List<History> historyList);

        void showLoadingError();

        void showNoHistory();

        void showTakenFilterLabel();

        void showIgnoredFilterLabel();

        void showAllFilterLabel();

        void showNoTakenHistory();

        void showNoIgnoredHistory();

        boolean isActive();

        void showFilteringPopUpMenu();

    }

    interface Presenter extends BasePresenter {

        void loadHistory(boolean showLoading);

        void setFiltering(FilterType filterType);

        FilterType getFilterType();
    }
}

