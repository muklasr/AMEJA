package com.papb2.ameja.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.papb2.ameja.R;

import java.util.Calendar;

public class AddDialogFragment extends BottomSheetDialogFragment {

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
        EditText etAgenda = view.findViewById(R.id.etAgenda);
        EditText etStart = view.findViewById(R.id.etStart);
        EditText etStop = view.findViewById(R.id.etEnd);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        etStart.setText(selectedTime.get(Calendar.HOUR_OF_DAY) + ":00");
    }

}
