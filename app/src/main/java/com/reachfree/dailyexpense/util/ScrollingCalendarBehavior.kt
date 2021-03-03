package com.reachfree.dailyexpense.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오전 9:35
 */
class ScrollingCalendarBehavior(
    context: Context,
    attrs: AttributeSet
) : AppBarLayout.Behavior(context, attrs) {

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        ev: MotionEvent
    ): Boolean {
        return false
    }

}