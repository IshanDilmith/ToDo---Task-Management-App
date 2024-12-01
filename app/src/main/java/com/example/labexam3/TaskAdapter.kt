package com.example.labexam3

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private val tasks: MutableList<TaskDetail>,
    private val onItemClick: (TaskDetail)-> Unit,
    private val onDeleteClick: (TaskDetail) -> Unit,
    private val onStartClick: (TaskDetail) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameText)
        private val taskDateTextView: TextView = itemView.findViewById(R.id.taskDateText)
        private val taskTimeTextView: TextView = itemView.findViewById(R.id.taskTimeText)
        private val taskDescriptionTextView: TextView = itemView.findViewById(R.id.taskDescriptionText)

        //action buttons
        private val updateButton: Button = itemView.findViewById(R.id.updateBtn)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteBtn)
        private val startButton: Button = itemView.findViewById(R.id.startTBtn)

        fun bind(task: TaskDetail) {
            taskNameTextView.text = task.name
            taskDateTextView.text = task.date
            taskTimeTextView.text = task.time
            taskDescriptionTextView.text = task.description

            startButton.setOnClickListener {
                onStartClick(task) // Trigger the lambda for start action
            }

            updateButton.setOnClickListener {
                onItemClick(task) // Trigger the lambda on button click
            }

            deleteButton.setOnClickListener {
                onDeleteClick(task) // Trigger the lambda for delete action
            }
        }
    }

    //function for delete tasks
    fun removeTask(task: TaskDetail) {
        tasks.remove(task)
        notifyDataSetChanged() // Refresh the RecyclerView to reflect the change


        // Save updated task list to SharedPreferences
        val sharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val updatedTasks = tasks.joinToString(";;") {
            "${it.id}|${it.name}|${it.date}|${it.time}|${it.description}"
        }
        val editor = sharedPreferences.edit()
        editor.putString("tasks_list", updatedTasks)
        editor.apply()

        updateWidget() //update widget

        // Display a toast message
        Toast.makeText(context, "Task is removed", Toast.LENGTH_SHORT).show()
    }

    private fun updateWidget() {
        val intent = Intent(context, TodoWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(context, TodoWidget::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }




}