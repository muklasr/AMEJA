package com.papb2.ameja.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.Calendar;

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
    private Button btnSave;
    private Button btnCancel;

    private Calendar selectedTime;

    AddDialogFragment(Calendar time) {
        this.selectedTime = time;
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
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        etStart.setText(selectedTime.get(Calendar.HOUR_OF_DAY) + ":00");
        btnSave.setOnClickListener(this);
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
        String date = String.valueOf(selectedTime.get(Calendar.DATE));
        String start = etStart.getText().toString();
        String end = etEnd.getText().toString();
        String location = etLocation.getText().toString();
        int status = 0;
        boolean important = false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(AGENDA, agenda);
        contentValues.put(DATE, date);
        contentValues.put(START, start);
        contentValues.put(END, end);
        contentValues.put(LOCATION, location);
        contentValues.put(STATUS, status);
        contentValues.put(IMPORTANT, important);

        ScheduleHelper scheduleHelper = new ScheduleHelper(getContext());
        scheduleHelper.open();
         scheduleHelper.insert(contentValues);
        Cursor cursor = scheduleHelper.queryAll();
        if (cursor.getCount() > 0) {
            Toast.makeText(getContext(), "Schedule successfully added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Schedule failed to be added", Toast.LENGTH_LONG).show();
        }
        scheduleHelper.close();
    }
}
