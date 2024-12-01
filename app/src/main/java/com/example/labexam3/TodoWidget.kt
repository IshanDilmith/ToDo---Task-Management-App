package com.example.labexam3

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class TodoWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val sharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
            val tasksString = sharedPreferences.getString("tasks_list", "")

            // Check if the task list is not empty and split it by ";;"
            val numOfTasks = if (tasksString.isNullOrEmpty()) 0 else tasksString.split(";;").filter { it.isNotEmpty() }.size

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.todo_count, "Remaining Tasks: $numOfTasks")

            // Setup intent to launch the app when widget is clicked
            val intent = Intent(context, HomePage::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.todo_count, pendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

