package com.reachfree.dailyexpense.data.model

import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 1:28
 */
data class Category(
    var id: String = "",
    var visibleNameResId: Int = 0,
    var iconResId: Int = 0,
    var backgroundColor: Int = 0
)
