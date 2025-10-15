package com.waffiq.divenzz.utils

import android.R.anim.fade_in
import android.R.anim.fade_out
import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.waffiq.divenzz.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

object Helpers {

  fun Activity.openDetailPage(eventId : Int) {
    val intent = Intent(this, DetailActivity::class.java).apply {
      putExtra(DetailActivity.EVENT_ID, eventId)
    }
    val options = ActivityOptionsCompat.makeCustomAnimation(this, fade_in, fade_out)
    ActivityCompat.startActivities(this, arrayOf(intent), options.toBundle())
  }

  fun convertToReadableDateTimeCompat(beginTime: String?, endTime: String?): Pair<String, String> {
    if(beginTime == null || endTime == null) {
      return Pair("", "")
    }

    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat("EEEE, MMM d yyyy", Locale.ENGLISH)
    val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)

    val beginDate = inputFormat.parse(beginTime)!!
    val endDate = inputFormat.parse(endTime)!!

    val dateString = dateFormat.format(beginDate)
    val timeRange = "${timeFormat.format(beginDate)} - ${timeFormat.format(endDate)}"

    return Pair(dateString, timeRange)
  }
}
