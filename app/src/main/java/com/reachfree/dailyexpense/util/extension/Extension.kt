package com.reachfree.dailyexpense.util.extension

import android.os.Handler
import android.os.Looper

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오후 6:06
 */
fun runDelayed(delayMillis: Long = 0, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(action, delayMillis)
}