package com.papb2.ameja.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.papb2.ameja.R

/**
 * Implementation of App Widget functionality.
 */
class TodayScheduleWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val widgetText = context.getString(R.string.appwidget_text)

    val intent = Intent(context, ScheduleWidgetService::class.java)
    intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.today_schedule_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
    views.setRemoteAdapter(R.id.lvSchedule, intent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}