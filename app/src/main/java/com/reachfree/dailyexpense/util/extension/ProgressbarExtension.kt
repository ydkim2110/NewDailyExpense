package com.reachfree.dailyexpense.util.extension

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.core.content.ContextCompat

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-09
 * Time: 오후 3:18
 */
fun ProgressBar.changeTintColor(color: Int) {
    progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
}

fun ProgressBar.animateProgressbar(percentage: Int) {
    ObjectAnimator.ofInt(this, "progress", 0, percentage).apply {
        duration = 1000
        interpolator = DecelerateInterpolator()
        start()
    }
}