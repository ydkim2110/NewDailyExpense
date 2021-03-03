package com.reachfree.dailyexpense.data.response

import com.reachfree.dailyexpense.data.model.ExpenseByCategoryWithBudget
import com.reachfree.dailyexpense.util.Constants.Status

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:44
 */
class ExpenseByCategoryWithBudgetResponse(
    val status: Status,
    val data: List<ExpenseByCategoryWithBudget>?,
    val error: String?
) {

    companion object {
        fun loading(): ExpenseByCategoryWithBudgetResponse = ExpenseByCategoryWithBudgetResponse(Status.LOADING, null, null)

        fun success(expense: List<ExpenseByCategoryWithBudget>): ExpenseByCategoryWithBudgetResponse =
            ExpenseByCategoryWithBudgetResponse(Status.SUCCESS, expense, null)

        fun error(error: String): ExpenseByCategoryWithBudgetResponse =
            ExpenseByCategoryWithBudgetResponse(Status.ERROR, null, error)
    }

}