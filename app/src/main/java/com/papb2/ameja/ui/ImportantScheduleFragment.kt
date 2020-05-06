package com.papb2.ameja.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papb2.ameja.helper.MappingHelper.mapCursorToArrayList
import com.papb2.ameja.R
import com.papb2.ameja.adapter.ImportantAdapter
import com.papb2.ameja.db.ScheduleHelper
import com.papb2.ameja.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ImportantScheduleFragment : Fragment() {

    private lateinit var scheduleHelper: ScheduleHelper
    private lateinit var adapter: ImportantAdapter

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_important_schedule, container, false)
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
        GlobalScope.launch(Dispatchers.Main) {
            val deferredSchedules = async(Dispatchers.IO) {
                val cursor = scheduleHelper.queryImportant(true)
                mapCursorToArrayList(cursor)
            }

            val schedules = deferredSchedules.await()
            if (schedules.size > 0) {
                adapter.setData(schedules)
            } else {
                adapter.listSchedule = ArrayList()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listSchedule)
    }
}