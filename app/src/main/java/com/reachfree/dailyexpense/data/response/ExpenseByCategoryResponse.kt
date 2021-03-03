package com.reachfree.dailyexpense.data.response

import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.Status

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:44
 */
class ExpenseByCategoryResponse(
    val status: Status,
    val data: List<ExpenseByCategory>?,
    val error: String?
) {

    companion object {
        fun loading(): ExpenseByCategoryResponse = ExpenseByCategoryResponse(Status.LOADING, null, null)

        fun success(transaction: List<ExpenseByCategory>): ExpenseByCategoryResponse =
            ExpenseByCategoryResponse(Status.SUCCESS, transaction, null)

        fun error(error: String): ExpenseByCategoryResponse =
            ExpenseByCategoryResponse(Status.ERROR, null, error)
    }

}