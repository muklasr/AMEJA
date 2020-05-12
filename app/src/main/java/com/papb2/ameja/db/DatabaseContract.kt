package com.papb2.ameja.db

import android.provider.BaseColumns

object DatabaseContract {

    internal class ScheduleColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "schedule"
            const val ID = "_id"
            const val AGENDA = "agenda"
            const val DATE = "date"
            const val START = "start"
            const val END = "end"
            const val LOCATION = "location"
            const val STATUS = "status"
            const val IMPORTANT = "important"
        }
    }

}