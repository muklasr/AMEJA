package com.papb2.ameja.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.papb2.ameja.R
import com.papb2.ameja.model.Schedule
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val CHANNEL_ID = "AMEJA1"
        const val CHANNEL_NAME = "AMEJA Channel"
    }

    override fun onReceive(p0: Context, p1: Intent) {
        Log.d("WAYAHE", "onReceive")
        val id = p1.getIntExtra("id", NOTIFICATION_ID)
        val agenda = p1.getStringExtra("agenda") as String
        val start = p1.getStringExtra("start") as String
        val location = p1.getStringExtra("location") as String
        showNotification(p0, id, agenda, start+p0.getString(R.string.at)+location)
    }

    fun setAgendaReminder(context: Context, schedule: Schedule) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("agenda", schedule.agenda)
        intent.putExtra("start", schedule.start)
        intent.putExtra("location", schedule.location)

        val timeArray = schedule.start.toString().split(":")
        val dateArray = schedule.date.toString().split("/")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1])-1)
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]))

        val uniqueIdString = timeArray[0]+timeArray[1]+dateArray[0]+dateArray[1]
        val uniqueId = Integer.parseInt(uniqueIdString)/5
        Log.d("WAYAHE", uniqueIdString)

        val pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, 0)
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        )
    }

    private fun showNotification(
            context: Context,
            notificationId: Int,
            title: String,
            message: String
    ) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(false)
            channel.description = CHANNEL_NAME
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManager.notify(notificationId, notification)

    }
}
