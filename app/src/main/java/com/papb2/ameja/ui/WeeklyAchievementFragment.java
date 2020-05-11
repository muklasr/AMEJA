package com.papb2.ameja.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.papb2.ameja.R;
import com.papb2.ameja.db.ScheduleHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyAchievementFragment extends Fragment implements View.OnClickListener {

    private AnyChartView anyChartView;
    private TextView tvCompleted;
    private TextView tvNotCompleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_achievement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = view.findViewById(R.id.any_chart_view);
        Button btnMode = view.findViewById(R.id.btnMode);
        tvCompleted = view.findViewById(R.id.tvCompleted);
        tvNotCompleted = view.findViewById(R.id.tvNotCompleted);

        setupChart(view);

        btnMode.setOnClickListener(this);
    }

    private void setupChart(View view) {
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.yAxis(0);
//        cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();

        ScheduleHelper scheduleHelper = new ScheduleHelper(Objects.requireNonNull(getContext()));
        scheduleHelper.open();

        int sumCompleted = 0;
        int sumNotCompleted = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);

        for (int i = 7; i > 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/YYYY", Locale.getDefault());
            String date = sdf.format(calendar.getTime());

            int completed = scheduleHelper.countByDateAndStatus(date, 1);
            int notCompleted = scheduleHelper.countByDateAndStatus(date, 0);
            sumCompleted += completed;
            sumNotCompleted += notCompleted;

            SimpleDateFormat sdf2 = new SimpleDateFormat("d MMMM", Locale.getDefault());
            String label = sdf2.format(calendar.getTime());
            if (i < 2) label = getString(R.string.today);
            seriesData.add(new CustomDataEntry(label, completed, notCompleted));
        }
        scheduleHelper.close();

        tvCompleted.setText(String.valueOf(sumCompleted));
        tvNotCompleted.setText(String.valueOf(sumNotCompleted));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name(getString(R.string.completed));
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name(getString(R.string.not_completed));
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
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

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }

    }
}
