package com.reachfree.dailyexpense.ui.dashboard.total

import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-04
 * Time: 오전 11:55
 */
data class TotalAmountChartModel(
    var date: String = "",
    var amount: BigDecimal? = null,
    var count: Int = 0,
    var type: Int = 0
)