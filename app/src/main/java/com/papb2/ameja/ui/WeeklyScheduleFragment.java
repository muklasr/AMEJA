package com.papb2.ameja.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.papb2.ameja.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WeeklyScheduleFragment extends Fragment {

    public WeeklyScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
    }
}