package com.papb2.ameja.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papb2.ameja.R;

public class ScheduleDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView btnToday = view.findViewById(R.id.tvToday);
        TextView btnWeekly = view.findViewById(R.id.tvWeekly);
        TextView btnImportant = view.findViewById(R.id.tvImportant);

        btnToday.setOnClickListener(this);
        btnWeekly.setOnClickListener(this);
        btnImportant.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.tvToday:
                transaction.replace(R.id.container, new TodayScheduleFragment()).addToBackStack(null).commit();
                this.dismiss();
                break;
            case R.id.tvWeekly:
                transaction.replace(R.id.container, new WeeklyScheduleFragment()).addToBackStack(null).commit();
                this.dismiss();
                break;
            case R.id.tvImportant:
                transaction.replace(R.id.container, new ImportantScheduleFragment()).addToBackStack(null).commit();
                this.dismiss();
                break;
        }
    }
}
