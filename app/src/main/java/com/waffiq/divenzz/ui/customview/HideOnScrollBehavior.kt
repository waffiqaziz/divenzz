package com.waffiq.divenzz.ui.customview


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat

/**
 * A custom behavior that hides a view when scrolling down and shows it when scrolling up.
 */
class HideOnScrollBehavior<V : View>(
  context: Context,
  attrs: AttributeSet
) : CoordinatorLayout.Behavior<V>(context, attrs) {

  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: V,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ): Boolean {
    return axes == ViewCompat.SCROLL_AXIS_VERTICAL
  }

  private var isHiding = false
  private var isShowing = false

  override fun onNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: V,
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
    consumed: IntArray
  ) {
    super.onNestedScroll(
      coordinatorLayout, child, target,
      dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed
    )

    if (dyConsumed > 0 && !isHiding && child.translationY == 0f) {
      // scroll down to hide
      isHiding = true
      child.animate()
        .translationY(child.height.toFloat() + 180f)
        .setDuration(150)
        .withEndAction {
          isHiding = false
        }
        .setInterpolator(AnimationUtils.loadInterpolator(child.context, android.R.interpolator.fast_out_slow_in))
        .start()

    } else if (dyConsumed < 0 && !isShowing && child.translationY > 0f) {
      // scroll up to show
      isShowing = true
      child.animate()
        .translationY(0f)
        .setDuration(150)
        .withEndAction {
          isShowing = false
        }
        .start()
    }
  }
}
