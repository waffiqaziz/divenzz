package com.waffiq.divenzz.ui.settings

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.waffiq.divenzz.BuildConfig
import com.waffiq.divenzz.utils.Helpers.readableDelay
import com.waffiq.divenzz.utils.Helpers.readableTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

  private const val WORK_NAME = "daily_reminder_work"
  private const val WORK_NAME_DEBUG = "debug_reminder_work"
  private const val TAG = "NotificationScheduler"

  fun scheduleDailyNotification(context: Context) {
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

  fun cancelDailyNotification(context: Context) {
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
