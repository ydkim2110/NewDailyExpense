package com.reachfree.dailyexpense.data.response

import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.Status

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:44
 */
class ExpenseBudgetResponse(
    val status: Status,
    val data: ExpenseBudgetEntity?,
    val error: String?
) {

    companion object {
        fun loading(): ExpenseBudgetResponse = ExpenseBudgetResponse(Status.LOADING, null, null)

        fun success(expenseBudget: ExpenseBudgetEntity): ExpenseBudgetResponse =
            ExpenseBudgetResponse(Status.SUCCESS, expenseBudget, null)

        fun error(error: String): ExpenseBudgetResponse =
            ExpenseBudgetResponse(Status.ERROR, null, error)
    }

}