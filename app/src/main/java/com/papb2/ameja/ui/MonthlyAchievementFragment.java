package com.papb2.ameja.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.papb2.ameja.R;
import com.papb2.ameja.db.ScheduleHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MonthlyAchievementFragment extends Fragment implements View.OnClickListener {

    private AnyChartView anyChartView;
    private int total;
    private int completed;
    private int notCompleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly_achievement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = view.findViewById(R.id.any_chart_view);
        Button btnMode = view.findViewById(R.id.btnMode);
        TextView tvTotal = view.findViewById(R.id.tvTotal);
        TextView tvCompleted = view.findViewById(R.id.tvCompleted);
        TextView tvNotCompleted = view.findViewById(R.id.tvNotCompleted);

        btnMode.setOnClickListener(this);

        ScheduleHelper scheduleHelper = new ScheduleHelper(Objects.requireNonNull(getContext()));
        scheduleHelper.open();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("/M/YYYY", Locale.getDefault());
        String month = sdf.format(calendar.getTime());
        this.notCompleted = scheduleHelper.countByMonthAndStatus(month, 0);
        this.completed = scheduleHelper.countByMonthAndStatus(month, 1);
        this.total = this.notCompleted + this.completed;
        scheduleHelper.close();

        setupChart(view);
        tvTotal.setText(String.valueOf(total));
        tvCompleted.setText(String.valueOf(completed));
        tvNotCompleted.setText(String.valueOf(notCompleted));

    }

    private void setupChart(View view) {
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(getActivity(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry(getString(R.string.completed), this.completed));
        data.add(new ValueDataEntry(getString(R.string.not_completed), this.notCompleted));

        pie.data(data);

        pie.labels().position("outside");

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);
    }

    private void showDialog() {
        ChartDialogFragment chartDialogFragment = new ChartDialogFragment((AchievementFragment) getParentFragment());
        chartDialogFragment.show(getChildFragmentManager(), "Dialog");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMode:
                showDialog();
                break;
        }
    }
}
