package com.example.fitsync.steps

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.fitsync.MainActivity
import com.example.fitsync.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service class for counting steps. It listens to the step detector sensor and
 * updates the step count accordingly.
 */
@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var stepCounterRepository: StepCounterRepository

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    override fun onBind(intent: Intent): IBinder? { return null }

    /**
     * Initializes the service, sets up the step counter repository and sensor manager.
     * Registers a listener for the step detector sensor if available.
     */
    override fun onCreate() {
        super.onCreate()

        // initializes step counter and sets the value for total steps
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepSensor != null) {
            Log.d("StepCounterService", "Step detector sensor is available")
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            Log.d("StepCounterService", "Step detector sensor is not available")
        }
    }

    /**
     * Handles the start command for the service. Creates a notification channel
     * and starts the service in the foreground with a notification.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    /**
     * Called when the step detector sensor reports a new step.
     * Increments the step count in the repository.
     * @param event The SensorEvent.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            // Increment the step count
            stepCounterRepository.incrementSteps()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    /**
     * Creates a notification channel for displaying foreground service notifications.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    /**
     * Creates a notification for the foreground service.
     * @return The created Notification instance.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Step Counter Running")
            .setContentText("Counting your steps...")
            .setSmallIcon(R.drawable.footsteps)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        stepCounterRepository.saveCurrentSteps()
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    companion object {
        const val CHANNEL_ID = "StepCounterServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}
