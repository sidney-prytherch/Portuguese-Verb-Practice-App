package com.sid.app.verbpractice

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.new_app_widget
            ).apply {
                setOnClickPendingIntent(R.id.buttonButton, pendingIntent)
            }

            val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
            val previousYear = sharedPrefs.getInt("prev_year", -1)
            val previousDay = sharedPrefs.getInt("prev_day", -1)
            val count = if (previousDay != currentDay || previousYear != currentYear) {
                0
            } else {
                sharedPrefs.getInt("used_count", 0)
            }

            val string = "You've practiced verbs " + count.toString() + " time" + (if (count == 1) "" else "s") + " today"
            views.setTextViewText(R.id.countDisplay, string)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}