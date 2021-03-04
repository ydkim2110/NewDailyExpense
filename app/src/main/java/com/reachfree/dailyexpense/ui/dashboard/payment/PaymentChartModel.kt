package com.reachfree.dailyexpense.ui.dashboard.payment

import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-04
 * Time: 오전 11:55
 */
data class PaymentChartModel(
    var date: String = "",
    var amount: BigDecimal? = null,
    var count: Int = 0,
    var payment: Int = 0
)