package com.example.labexam3

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomePage : AppCompatActivity() {

    //variables for page interaction
    private lateinit var homePg: ImageButton;
    private lateinit var addTaskpg: ImageButton;

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasks: MutableList<TaskDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //get homepage id
        homePg = findViewById(R.id.navHome)
        homePg.setOnClickListener(){
            goHomepg()
        }

        //get add task page id
        addTaskpg = findViewById(R.id.navPlus)
        addTaskpg.setOnClickListener(){
            goAddtask()
        }

        //get recycler view id
        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load tasks from SharedPreferences
        tasks = loadTasksFromSharedPreferences()
        taskAdapter = TaskAdapter(this, tasks, { task ->
            val intent = Intent(this, UpdateTask::class.java).apply {
                putExtra("task_id", task.id)
                putExtra("task_name", task.name)
                putExtra("task_date", task.date)
                putExtra("task_time", task.time)
                putExtra("task_description", task.description)
            }
            startActivity(intent)
        }, { task ->
            taskAdapter.removeTask(task)
        }, { task ->
            val intent = Intent(this, ActionPage::class.java).apply {
                putExtra("task_id", task.id)
                putExtra("task_name", task.name)
                putExtra("task_date", task.date)
                putExtra("task_time", task.time)
                putExtra("task_description", task.description)
            }
            startActivity(intent)
        })


        recyclerView.adapter = taskAdapter

    }

    //intent for home page interaction
    private fun goHomepg(){
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }

    //intent for add task page interaction
    private fun goAddtask(){
        val intent = Intent(this, CreateTask::class.java)
        startActivity(intent)
    }

    //Function for load all tasks
    private fun loadTasksFromSharedPreferences(): MutableList<TaskDetail> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val taskList = mutableListOf<TaskDetail>()

        val tasksString = sharedPreferences.getString("tasks_list", "")
        if (!tasksString.isNullOrEmpty()) {
            val tasksArray = tasksString.split(";;") // Split the string based on your delimiter
            for (taskString in tasksArray) {
                val taskDetails = taskString.split("|")
                if (taskDetails.size == 5) {
                    val task = TaskDetail(
                        id = taskDetails[0],
                        name = taskDetails[1],
                        date = taskDetails[2],
                        time = taskDetails[3],
                        description = taskDetails[4]
                    )
                    taskList.add(task)
                }
            }
        }
        return taskList
    }

}