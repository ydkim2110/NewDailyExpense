package com.reachfree.dailyexpense.util

import android.os.SystemClock
import android.view.View

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 1:58
 */
class OnSingleClickListener(
    private var interval: Int = 600,
    private var onSingleClick: (View) -> Unit
) : View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(view: View) {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        if ((elapsedRealtime - lastClickTime) < interval) {
            return
        }
        lastClickTime = elapsedRealtime
        onSingleClick(view)
    }
}