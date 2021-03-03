package com.reachfree.dailyexpense.data.model

import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:58
 */
data class ExpenseBySubCategory(
    var subCategoryId: String = "",
    var sumBySubCategory: BigDecimal? = null,
    var countBySubCategory: Int = 0
)
