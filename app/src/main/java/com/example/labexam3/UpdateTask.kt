package com.example.labexam3

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UpdateTask : AppCompatActivity() {
    private lateinit var taskId: String
    private lateinit var taskName: EditText
    private lateinit var taskDate: EditText
    private lateinit var taskTime: EditText
    private lateinit var taskDescription: EditText
    private lateinit var updateButton: Button
    private lateinit var homePg: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //set ids to variables
        taskName = findViewById(R.id.updatetaskNameIn)
        taskDate = findViewById(R.id.updatetaskDateIn)
        taskTime = findViewById(R.id.updatetasktimeIn)
        taskDescription = findViewById(R.id.updatetaskDesIn)

        //set submit button id
        updateButton = findViewById(R.id.updateTaskBtn)

        // Retrieve task details from the intent
        taskId = intent.getStringExtra("task_id") ?: ""
        val name = intent.getStringExtra("task_name")
        val date = intent.getStringExtra("task_date")
        val time = intent.getStringExtra("task_time")
        val description = intent.getStringExtra("task_description")

        // Set the retrieved details to the existing fields
        taskName.setText(name)
        taskDate.setText(date)
        taskTime.setText(time)
        taskDescription.setText(description)

        //call the function with button
        updateButton.setOnClickListener {
            saveUpdatedTask()
        }

        homePg = findViewById(R.id.navHome)
        homePg.setOnClickListener(){
            goHomePage()
        }

    }

    //Function for save updated details
    private fun saveUpdatedTask() {
        val name = taskName.text.toString()
        val date = taskDate.text.toString()
        val time = taskTime.text.toString()
        val description = taskDescription.text.toString()

        if (name.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && description.isNotEmpty()) {
            val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
            val existingTasks = sharedPreferences.getString("tasks_list", "") ?: ""

            // Update the existing task
            val updatedTasks = existingTasks
                .split(";;")
                .map { taskString ->
                    if (taskString.startsWith("$taskId|")) {
                        "$taskId|$name|$date|$time|$description"
                    } else {
                        taskString
                    }
                }
                .joinToString(";;")

            val editor = sharedPreferences.edit()
            editor.putString("tasks_list", updatedTasks)
            editor.apply()

            updateWidget()//update the widget
            goHomePage() //call goHome page to go back
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    //Function to go Homepage
    private fun goHomePage(){
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }

    private fun updateWidget() {
        val intent = Intent(this, TodoWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, TodoWidget::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

}