package com.waffiq.divenzz.utils

import android.R.anim.fade_in
import android.R.anim.fade_out
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.CONSUMED
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updateLayoutParams
import com.google.android.material.appbar.AppBarLayout
import com.waffiq.divenzz.ui.detail.DetailActivity
import com.waffiq.divenzz.ui.favorite.FavoriteActivity
import java.text.SimpleDateFormat
import java.util.Locale

object Helpers {

  /**
   * Open [DetailActivity] with a fade-in and fade-out animation.
   *
   * @param eventId The ID of the event to be displayed in the detail page.
   */
  fun Activity.openDetailPage(eventId: Int) {
    val intent = Intent(this, DetailActivity::class.java).apply {
      putExtra(DetailActivity.EVENT_ID, eventId)
    }
    val options = ActivityOptionsCompat.makeCustomAnimation(this, fade_in, fade_out)
    ActivityCompat.startActivities(this, arrayOf(intent), options.toBundle())
  }

  /**
   * Open [FavoriteActivity] with a fade-in and fade-out animation.
   */
  fun Activity.openFavoritePage() {
    val intent = Intent(this, FavoriteActivity::class.java)
    val options = ActivityOptionsCompat.makeCustomAnimation(this, fade_in, fade_out)
    ActivityCompat.startActivities(this, arrayOf(intent), options.toBundle())
  }

  /**
   * Converts begin and end time strings into a more readable date and time range format.
   *
   * @param beginTime The start time in "yyyy-MM-dd HH:mm:ss" format.
   * @param endTime The end time in "yyyy-MM-dd HH:mm:ss" format.
   * @return A Pair containing the formatted date string and time range string.
   */
  fun convertToReadableDateTimeCompat(beginTime: String?, endTime: String?): Pair<String, String> {
    if (beginTime == null || endTime == null) {
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

  /**
   * Adjusts the AppBarLayout to handle system insets, ensuring proper display on different device
   * configurations.
   *
   * @param margin An optional margin to be added to the left and right of the AppBarLayout.
   */
  fun AppBarLayout.handleOverHeightAppBar(margin: Int = 0) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
      val insets = windowInsets.getInsets(Type.systemBars())
      v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = insets.top
        leftMargin = insets.left + margin
        rightMargin = insets.right + margin
      }
      CONSUMED
    }
  }

  /**
   * Checks if the current theme is set to dark mode.
   */
  val Activity.isDarkTheme: Boolean
    get() = this.resources.configuration.uiMode and
      Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
