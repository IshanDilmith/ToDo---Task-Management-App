package com.example.labexam3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActionPage : AppCompatActivity() {

    private lateinit var goBackBtn:ImageView
    private lateinit var timerTextView: TextView
    lateinit var startButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var resetButton: ImageButton
    private lateinit var countDownTimer: CountDownTimer
    var timeLeftInMillis: Long = 0 // Remaining time
    var initialTimeInMillis: Long = 0 // To store the initial time

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_action_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Get task details from intent
        val taskName = intent.getStringExtra("task_name")
        val taskDate = intent.getStringExtra("task_date")
        val taskTime = intent.getStringExtra("task_time")
        val taskDescription = intent.getStringExtra("task_description")

        // Find TextViews and set the task details
        val taskNameTextView: TextView = findViewById(R.id.taskName)
        val taskDateTextView: TextView = findViewById(R.id.taskDate)
        val taskTimeTextView: TextView = findViewById(R.id.taskTime)
        val taskDescriptionTextView: TextView = findViewById(R.id.taskDescription)

        //assigning values to ids
        taskNameTextView.text = taskName
        taskDateTextView.text = taskDate
        taskTimeTextView.text = taskTime
        taskDescriptionTextView.text = taskDescription

        //get button id
        goBackBtn = findViewById(R.id.goBack)

        //set ids for timer
        timerTextView = findViewById(R.id.timer_text_view)
        startButton = findViewById(R.id.timerStart)
        stopButton = findViewById(R.id.timerStop)
        resetButton = findViewById(R.id.timerReset)

        // Set up the start button click listener
        startButton.setOnClickListener {
            val expectTime = taskTime.toString()

            // Validate the time input format
            if (expectTime.matches(Regex("^\\d{2}:\\d{2}:\\d{2}$"))) {
                val timeParts = expectTime.split(":")
                val hours = timeParts[0].toInt()
                val minutes = timeParts[1].toInt()
                val seconds = timeParts[2].toInt()

                initialTimeInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L // Convert to milliseconds
                timeLeftInMillis = initialTimeInMillis // Set remaining time to initial time
                startTimer()
                startButton.isEnabled = false //disable start button when start
            } else {
                Toast.makeText(this, "Please enter time in HH:MM:SS format", Toast.LENGTH_SHORT).show()
            }
        }

        //back button functionality
        goBackBtn.setOnClickListener {
            goBackHome()
        }

        // Stop button functionality
        stopButton.setOnClickListener {
            stopTimer()
            startButton.isEnabled = true //enable start button again
        }

        // Reset button functionality
        resetButton.setOnClickListener {
            resetTimer()
            startButton.isEnabled = true //enable start button again
        }

    }

    // Function to go back home
    private fun goBackHome() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }

    // Function to send a notification
    private fun sendNotification(title: String, content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if device is running Android 8.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channelName = "Default Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)

            // Register the channel with the system
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        //give different notification ids
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    // Function to start the timer
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            private var quarterNotified = false
            private var halfNotified = false

            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()

                // Define thresholds
                val quarterTime = initialTimeInMillis / 4
                val halfTime = initialTimeInMillis / 2

                // Notify at last quarter of the time
                if (!quarterNotified && millisUntilFinished <= quarterTime) {
                    sendNotification("Quarter Time Remaining", "Only one-quarter of the countdown time is left.")
                    quarterNotified = true
                }

                // Notify at half of the time
                if (!halfNotified && millisUntilFinished <= halfTime) {
                    sendNotification("Half Time Remaining", "Only half of the countdown time is left!")
                    halfNotified = true
                }
            }

            override fun onFinish() {
                vibrateDevice()
                Toast.makeText(this@ActionPage, "Time is up!", Toast.LENGTH_SHORT).show()
                sendNotification("Time is Over!", "The timer has ended. Please complete the necessary actions.")
                startButton.isEnabled = true
            }
        }.start()
    }

    // Function to stop the timer
    private fun stopTimer() {
        countDownTimer.cancel() // Cancel the timer
        Toast.makeText(this, "Timer Stopped", Toast.LENGTH_SHORT).show()
    }

    // Function to reset the timer
    private fun resetTimer() {
        countDownTimer.cancel() // Cancel the current running timer
        timeLeftInMillis = initialTimeInMillis // Reset to initial time
        updateTimerText() // Update the TextView with the initial time
        Toast.makeText(this, "Timer Reset", Toast.LENGTH_SHORT).show()
    }

    // Function to update the timer display
    private fun updateTimerText() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        timerTextView.text = timeFormatted
    }

    //Function to vibrate
    private fun vibrateDevice() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // vibration pattern
        val vibrationPattern = longArrayOf(0, 200, 100, 200, 300, 400) // Example pattern

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // For Android 8.0 and above
            val vibrationEffect = VibrationEffect.createWaveform(vibrationPattern, -1) // -1 means no repeat
            vibrator.vibrate(vibrationEffect)
        } else {
            // For below Android 8.0
            vibrator.vibrate(vibrationPattern, -1) // -1 means no repeat
        }
    }


}