package com.papb2.ameja.ui

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papb2.ameja.R
import com.papb2.ameja.adapter.HomeTodayAdapter
import com.papb2.ameja.adapter.TodayAdapter
import com.papb2.ameja.db.DatabaseContract
import com.papb2.ameja.db.ScheduleHelper
import com.papb2.ameja.helper.MappingHelper
import com.papb2.ameja.model.Schedule
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_today_schedule.rvSchedule
import kotlinx.android.synthetic.main.fragment_today_schedule.tvFeedback
import kotlinx.android.synthetic.main.item_today_schedule.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var today: String
    private lateinit var scheduleHelper: ScheduleHelper
    private lateinit var adapter: TodayAdapter

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
        adapter = TodayAdapter()
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

        getStat()

    }

    private fun getStat(){
        val notCompleted = scheduleHelper.countByDateAndStatus(today, 0)
        val completed = scheduleHelper.countByDateAndStatus(today, 1)
        val total = notCompleted + completed

        tvCompleted.text = "$completed / $total"
        tvNotCompleted.text = notCompleted.toString()
    }

//without checkbox
//    private fun loadSchedulesAsync() {
//        adapter = HomeTodayAdapter()
//        GlobalScope.launch(Dispatchers.Main) {
//            val deferredSchedules = async(Dispatchers.IO)
//            {
//                val cursor = scheduleHelper.queryByDate(today)
//                MappingHelper.mapCursorToArrayList(cursor)
//            }
//            val schedules = deferredSchedules.await()
//            if (schedules.size > 0) {
//                adapter.setData(schedules)
//                tvFeedback.text = ""
//            } else {
//                adapter.listSchedule = ArrayList()
//                tvFeedback.text = getString(R.string.no_schedule)
//            }
//        }
//    }

    private fun loadSchedulesAsync() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("d/M/YYYY", Locale.getDefault())
        val date = sdf.format(calendar.time)

        GlobalScope.launch(Dispatchers.Main) {
            val deferredSchedules = async(Dispatchers.IO) {
                val cursor = scheduleHelper.queryByDate(date)
                MappingHelper.mapCursorToArrayList(cursor)
            }

            val schedules = deferredSchedules.await()
            if (schedules.size > 0) {
                adapter.setData(schedules)
                adapter.setOnCheckedCallback(object : TodayAdapter.OnCheckedCallback {
                    override fun onChecked(itemView: View, data: Schedule, isChecked: Boolean) {

                        val contentValues = ContentValues()
                        with(itemView) {
                            if (isChecked) {
                                tvAgenda.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                tvAgenda.setTextColor(Color.GRAY)
                                tvInfo.setTextColor(Color.GRAY)

                                contentValues.put(DatabaseContract.ScheduleColumns.STATUS, 1)
                            } else {
                                tvAgenda.paintFlags = Paint.ANTI_ALIAS_FLAG
                                tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                                tvAgenda.setTextColor(Color.BLACK)
                                tvInfo.setTextColor(Color.BLACK)

                                contentValues.put(DatabaseContract.ScheduleColumns.STATUS, 0)
                            }
                        }
                        scheduleHelper.open()
                        scheduleHelper.update(data.id.toString(), contentValues)
                        getStat()
                        scheduleHelper.close()
                    }
                })
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