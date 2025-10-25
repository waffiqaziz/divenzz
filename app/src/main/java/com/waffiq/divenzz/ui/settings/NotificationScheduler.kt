package com.waffiq.divenzz.ui.settings

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.waffiq.divenzz.BuildConfig
import com.waffiq.divenzz.ui.settings.DailyReminderReceiver.Companion.DAILY_REMINDER_RECEIVER_TAG
import com.waffiq.divenzz.ui.settings.DailyReminderWorkerCoroutine.Companion.COROUTINE_NOTIFICATION_ID
import com.waffiq.divenzz.utils.Helpers.readableDelay
import com.waffiq.divenzz.utils.Helpers.readableTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

  private const val WORK_NAME = "daily_reminder_work"
  private const val WORK_NAME_DEBUG = "debug_reminder_work"
  private const val TAG = "NotificationScheduler"

  private const val ALARM_ID = 100

  // Scheduler function
  fun scheduleDailyNotificationAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, DailyReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      ALARM_ID,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val currentTime = Calendar.getInstance()
    logDebug("Current time: ${readableTime(currentTime.timeInMillis)}")

    val targetTime = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 7)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }

    if (currentTime.after(targetTime)) {
      targetTime.add(Calendar.DAY_OF_MONTH, 1)
    }

    val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis
    logDebug("Delay time: ${readableDelay(initialDelay)}")

    // android 12+ (API 31+), requirement request exact alarm permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      if (alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          targetTime.timeInMillis,
          pendingIntent
        )
      } else {
        // fallback to inexact alarm
        alarmManager.setAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          targetTime.timeInMillis,
          pendingIntent
        )
      }
    } else {
      alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        targetTime.timeInMillis,
        pendingIntent
      )
    }

    logDebug("Daily notification scheduled for ${readableTime(targetTime.timeInMillis)}")
  }

  fun cancelDailyNotificationAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, DailyReminderReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      ALARM_ID,
      intent,
      PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )

    if (pendingIntent != null) {
      alarmManager.cancel(pendingIntent)
      pendingIntent.cancel()
      logDebug("Daily notification alarm cancelled")
    }

    // cancel one time worker
    WorkManager.getInstance(context).cancelAllWorkByTag(DAILY_REMINDER_RECEIVER_TAG)
    // WorkManager.getInstance(context).cancelAllWork()
    logDebug("WorkManager tasks cancelled")

    // cancel notification
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(COROUTINE_NOTIFICATION_ID)
    logDebug("Notification dismissed")
  }

  fun scheduleDailyNotificationWorker(context: Context) {
    logDebug("Scheduling daily notification at 7:00 AM")

    val currentTime = Calendar.getInstance()
    logDebug("Current time: ${readableTime(currentTime.timeInMillis)}")

    val targetTime = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 7)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
    }

    // if 7 AM already passed, schedule for tomorrow
    if (currentTime.after(targetTime)) {
      targetTime.add(Calendar.DAY_OF_MONTH, 1)
    }

    val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis
    logDebug("Delay time: ${readableDelay(initialDelay)}")

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
      1, TimeUnit.DAYS
    )
      .setConstraints(constraints)
      .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
      .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      WORK_NAME,
      ExistingPeriodicWorkPolicy.KEEP,
      dailyWorkRequest
    )
    logDebug("Daily notification scheduled")
  }

  fun cancelDailyNotificationWorker(context: Context) {
    logDebug("Cancelling daily notification")
    WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
  }

  fun scheduleOneTimeNotification(context: Context) {
    logDebug("Scheduling one-time notification (DEBUG)")

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<DailyReminderWorkerCoroutine>()
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
      "one_time_reminder",
      ExistingWorkPolicy.REPLACE,
      oneTimeWorkRequest
    )

    logDebug("One-time notification scheduled")
  }

  fun scheduleDebugPeriodicNotification(context: Context) {
    logDebug("Scheduling debug periodic notification (every 15 min)")

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorkerCoroutine>(
      15, TimeUnit.MINUTES
    )
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      WORK_NAME_DEBUG,
      ExistingPeriodicWorkPolicy.REPLACE,
      periodicWorkRequest
    )

    logDebug("Debug periodic notification scheduled")
  }

  fun cancelDebugPeriodicNotification(context: Context) {
    logDebug("Cancelling debug periodic notification")
    WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DEBUG)
  }

  private fun logDebug(message: String) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, message)
    }
  }
}
