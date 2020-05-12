package com.papb2.ameja.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.papb2.ameja.R
import com.papb2.ameja.db.ScheduleHelper
import com.papb2.ameja.helper.MappingHelper
import com.papb2.ameja.model.Schedule
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
            ScheduleRemoteViewsFactory(this.applicationContext)
}


class ScheduleRemoteViewsFactory(private val mContext: Context) :
        RemoteViewsService.RemoteViewsFactory {

    var mWidgetItems = ArrayList<Schedule>()

    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = 0

    override fun onDataSetChanged() {
        val scheduleHelper = ScheduleHelper(mContext)
        scheduleHelper.open()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("d/M/YYYY", Locale.getDefault())
        val date = sdf.format(calendar.time)
        val cursor = scheduleHelper.queryByDate(date)
        val schedules = cursor.let { MappingHelper.mapCursorToArrayList(cursor) }
        schedules.let {
            if (schedules.isNotEmpty()) {
                mWidgetItems = schedules
            }
        }
        scheduleHelper.close()
    }


    override fun hasStableIds(): Boolean = false

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName,
                R.layout.widget_item
        )
        Log.d("TES", mWidgetItems[position].agenda)
        rv.setTextViewText(R.id.tvAgenda, mWidgetItems[position].agenda)
        rv.setTextViewText(R.id.tvInfo, mWidgetItems[position].start + mContext.getString(R.string.at) + mWidgetItems[position].location)

        return rv
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {

    }

}