package com.waffiq.divenzz.utils

import android.R.anim.fade_in
import android.R.anim.fade_out
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.CONSUMED
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.appbar.AppBarLayout
import com.waffiq.divenzz.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

object Helpers {

  /**
   * Opens the DetailActivity with a fade-in and fade-out animation.
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
      val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = insets.top
        leftMargin = insets.left + margin
        rightMargin = insets.right + margin
      }
      CONSUMED
    }
  }

  fun Context.setStatusBarColor(
    window: Window,
    toColor: Int,
  ) {

    // set status bar color based on API level
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      // For Android 14 and below
      @Suppress("DEPRECATION")
      window.statusBarColor = toColor
    } else { // Android 15 and up
      val insetsController = WindowInsetsControllerCompat(window, window.decorView)
      insetsController.isAppearanceLightStatusBars = isLightColor(toColor)

      // add a colored background to the status bar area
      val rootView = window.decorView as ViewGroup
      val statusBarBackgroundView = View(this).apply {
        setBackgroundColor(toColor)
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          window.getStatusBarHeight()
        )
      }

      // remove existing status bar background if present
      val existingBackground = rootView.findViewWithTag<View>("statusBarBackground")
      if (existingBackground != null) rootView.removeView(existingBackground)

      // add the new status bar background view
      statusBarBackgroundView.tag = "statusBarBackground"
      rootView.addView(statusBarBackgroundView)
    }
  }

  fun isLightColor(color: Int): Boolean {
    val darkness = 1 - (
      0.299 * ((color shr 16 and 0xFF) / 255.0) +
        0.587 * ((color shr 8 and 0xFF) / 255.0) +
        0.114 * ((color and 0xFF) / 255.0)
      )
    return darkness < 0.5
  }

  // helper to get the status bar height
  fun Window.getStatusBarHeight(): Int {
    val insets = ViewCompat.getRootWindowInsets(decorView) ?: return 0
    return insets.getInsets(Type.statusBars()).top
  }

  @ColorInt
  fun Context.getThemeColor(attrResId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrResId, typedValue, true)
    return typedValue.data
  }
}
