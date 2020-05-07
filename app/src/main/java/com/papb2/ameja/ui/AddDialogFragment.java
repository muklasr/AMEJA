package com.papb2.ameja.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.papb2.ameja.R;
import com.papb2.ameja.db.ScheduleHelper;
import com.papb2.ameja.model.Schedule;
import com.papb2.ameja.receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.Objects;

import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.AGENDA;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.DATE;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.END;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.IMPORTANT;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.LOCATION;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.START;
import static com.papb2.ameja.db.DatabaseContract.ScheduleColumns.STATUS;

public class AddDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText etAgenda;
    private EditText etStart;
    private EditText etEnd;
    private EditText etLocation;

    private ScheduleHelper scheduleHelper;

    private Calendar selectedTime;
    private Long idSchedule;
    private Boolean isUpdate;
    private String scheduleDate;
    private int scheduleStatus = 0;
    private Boolean isImportant = false;

    AddDialogFragment(Calendar time) {
        this.selectedTime = time;
        this.isUpdate = false;
    }

    AddDialogFragment(Long id) {
        this.idSchedule = id;
        this.isUpdate = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etAgenda = view.findViewById(R.id.etAgenda);
        etStart = view.findViewById(R.id.etStart);
        etEnd = view.findViewById(R.id.etEnd);
        etLocation = view.findViewById(R.id.etLocation);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        scheduleHelper = new ScheduleHelper(Objects.requireNonNull(getContext()));

        if (isUpdate)
            loadData();
        else
            etStart.setText(selectedTime.get(Calendar.HOUR_OF_DAY) + ":00");
    }

    private void loadData() {
        scheduleHelper.open();
        Cursor cursor = scheduleHelper.queryById(this.idSchedule.toString());

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                etAgenda.setText(cursor.getString(cursor.getColumnIndex(AGENDA)));
                etStart.setText(cursor.getString(cursor.getColumnIndex(START)));
                etEnd.setText(cursor.getString(cursor.getColumnIndex(END)));
                etLocation.setText(cursor.getString(cursor.getColumnIndex(LOCATION)));
                this.scheduleDate = cursor.getString(cursor.getColumnIndex(DATE));
                this.scheduleStatus = cursor.getInt(cursor.getColumnIndex(STATUS));
                this.isImportant = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(IMPORTANT)));
            }
        }
        scheduleHelper.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                saveSchedule();
                break;
            case R.id.btnCancel:
                this.dismiss();
                break;
        }
    }

    private void saveSchedule() {
        String agenda = etAgenda.getText().toString();
        String date;
        if (isUpdate) {
            date = this.scheduleDate;
        } else {
            String month = String.valueOf(selectedTime.get(Calendar.MONTH) + 1);
            date = selectedTime.get(Calendar.DATE) + "/" + month + "/" + selectedTime.get(Calendar.YEAR);
        }
        String start = etStart.getText().toString();
        String end = etEnd.getText().toString();
        String location = etLocation.getText().toString();
        int status = this.scheduleStatus;

        ContentValues contentValues = new ContentValues();
        contentValues.put(AGENDA, agenda);
        contentValues.put(DATE, date);
        contentValues.put(START, start);
        contentValues.put(END, end);
        contentValues.put(LOCATION, location);
        contentValues.put(STATUS, status);
        contentValues.put(IMPORTANT, isImportant);

        setupReminder(new Schedule(0, agenda, date, start, end, location, status, isImportant));

        scheduleHelper.open();

        if (isUpdate) {
            int result = scheduleHelper.update(idSchedule.toString(), contentValues);

            if (result > 0) {
                Toast.makeText(getContext(), R.string.update_success, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), R.string.update_fail, Toast.LENGTH_LONG).show();
            }
        } else {
            int currentCount = scheduleHelper.queryAll().getCount();
            scheduleHelper.insert(contentValues);

            Cursor cursor = scheduleHelper.queryAll();
            if (cursor.getCount() > currentCount) {
                Toast.makeText(getContext(), R.string.add_success, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), R.string.add_fail, Toast.LENGTH_LONG).show();
            }
        }

        scheduleHelper.close();
        assert getParentFragment() != null;
        ((WeeklyScheduleFragment) getParentFragment()).refreshCalendar();
        this.dismiss();
    }

    private void setupReminder(Schedule schedule){
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAgendaReminder(Objects.requireNonNull(getContext()), schedule);
    }
}
