package dev.imranr.obtainium

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import dev.imranr.obtainium.data.AppDatabase
import dev.imranr.obtainium.util.NotificationHelper

class ObtainiumApplication : Application(), Configuration.Provider {
    
    lateinit var database: AppDatabase
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize database
        database = AppDatabase.getInstance(this)
        
        // Initialize WorkManager
        WorkManager.initialize(this, workManagerConfiguration)
        
        // Create notification channels
        createNotificationChannels()
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // Update notification channel
            val updateChannel = NotificationChannel(
                NotificationHelper.CHANNEL_ID_UPDATES,
                "App Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for app updates"
            }
            notificationManager.createNotificationChannel(updateChannel)
            
            // Download notification channel
            val downloadChannel = NotificationChannel(
                NotificationHelper.CHANNEL_ID_DOWNLOADS,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for app downloads"
            }
            notificationManager.createNotificationChannel(downloadChannel)
            
            // Background check channel
            val backgroundChannel = NotificationChannel(
                NotificationHelper.CHANNEL_ID_BACKGROUND,
                "Background Checks",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for background update checks"
            }
            notificationManager.createNotificationChannel(backgroundChannel)
        }
    }
    
    companion object {
        lateinit var instance: ObtainiumApplication
            private set
    }
}
