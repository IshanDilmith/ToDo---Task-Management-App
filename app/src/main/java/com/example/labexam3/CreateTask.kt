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
import java.util.UUID

class CreateTask : AppCompatActivity() {

    //page interaction variables
    private lateinit var homePg: ImageButton;
    private lateinit var addTaskpg: ImageButton;

    //task variables
    private lateinit var taskName: EditText
    private lateinit var taskDate: EditText
    private lateinit var taskTime: EditText
    private lateinit var taskDescription: EditText
    private lateinit var createTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //get Home ID
        homePg = findViewById(R.id.navHome)
        homePg.setOnClickListener(){
            goHomepg()
        }

        //get TaskAdd ID
        addTaskpg = findViewById(R.id.navPlus)
        addTaskpg.setOnClickListener(){
            goAddtask()
        }

        //get ids for each input field
        taskName = findViewById(R.id.taskNameIn)
        taskDate = findViewById(R.id.taskDateIn)
        taskTime = findViewById(R.id.tasktimeIn)
        taskDescription = findViewById(R.id.taskDesIn)
        createTaskButton = findViewById(R.id.createTaskBtn)

        //call saveTask with button id
        createTaskButton.setOnClickListener {
            saveTask()
        }

    }

    //start intent to go homepage
    private fun goHomepg(){
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }

    //start intent to go add task
    private fun goAddtask(){
        val intent = Intent(this, CreateTask::class.java)
        startActivity(intent)
    }

    //function for save task
    private fun saveTask() {
        val name = taskName.text.toString()
        val date = taskDate.text.toString()
        val time = taskTime.text.toString()
        val description = taskDescription.text.toString()

        if (name.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && description.isNotEmpty()) {
            val taskId = UUID.randomUUID().toString() // Generate unique ID
            val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // Load existing tasks
            val existingTasks = sharedPreferences.getString("tasks_list", "") ?: ""
            val newTaskString = "$taskId|$name|$date|$time|$description"
            val updatedTasks = if (existingTasks.isNotEmpty()) "$existingTasks;;$newTaskString" else newTaskString

            // Save the updated list of tasks
            editor.putString("tasks_list", updatedTasks)
            editor.apply()

            updateWidget() //update the widget

            // Go back to the home page after saving
            startActivity(Intent(this, HomePage::class.java))
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
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