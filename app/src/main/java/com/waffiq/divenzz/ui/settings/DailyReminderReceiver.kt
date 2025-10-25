package com.waffiq.divenzz.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.waffiq.divenzz.ui.settings.NotificationScheduler.scheduleDailyNotificationAlarm

class DailyReminderReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Log.i("DailyReminderReceiver", "Alarm triggered, starting worker")

    val workRequest = OneTimeWorkRequestBuilder<DailyReminderWorkerCoroutine>()
      .addTag(DAILY_REMINDER_RECEIVER_TAG)
      .build()

    WorkManager.getInstance(context).enqueue(workRequest)

    // reschedule for tomorrow
    scheduleDailyNotificationAlarm(context)
  }

  companion object{
    const val DAILY_REMINDER_RECEIVER_TAG = "DailyReminderWorkerCoroutines"
  }
}
