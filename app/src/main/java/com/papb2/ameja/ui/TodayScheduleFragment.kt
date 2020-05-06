package com.papb2.ameja.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papb2.ameja.R
import com.papb2.ameja.adapter.ImportantAdapter
import com.papb2.ameja.db.ScheduleHelper
import com.papb2.ameja.helper.MappingHelper.mapCursorToArrayList
import com.papb2.ameja.model.Schedule
import kotlinx.android.synthetic.main.fragment_today_schedule.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class TodayScheduleFragment : Fragment() {

    private lateinit var scheduleHelper: ScheduleHelper
    private lateinit var adapter: ImportantAdapter

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvSchedule: RecyclerView = view.findViewById(R.id.rvSchedule)
        scheduleHelper = ScheduleHelper(context!!)
        scheduleHelper.open()
        adapter = ImportantAdapter()

        if (savedInstanceState == null) {
            loadSchedulesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Schedule>(EXTRA_STATE)
            if (list != null) {
                adapter.listSchedule = list
            }
        }
        rvSchedule.layoutManager = LinearLayoutManager(context)
        rvSchedule.setHasFixedSize(true)
        rvSchedule.adapter = adapter
    }
    private fun loadSchedulesAsync() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("d/M/YYYY", Locale.getDefault())
        val date = sdf.format(calendar.time)

        GlobalScope.launch(Dispatchers.Main) {
            val deferredSchedules = async(Dispatchers.IO) {
                val cursor = scheduleHelper.queryByDate(date)
                mapCursorToArrayList(cursor)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listSchedule)
    }
}