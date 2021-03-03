package com.reachfree.dailyexpense.data.model

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 2:03
 */
data class SubCategory(
    var id: String = "",
    var visibleNameResId: Int = 0,
    var iconResId: Int = 0,
    var backgroundColor: Int = 0,
    var categoryId: String = ""
)
