package com.reachfree.dailyexpense.ui.transaction

import com.reachfree.dailyexpense.data.model.TransactionEntity

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오후 8:12
 */
data class TransactionGroup(
    var key: String = "",
    var transactionList: ArrayList<TransactionEntity> = ArrayList()
)
