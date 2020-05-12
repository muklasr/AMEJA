package com.papb2.ameja.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.papb2.ameja.R;
import com.papb2.ameja.widget.TodayScheduleWidget;
import com.papb2.ameja.db.ScheduleHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.AGENDA;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.DATE;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.END;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.ID;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.LOCATION;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.START;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyScheduleFragment extends Fragment implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewClickListener {

    private WeekView mWeekView;

    public WeeklyScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWeekView = view.findViewById(R.id.weekView);

        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setEmptyViewClickListener(this);
        setupDateTimeInterpreter(false);
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) { showDialog(event); }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        ScheduleHelper scheduleHelper = new ScheduleHelper(Objects.requireNonNull(getContext()));
        scheduleHelper.open();
        scheduleHelper.delete(String.valueOf(event.getId()));
        scheduleHelper.close();
        refreshCalendar();
        Toast.makeText(getContext(), event.getName()+getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        showDialog(time);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();

        ScheduleHelper scheduleHelper = new ScheduleHelper(Objects.requireNonNull(getContext()));
        scheduleHelper.open();
        Cursor cursor = scheduleHelper.queryAll();

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int id = cursor.getInt(cursor.getColumnIndex(ID));
                String agenda = cursor.getString(cursor.getColumnIndex(AGENDA));
                String location = cursor.getString(cursor.getColumnIndex(LOCATION));
                String title = agenda + " at " + location;

                String[] start = cursor.getString(cursor.getColumnIndex(START)).split(":");
                int startHour = Integer.parseInt(start[0]);
                int startMinute = Integer.parseInt(start[1]);
                String[] end = cursor.getString(cursor.getColumnIndex(END)).split(":");
                int endHour = Integer.parseInt(end[0]);
                int endMinute = Integer.parseInt(end[1]);
                String[] date = cursor.getString(cursor.getColumnIndex(DATE)).split("/");
                int day = Integer.parseInt(date[0]);
                int year = Integer.parseInt(date[2]);

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, startHour);
                startTime.set(Calendar.MINUTE, startMinute);
                startTime.set(Calendar.DAY_OF_MONTH, day);
                startTime.set(Calendar.MONTH, newMonth - 1);
                startTime.set(Calendar.YEAR, year);
                Calendar endTime = (Calendar) startTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, endHour);
                endTime.set(Calendar.MINUTE, endMinute);
                WeekViewEvent event = new WeekViewEvent(id, title, startTime, endTime);
                event.setColor(getResources().getColor(R.color.colorAccent));
                events.add(event);
            }
        }
        scheduleHelper.close();
        return events;
    }

    private void showDialog(Calendar time) {
        AddDialogFragment addDialogFragment = new AddDialogFragment(time);
        addDialogFragment.show(getChildFragmentManager(), "Dialog");
    }

    private void showDialog(WeekViewEvent event) {
        AddDialogFragment addDialogFragment = new AddDialogFragment(event.getId());
        addDialogFragment.show(getChildFragmentManager(), "Dialog");
    }

    void refreshCalendar() {
        mWeekView.notifyDatasetChanged();
        updateWidget();
    }

    private void updateWidget() {
        AppWidgetManager manager = AppWidgetManager.getInstance(getContext());
        RemoteViews view = new RemoteViews(Objects.requireNonNull(getActivity()).getPackageName(), R.layout.today_schedule_widget);
        ComponentName theWidget = new ComponentName(Objects.requireNonNull(getContext()), TodayScheduleWidget.class);
        int[] ids = manager.getAppWidgetIds(theWidget);

        manager.notifyAppWidgetViewDataChanged(ids, R.id.lvSchedule);
        manager.updateAppWidget(ids, view);
    }
}