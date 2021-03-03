package com.reachfree.dailyexpense.util.extension

import android.view.View
import com.reachfree.dailyexpense.util.OnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 1:59
 */
fun View.setOnSingleClickListener(action: (View) -> Unit) {
    val onClick = OnSingleClickListener {
        action(it)
    }
    setOnClickListener(onClick)
}