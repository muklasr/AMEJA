package com.papb2.ameja.ui

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.papb2.ameja.MappingHelper.mapCursorToArrayList
import com.papb2.ameja.R
import com.papb2.ameja.adapter.ImportantAdapter
import com.papb2.ameja.db.ScheduleHelper
import com.papb2.ameja.model.Schedule
import java.lang.ref.WeakReference
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ImportantScheduleFragment : Fragment(), LoadScheduleCallback {
    private var scheduleHelper: ScheduleHelper? = null
    private var adapter: ImportantAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_important_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvSchedule: RecyclerView = view.findViewById(R.id.rvSchedule)
        scheduleHelper = ScheduleHelper(context!!)
        scheduleHelper!!.open()
        adapter = ImportantAdapter()
        rvSchedule.layoutManager = LinearLayoutManager(context)
        rvSchedule.setHasFixedSize(true)
        rvSchedule.adapter = adapter
        if (savedInstanceState == null) { // proses ambil data
            LoadScheduleAsync(scheduleHelper!!, context as LoadScheduleCallback?).execute()
        } else {
            val list: ArrayList<out Schedule?> = savedInstanceState.getParcelableArrayList(EXTRA_STATE)
            if (list != null) {
                adapter!!.setData(list as ArrayList<Schedule?>)
            }
        }
    }

    protected override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter!!.listSchedule)
    }

    override fun preExecute() {}
    override fun postExecute(schedules: ArrayList<Schedule?>) {
        if (schedules.size > 0) {
            adapter!!.setData(schedules)
        } else {
            adapter!!.setData(ArrayList())
        }
    }

    private class LoadScheduleAsync(scheduleHelper: ScheduleHelper, callback: LoadScheduleCallback?) : AsyncTask<Void?, Void?, ArrayList<Schedule?>>() {
        private val weakScheduleHelper: WeakReference<ScheduleHelper>
        private val weakCallback: WeakReference<LoadScheduleCallback?>
        override fun onPreExecute() {
            super.onPreExecute()
            weakCallback.get()!!.preExecute()
        }

        protected override fun doInBackground(vararg voids: Void): ArrayList<Schedule?> {
            val dataCursor = weakScheduleHelper.get()!!.queryAll()
            return mapCursorToArrayList(dataCursor)
        }

        override fun onPostExecute(schedules: ArrayList<Schedule?>) {
            super.onPostExecute(schedules)
            weakCallback.get()!!.postExecute(schedules)
        }

        init {
            weakScheduleHelper = WeakReference(scheduleHelper)
            weakCallback = WeakReference(callback)
        }
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
}

internal interface LoadScheduleCallback {
    fun preExecute()
    fun postExecute(schedules: ArrayList<Schedule?>)
}