package com.papb2.ameja.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns

internal class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "scheduledb"
        private const val DATABASE_VERSION = 1
        private val SQL_CREATE_TABLE_SCHEDULE = "CREATE TABLE ${ScheduleColumns.TABLE_NAME}" +
                "(${ScheduleColumns.ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${ScheduleColumns.AGENDA} TEXT NOT NULL," +
                "${ScheduleColumns.DATE} TEXT NOT NULL," +
                "${ScheduleColumns.START} TEXT NOT NULL," +
                "${ScheduleColumns.END} TEXT NOT NULL," +
                "${ScheduleColumns.LOCATION} TEXT NOT NULL," +
                "${ScheduleColumns.STATUS} INTEGER NOT NULL," +
                "${ScheduleColumns.IMPORTANT} BOOLEAN NOT NULL)"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_SCHEDULE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${ScheduleColumns.TABLE_NAME}")
        onCreate(db)
    }

}