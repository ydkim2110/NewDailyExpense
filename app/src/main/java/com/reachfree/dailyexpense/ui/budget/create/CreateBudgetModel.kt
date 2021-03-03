package com.reachfree.dailyexpense.ui.budget.create

import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.util.Constants
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-02
 * Time: 오후 11:11
 */
data class CreateBudgetModel(
    var category: Category? = null,
    var budgetedAmount: BigDecimal? = null
)