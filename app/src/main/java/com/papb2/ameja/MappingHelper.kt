package com.papb2.ameja

import android.database.Cursor
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.AGENDA
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.DATE
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.END
import com.papb2.ameja.model.Schedule
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.ID
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.IMPORTANT
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.LOCATION
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.START
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.STATUS

object MappingHelper {
    fun mapCursorToArrayList(schedulesCursor: Cursor?): ArrayList<Schedule> {
        val schedulesList = ArrayList<Schedule>()
        schedulesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ID))
                val agenda = getString(getColumnIndexOrThrow(AGENDA))
                val date = getString(getColumnIndexOrThrow(DATE))
                val start = getString(getColumnIndexOrThrow(START))
                val end = getString(getColumnIndexOrThrow(END))
                val location = getString(getColumnIndexOrThrow(LOCATION))
                val status = getInt(getColumnIndexOrThrow(STATUS))
                val important = getString(getColumnIndexOrThrow(IMPORTANT))!!.toBoolean()
                schedulesList.add(Schedule(id, agenda, date, start, end, location, status, important))
            }
        }
        return schedulesList
    }
}