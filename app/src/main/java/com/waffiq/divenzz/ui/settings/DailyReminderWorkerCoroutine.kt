package com.waffiq.divenzz.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.waffiq.divenzz.BuildConfig
import com.waffiq.divenzz.MainActivity
import com.waffiq.divenzz.R.drawable.ic_notification_icon
import com.waffiq.divenzz.core.data.remote.retrofit.EventApiConfig
import com.waffiq.divenzz.utils.Helpers.convertToReadableDateTimeCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyReminderWorkerCoroutine(
  context: Context,
  workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    logDebug("Worker started")

    return withContext(Dispatchers.IO) {
      try {
        logDebug("Fetching events from API")

        val apiService = EventApiConfig.getApiService()
        val response = apiService.getAllEvent(active = 1)

        logDebug("API response received, events count: ${response.listEvents?.size ?: 0}")

        val firstEvent = response.listEvents?.firstOrNull()

        if (firstEvent != null) {
          logDebug("Showing notification for event: ${firstEvent.name}")

          val date = convertToReadableDateTimeCompat(firstEvent.beginTime, firstEvent.endTime)

          showNotification(
            title = firstEvent.name ?: "Event Reminder",
            message = "${date.first} | ${date.second}",
            url = firstEvent.mediaCover ?: ""
          )
          Result.success()
        } else {
          logDebug("No events available, showing generic notification")

          showNotification(
            title = "Daily Reminder",
            message = "Check out today's events!",
            ""
          )

          logDebug("Worker completed successfully")
          Result.success()
        }
      } catch (e: Exception) {
        Log.e(TAG, "Worker failed: ${e.message}", e)
        e.printStackTrace()
        Result.retry()
      }
    }
  }

  private fun showNotification(title: String, message: String, url: String) {
    val notificationManager =
      applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "daily_reminder_channel"
    val channelName = "Daily Reminder"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Channel for daily event reminders"
      }
      notificationManager.createNotificationChannel(channel)
    }

    // intent open app when notification is clicked
    val intent = Intent(applicationContext, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      applicationContext,
      0,
      intent,
      PendingIntent.FLAG_IMMUTABLE
    )

    val myBitmap: Bitmap? = try {
      Glide.with(applicationContext)
        .asBitmap()
        .load(url)
        .submit()
        .get()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }

    val notification = NotificationCompat.Builder(applicationContext, channelId)
      .setSmallIcon(ic_notification_icon)
      .setContentTitle(title)
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .setLargeIcon(myBitmap)
      .setStyle(
        NotificationCompat.BigPictureStyle()
          .bigPicture(myBitmap)
          .bigLargeIcon(null as Bitmap?)
      )
      .build()

    notificationManager.notify(COROUTINE_NOTIFICATION_ID, notification)
    logDebug("Notification displayed")
  }

  private fun logDebug(message: String) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, message)
    }
  }

  companion object {
    private const val TAG = "DailyReminderWorker"
    const val COROUTINE_NOTIFICATION_ID = 234123
  }
}