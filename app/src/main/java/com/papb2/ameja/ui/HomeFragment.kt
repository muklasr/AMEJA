package com.papb2.ameja.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.papb2.ameja.R
import com.papb2.ameja.adapter.HomeTodayAdapter
import com.papb2.ameja.db.ScheduleHelper
import com.papb2.ameja.helper.MappingHelper
import com.papb2.ameja.model.Schedule
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_today_schedule.rvSchedule
import kotlinx.android.synthetic.main.fragment_today_schedule.tvFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var adapter: HomeTodayAdapter
    private lateinit var today: String
    private lateinit var scheduleHelper: ScheduleHelper

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleHelper = ScheduleHelper(requireContext())
        scheduleHelper.open()
        if (savedInstanceState == null) {
            loadSchedulesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Schedule>(EXTRA_STATE)
            if (list != null) {
                adapter.listSchedule = list
            }
        }
        rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        rvSchedule.setHasFixedSize(true)
        rvSchedule.adapter = adapter

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        val date = sdf.format(calendar.time)
        tvDate.text = date


        val sdf2 = SimpleDateFormat("d/M/YYYY", Locale.getDefault())
        today = sdf2.format(calendar.time)

        val notCompleted = scheduleHelper.countByDateAndStatus(today, 0)
        val completed = scheduleHelper.countByDateAndStatus(today, 1)
        val total = notCompleted + completed

        tvCompleted.text = "$completed / $total"
        tvNotCompleted.text = notCompleted.toString()
    }

    private fun loadSchedulesAsync() {
        adapter = HomeTodayAdapter()
        GlobalScope.launch(Dispatchers.Main) {
            val deferredSchedules = async(Dispatchers.IO)
            {
                val cursor = scheduleHelper.queryByDate(today)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val schedules = deferredSchedules.await()
            if (schedules.size > 0) {
                adapter.setData(schedules)
                tvFeedback.text = ""
            } else {
                adapter.listSchedule = ArrayList()
                tvFeedback.text = getString(R.string.no_schedule)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scheduleHelper.close()
    }
}