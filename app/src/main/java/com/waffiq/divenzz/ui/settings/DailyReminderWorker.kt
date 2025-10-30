package com.waffiq.divenzz.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import com.waffiq.divenzz.BuildConfig
import com.waffiq.divenzz.BuildConfig.BASE_URL
import com.waffiq.divenzz.MainActivity
import com.waffiq.divenzz.R.drawable.ic_notification_icon
import com.waffiq.divenzz.utils.Helpers.convertToReadableDateTimeCompat
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DailyReminderWorker(
  context: Context,
  workerParams: WorkerParameters,
) : Worker(context, workerParams) {

  override fun doWork(): Result {
    logDebug("Worker started")

    return try {
      logDebug("Fetching events from API")

      // Prepare looper for HTTP client callbacks
      if (Looper.myLooper() == null) {
        Looper.prepare()
      }

      val client = SyncHttpClient()
      val url = BASE_URL + "events?active=-1&limit=1"

      var result: Result = Result.failure()

      client.get(url, object : AsyncHttpResponseHandler() {
        override fun onSuccess(
          statusCode: Int,
          headers: Array<Header?>?,
          responseBody: ByteArray,
        ) {
          try {
            val responseString = String(responseBody)
            logDebug("API response received: $responseString")

            val jsonResponse = JSONObject(responseString)
            val eventsArray = jsonResponse.getJSONArray("listEvents")

            if (eventsArray.length() > 0) {
              val firstEvent = eventsArray.getJSONObject(0)
              val eventName = firstEvent.getString("name")
              val dateBegin = firstEvent.getString("beginTime")
              val dateEnd = firstEvent.getString("endTime")
              val date = convertToReadableDateTimeCompat(dateBegin, dateEnd)

              logDebug("Showing notification for event: $eventName")

              showNotification(
                title = eventName, message = "${date.first} | ${date.second}"
              )

              result = Result.success()
            } else {
              logDebug("No events available, showing generic notification")

              showNotification(
                title = "Daily Reminder", message = "Check out today's events!"
              )

              result = Result.success()
            }

            logDebug("Worker completed successfully")

          } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message}", e)
            showNotification(
              title = "Error", message = "Failed to parse event data"
            )
            result = Result.failure()
          }
        }

        override fun onFailure(
          statusCode: Int,
          headers: Array<Header?>?,
          responseBody: ByteArray?,
          error: Throwable,
        ) {
          Log.e(TAG, "API call failed: ${error.message}", error)

          // Show generic notification even on failure
          showNotification(
            title = "Daily Reminder", message = "Check out today's events!"
          )

          result = Result.retry()
        }
      })

      // Wait for the async callback to complete
      Looper.loop()

      result

    } catch (e: Exception) {
      Log.e(TAG, "Worker failed: ${e.message}", e)

      showNotification(
        title = "Daily Reminder", message = "Check out today's events!"
      )

      Result.failure()
    }
  }

  private fun showNotification(title: String, message: String) {
    val notificationManager =
      applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "daily_reminder_channel"
    val channelName = "Daily Reminder"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Channel for daily event reminders"
      }
      notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(applicationContext, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val notification =
      NotificationCompat.Builder(applicationContext, channelId).setSmallIcon(ic_notification_icon)
        .setContentTitle(title).setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
        .setAutoCancel(true).build()

    notificationManager.notify(NOTIFICATION_ID, notification)
    logDebug("Notification displayed")
  }

  private fun logDebug(message: String) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, message)
    }
  }

  companion object {
    private const val TAG = "DailyReminderWorker"
    private const val NOTIFICATION_ID = 234123
  }
}
