package com.reachfree.dailyexpense.data.model

import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:58
 */
data class ExpenseByCategoryWithBudget(
    var categoryId: String = "",
    var sumByCategory: BigDecimal? = null,
    var countByCategory: Int = 0,
    var budgetAmount: BigDecimal? = null,
)
