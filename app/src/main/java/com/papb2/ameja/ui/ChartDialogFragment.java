package com.papb2.ameja.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.papb2.ameja.R;

public class ChartDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private ClickCallback callback;

    public ChartDialogFragment(ClickCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvMonthly = view.findViewById(R.id.tvMonthly);
        TextView tvWeekly = view.findViewById(R.id.tvWeekly);

        tvMonthly.setOnClickListener(this);
        tvWeekly.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvMonthly:
                callback.onItemClicked(new MonthlyAchievementFragment());
                break;
            case R.id.tvWeekly:
                callback.onItemClicked(new WeeklyAchievementFragment());
                break;
        }
    }

    interface ClickCallback {
        void onItemClicked(Fragment fragment);
    }

}
