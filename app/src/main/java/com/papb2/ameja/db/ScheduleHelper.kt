package com.papb2.ameja.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.DATE
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.ID
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.IMPORTANT
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.START
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.STATUS
import com.papb2.ameja.db.DatabaseContract.ScheduleColumns.Companion.TABLE_NAME

class ScheduleHelper(context: Context) {

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var databaseHelper: DatabaseHelper
        private lateinit var database: SQLiteDatabase
    }
    init {
        databaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun queryById(id: String): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                "$ID = ?",
                arrayOf(id),
                null,
                null,
                null,
                null
        )
    }

    fun queryByDate(date: String): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                "$DATE = ?",
                arrayOf(date),
                null,
                null,
                "$START ASC",
                null
        )
    }

    fun queryImportant(state: Boolean): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                "$IMPORTANT = ?",
                arrayOf(state.toString()),
                null,
                null,
                null,
                null
        )
    }

    private fun queryByMonthAndStatus(month: String, status: Int): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                "$DATE like '%$month%' AND $STATUS = $status",
                null,
                null,
                null,
                null,
                null
        )
    }

    private fun queryByDateAndStatus(status: Int, date: String): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                "$DATE = '$date' AND $STATUS = $status",
                null,
                null,
                null,
                null,
                null
        )
    }

    fun queryAll(): Cursor {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                "$ID DESC"
        )
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$ID = ?", arrayOf(id))
    }

    fun delete(id: String): Int {
        return database.delete(DATABASE_TABLE, "$ID = ?", arrayOf(id))
    }

    fun countByMonthAndStatus(month: String, status: Int):Int{
        return queryByMonthAndStatus(month, status).count
    }

    fun countByDateAndStatus(date: String, status: Int): Int{
        return queryByDateAndStatus(status, date).count
    }
}