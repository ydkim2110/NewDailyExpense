package com.reachfree.dailyexpense.util.extension

import android.content.Context

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-09
 * Time: 오후 5:29
 */
fun Context.getQuantityString(
    quantity: Int,
    pluralResId: Int,
    zeroResId: Int? = null
): String  {
    return if (zeroResId != null && quantity == 0) {
        resources.getString(zeroResId)
    } else {
        resources.getQuantityString(pluralResId, quantity, quantity)
    }
}